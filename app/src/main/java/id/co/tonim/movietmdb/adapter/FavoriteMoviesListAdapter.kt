package id.co.tonim.movietmdb.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.co.tonim.movietmdb.DetailMovieActivity
import id.co.tonim.movietmdb.DetailMovieFavoritActivity
import id.co.tonim.movietmdb.R
import id.co.tonim.movietmdb.data.FavoritedMovie
import id.co.tonim.movietmdb.utils.HPI

class FavoriteMoviesListAdapter(private var movieList: List<FavoritedMovie>) :
    RecyclerView.Adapter<FavoriteMoviesListAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentItem = movieList[position]
        holder.name.text = currentItem.name
        holder.date.text = currentItem.release
        val imagePath = currentItem.images

        val baseUrl = "${HPI.IMAGE_URL}"
        val imageUrl = baseUrl + imagePath

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.images)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailMovieFavoritActivity::class.java)
            intent.putExtra(DetailMovieActivity.EXTRA_MOVIE, currentItem)
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    fun updateData(newMovieList: List<FavoritedMovie>) {
        movieList = newMovieList
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.titleTextView)
        val images: ImageView = itemView.findViewById(R.id.imageView)
        val date: TextView = itemView.findViewById(R.id.dateTextView)
    }

}
