package id.co.tonim.movietmdb.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.co.tonim.movietmdb.GenreActivity
import id.co.tonim.movietmdb.R
import id.co.tonim.movietmdb.data.Genre

class GenreAdapter (private var genreList: List<Genre>) : RecyclerView.Adapter<GenreAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.genre_list_item, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentGenre = genreList[position]
        holder.nameTextView.text = currentGenre.name

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, GenreActivity::class.java)
            intent.putExtra(GenreActivity.EXTRA_GENRE, currentGenre)
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return genreList.size
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textGenre)
    }
}