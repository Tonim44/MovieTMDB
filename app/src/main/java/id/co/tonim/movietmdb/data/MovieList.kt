package id.co.tonim.movietmdb.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieList (
    val name: String,
    val images: String,
    val release: String,
    val descripsi: String,
    val rating: Double) : Parcelable
