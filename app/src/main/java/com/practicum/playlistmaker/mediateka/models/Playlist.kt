package com.practicum.playlistmaker.mediateka.models

import android.os.Parcel
import android.os.Parcelable
import com.practicum.playlistmaker.search.models.Track

data class Playlist(
    val id: Long = 0,
    var name: String,
    var description: String,
    var image: String?,
    val tracks: MutableList<Track>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString(),
        TODO("tracks")
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Playlist> {
        override fun createFromParcel(parcel: Parcel): Playlist {
            return Playlist(parcel)
        }

        override fun newArray(size: Int): Array<Playlist?> {
            return arrayOfNulls(size)
        }
    }
}
