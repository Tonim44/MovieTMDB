package id.co.tonim.movietmdb

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import id.co.tonim.movietmdb.adapter.FavoriteMoviesListAdapter
import id.co.tonim.movietmdb.databinding.ActivityGenreBinding
import id.co.tonim.movietmdb.utils.DatabaseHelper
import id.co.tonim.movietmdb.utils.LoadingDialog

class FavoriteMovieListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var movieAdapter: FavoriteMoviesListAdapter
    private lateinit var binding: ActivityGenreBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var loading: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityGenreBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.back.setOnClickListener(View.OnClickListener { onBackPressed() })

        binding.tittle.text = "Movie Favorit"

        recyclerView = binding.recyclerView
        val layoutManager = GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        movieAdapter = FavoriteMoviesListAdapter(emptyList())
        recyclerView.adapter = movieAdapter

        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            fetchFavoriteMovies()
        }

        loading = LoadingDialog(this)
        loading.startLoading()

        fetchFavoriteMovies()
    }

    private fun fetchFavoriteMovies() {
        val databaseHelper = DatabaseHelper(this)
        val favoriteMovies = databaseHelper.getFavoriteMovies()

        movieAdapter.updateData(favoriteMovies)
        swipeRefreshLayout.isRefreshing = false
        loading.isDismiss()

    }
}
