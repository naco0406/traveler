package com.example.traveler

import android.os.Parcel
import android.os.Parcelable

data class Place (
    val name : String,
    val city : String,
    val type : String,
    val mapx : Float,
    val mapy : Float,
    val address : String,
    val visited : Int,
    val tag : List<String>,
    val img : String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.createStringArrayList() ?: listOf(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(city)
        parcel.writeString(type)
        parcel.writeFloat(mapx)
        parcel.writeFloat(mapy)
        parcel.writeString(address)
        parcel.writeInt(visited)
        parcel.writeStringList(tag)
        parcel.writeString(img)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Place> {
        override fun createFromParcel(parcel: Parcel): Place {
            return Place(parcel)
        }

        override fun newArray(size: Int): Array<Place?> {
            return arrayOfNulls(size)
        }
    }
}
