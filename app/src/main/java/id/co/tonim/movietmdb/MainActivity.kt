package id.co.tonim.movietmdb

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import id.co.tonim.movietmdb.adapter.GenreAdapter
import id.co.tonim.movietmdb.adapter.MovieAdapter
import id.co.tonim.movietmdb.data.Genre
import id.co.tonim.movietmdb.data.MovieList
import id.co.tonim.movietmdb.databinding.ActivityMainBinding
import id.co.tonim.movietmdb.utils.HPI
import id.co.tonim.movietmdb.utils.LoadingDialog
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var genrerecylerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var genreAdapter: GenreAdapter
    private lateinit var binding: ActivityMainBinding
    lateinit var searchView: SearchView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var loading: LoadingDialog
    private val handler = Handler()

    companion object {
        lateinit var movieList: MutableList<MovieList>
        lateinit var genreList: MutableList<Genre>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        recyclerView = binding.recyclerView
        val layoutManager = GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        movieList = mutableListOf()
        movieAdapter = MovieAdapter(movieList)
        recyclerView.adapter = movieAdapter

        binding.favorit.setOnClickListener {
            startActivity(Intent(this, FavoriteMovieListActivity::class.java))
            finish()
        }

        genrerecylerView = binding.genrerecyclerView
        genrerecylerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        genreList = mutableListOf()
        genreAdapter = GenreAdapter(genreList)
        genrerecylerView.adapter = genreAdapter

        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            fetchMovieData()
            fectGenreMovie()
        }

        loading = LoadingDialog(this)
        loading.startLoading()

        searchMovie()

        checkInternet()
        handler.postDelayed({
            checkInternet()
        }, 5000L)
    }

    private fun searchMovie() {
        searchView = binding.searchView
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    performSearch(newText)
                }
                return true
            }
        })
    }

    private fun checkInternet() {
        if (isConnectedToNetwork()) {
            fectGenreMovie()
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

    private fun fectGenreMovie() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("${HPI.API_URL}/genre/movie/list")
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
                        val jsonArray = jsonObject.getJSONArray("genres")

                        for (i in 0 until jsonArray.length()) {
                            val genreObject = jsonArray.getJSONObject(i)
                            val name = genreObject.getString("name")
                            val id = genreObject.getInt("id")

                            val genre = Genre(name, id)
                            genreList.add(genre)
                        }

                        runOnUiThread {
                            genreAdapter.notifyDataSetChanged()
                            swipeRefreshLayout.isRefreshing = false
                            loading.isDismiss()
                        }
                    }?: run {
                        runOnUiThread {
                            swipeRefreshLayout.isRefreshing = false
                            loading.isDismiss()
                        }
                    }
                }
            }
        })
    }

    private fun performSearch(query: String) {
        val filteredMovieList = MainActivity.movieList.filter { movie ->
            movie.name!!.contains(query, ignoreCase = true)
        }
        movieAdapter.setMovieList(filteredMovieList)
    }

    private fun fetchMovieData() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("${HPI.API_URL}/discover/movie")
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