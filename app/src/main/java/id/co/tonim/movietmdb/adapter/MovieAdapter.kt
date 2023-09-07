package id.co.tonim.movietmdb.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.co.tonim.movietmdb.DetailMovieActivity
import id.co.tonim.movietmdb.R
import id.co.tonim.movietmdb.data.MovieList
import id.co.tonim.movietmdb.utils.HPI

class MovieAdapter(private var movieList: List<MovieList>) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

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
            val intent = Intent(holder.itemView.context, DetailMovieActivity::class.java)
            intent.putExtra(DetailMovieActivity.EXTRA_MOVIE, currentItem)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.titleTextView)
        val images: ImageView = itemView.findViewById(R.id.imageView)
        val date: TextView = itemView.findViewById(R.id.dateTextView)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMovieList(list: List<MovieList>) {
        movieList = list
        notifyDataSetChanged()
    }
}
