package id.co.tonim.movietmdb.data

import android.os.Parcel
import android.os.Parcelable

data class FavoritedMovie(
  val id: Int,
  val name: String?,
  val release: String?,
  val rating: Double,
  val descripsi: String?,
  val images: String?
 ) : Parcelable {
  constructor(parcel: Parcel) : this(
   parcel.readInt(),
   parcel.readString(),
   parcel.readString(),
   parcel.readDouble(),
   parcel.readString(),
   parcel.readString()
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
   parcel.writeInt(id)
   parcel.writeString(name)
   parcel.writeString(release)
   parcel.writeDouble(rating)
   parcel.writeString(descripsi)
   parcel.writeString(images)
  }

  override fun describeContents(): Int {
   return 0
  }

  companion object CREATOR : Parcelable.Creator<FavoritedMovie> {
   override fun createFromParcel(parcel: Parcel): FavoritedMovie {
    return FavoritedMovie(parcel)
   }

   override fun newArray(size: Int): Array<FavoritedMovie?> {
    return arrayOfNulls(size)
   }
  }
 }

