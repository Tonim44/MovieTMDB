package id.co.tonim.movietmdb.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Genre (
    val name : String,
    val id : Int ) : Parcelable
