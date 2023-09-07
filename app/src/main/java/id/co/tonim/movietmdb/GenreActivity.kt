package id.co.tonim.movietmdb

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import id.co.tonim.movietmdb.adapter.MovieAdapter
import id.co.tonim.movietmdb.data.Genre
import id.co.tonim.movietmdb.data.MovieList
import id.co.tonim.movietmdb.databinding.ActivityGenreBinding
import id.co.tonim.movietmdb.utils.HPI
import id.co.tonim.movietmdb.utils.LoadingDialog
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class GenreActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var binding: ActivityGenreBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var genreList: Genre
    var id: Int? =null
    private val handler = Handler()
    private lateinit var loading: LoadingDialog

    companion object {
        lateinit var movieList: MutableList<MovieList>
        const val EXTRA_GENRE = "extra_genre"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityGenreBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.back.setOnClickListener(View.OnClickListener { onBackPressed() })

        genreList = intent.getParcelableExtra<Genre>(GenreActivity.EXTRA_GENRE) as Genre
        binding.tittle.text = genreList.name
        id = genreList.id

        recyclerView = binding.recyclerView
        val layoutManager = GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        movieList = mutableListOf()
        movieAdapter = MovieAdapter(movieList)
        recyclerView.adapter = movieAdapter


        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            fetchMovieData()
        }

        loading = LoadingDialog(this)
        loading.startLoading()

        checkInternet()
        handler.postDelayed({
            checkInternet()
        }, 5000L)

    }

    private fun checkInternet() {
        if (isConnectedToNetwork()) {
            fetchMovieData()
        }else{
            intentNoConneect()
        }
    }

    private fun intentNoConneect() {
        val intent = Intent(this, NoConnectedActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun isConnectedToNetwork(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        var isConnected = false

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            isConnected =
                capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
        } else {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            isConnected = networkInfo?.isConnected ?: false
        }

        return isConnected
    }

    private fun fetchMovieData() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("${HPI.API_URL}/discover/movie?with_genres=${id}")
            .get()
            .addHeader("accept", "application/json")
            .addHeader(
                "Authorization",
                "Bearer ${HPI.TOKEN}"
            )
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    loading.isDismiss()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData = response.body?.string()
                    responseData?.let {
                        val jsonObject = JSONObject(it)
                        val jsonArray = jsonObject.getJSONArray("results")

                        for (i in 0 until jsonArray.length()) {
                            val movieObject = jsonArray.getJSONObject(i)
                            val name = movieObject.getString("original_title")
                            val images = movieObject.getString("poster_path")
                            val release = movieObject.getString("release_date")
                            val descripsi = movieObject.getString("overview")
                            val rating = movieObject.getDouble("vote_average")
                            val movie = MovieList(name, images, release, descripsi, rating)
                            movieList.add(movie)
                        }

                        runOnUiThread {
                            movieAdapter.notifyDataSetChanged()
                            swipeRefreshLayout.isRefreshing = false
                            loading.isDismiss()
                        }
                    } ?: run {
                        runOnUiThread {
                            swipeRefreshLayout.isRefreshing = false
                            loading.isDismiss()
                        }
                    }
                }
            }
        })
    }
}
