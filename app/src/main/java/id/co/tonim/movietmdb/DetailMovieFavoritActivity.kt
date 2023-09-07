package id.co.tonim.movietmdb

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import id.co.tonim.movietmdb.data.FavoritedMovie
import id.co.tonim.movietmdb.databinding.ActivityDetailMovieBinding
import id.co.tonim.movietmdb.utils.HPI

class DetailMovieFavoritActivity: AppCompatActivity() {

    private lateinit var movieNameTextView: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var overviewTextView: TextView
    private lateinit var posterImageView: ImageView
    private lateinit var ratingTextView: TextView
    private lateinit var binding: ActivityDetailMovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityDetailMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener(View.OnClickListener { onBackPressed() })

        movieNameTextView = binding.nameTextView
        releaseDateTextView = binding.dateTextView
        overviewTextView = binding.descripsiTextView
        posterImageView = binding.imageView
        ratingTextView = binding.ratingTextView

        val favoritedMovie = intent.getParcelableExtra<FavoritedMovie>(EXTRA_MOVIE)

        favoritedMovie?.let {
            movieNameTextView.text = it.name
            releaseDateTextView.text = it.release
            overviewTextView.text = it.descripsi
            ratingTextView.text = it.rating.toString()

            val baseUrl = "${HPI.IMAGE_URL}"
            val imageUrl = baseUrl + it.images

            Glide.with(this)
                .load(imageUrl)
                .into(posterImageView)

            Glide.with(this)
                .load(imageUrl)
                .into(binding.backgroundimageView)

            binding.favorit.visibility = View.GONE
        }
    }

    companion object {
        const val EXTRA_MOVIE = "extra_movie"
    }
}