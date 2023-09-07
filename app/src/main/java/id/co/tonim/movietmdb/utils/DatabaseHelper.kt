package id.co.tonim.movietmdb.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import id.co.tonim.movietmdb.data.FavoritedMovie

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "FavoriteMoviesDB"
        private const val TABLE_FAVORITE_MOVIES = "favorite_movies"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_RELEASE = "release_date"
        private const val KEY_RATING = "rating"
        private const val KEY_DESCRIPSI = "description"
        private const val KEY_IMAGES = "image_url"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            ("CREATE TABLE $TABLE_FAVORITE_MOVIES($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$KEY_NAME TEXT, $KEY_RELEASE TEXT, $KEY_RATING REAL, $KEY_DESCRIPSI TEXT, $KEY_IMAGES TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITE_MOVIES")
        onCreate(db)
    }

    fun addFavoriteMovie(movie: FavoritedMovie) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, movie.name)
        values.put(KEY_RELEASE, movie.release)
        values.put(KEY_RATING, movie.rating)
        values.put(KEY_DESCRIPSI, movie.descripsi)
        values.put(KEY_IMAGES, movie.images)

        db.insert(TABLE_FAVORITE_MOVIES, null, values)
        db.close()
    }

    fun isMovieInFavorites(movieName: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_FAVORITE_MOVIES WHERE $KEY_NAME = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(movieName))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    @SuppressLint("Range")
    fun getFavoriteMovies(): List<FavoritedMovie> {
        val favoriteMoviesList = mutableListOf<FavoritedMovie>()
        val selectQuery = "SELECT * FROM $TABLE_FAVORITE_MOVIES"
        val db = this.readableDatabase

        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(KEY_ID))
                val name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                val release = cursor.getString(cursor.getColumnIndex(KEY_RELEASE))
                val rating = cursor.getDouble(cursor.getColumnIndex(KEY_RATING))
                val descripsi = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPSI))
                val images = cursor.getString(cursor.getColumnIndex(KEY_IMAGES))
                val movie = FavoritedMovie(id.toInt(), name, release, rating, descripsi, images)
                favoriteMoviesList.add(movie)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return favoriteMoviesList
    }
}