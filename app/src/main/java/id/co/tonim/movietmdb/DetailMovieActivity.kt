package id.co.tonim.movietmdb

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import id.co.tonim.movietmdb.data.FavoritedMovie
import id.co.tonim.movietmdb.data.MovieList
import id.co.tonim.movietmdb.databinding.ActivityDetailMovieBinding
import id.co.tonim.movietmdb.utils.DatabaseHelper
import id.co.tonim.movietmdb.utils.HPI

class DetailMovieActivity : AppCompatActivity() {

    private lateinit var movieList: MovieList
    private lateinit var binding: ActivityDetailMovieBinding
    private lateinit var databaseHelper: DatabaseHelper

    companion object {
        const val EXTRA_MOVIE = "extra_movie"
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityDetailMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener(View.OnClickListener { onBackPressed() })

        movieList = intent.getParcelableExtra<MovieList>(EXTRA_MOVIE) as MovieList

        databaseHelper = DatabaseHelper(this)

        binding.love2.visibility = View.GONE
        binding.dateTextView.text = movieList.release
        binding.nameTextView.text = movieList.name
        binding.ratingTextView.text = "Rating: ${movieList.rating.toString()}"
        binding.descripsiTextView.text = movieList.descripsi
        Picasso.get().load(movieList.images).into(binding.imageView)

        if (databaseHelper.isMovieInFavorites(movieList.name)) {
            binding.love1.visibility = View.GONE
            binding.love2.visibility = View.VISIBLE
        }

        val imagePath = movieList.images
        val baseUrl = "${HPI.IMAGE_URL}"
        val imageUrl = baseUrl + imagePath

        Glide.with(this)
            .load(imageUrl)
            .into(binding.imageView)

        Glide.with(this)
            .load(imageUrl)
            .into(binding.backgroundimageView)

        binding.favorit.setOnClickListener {
            if (databaseHelper.isMovieInFavorites(movieList.name)) {

                Toast.makeText(
                    this,
                    "Movie sudah ada",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                binding.love1.visibility = View.GONE
                binding.love2.visibility = View.VISIBLE

                val favoritedMovie = FavoritedMovie(
                    0,
                    movieList.name,
                    movieList.release,
                    movieList.rating,
                    movieList.descripsi,
                    movieList.images
                )

                databaseHelper.addFavoriteMovie(favoritedMovie)

                Toast.makeText(
                    this,
                    "Movie sudah ditambahkan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
