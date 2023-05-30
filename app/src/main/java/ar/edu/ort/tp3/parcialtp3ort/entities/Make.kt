package ar.edu.ort.tp3.parcialtp3ort.entities

import android.os.Parcel
import android.os.Parcelable

class Make(name: String?, url: String?, count: Int?) : Parcelable {
    var name: String = ""
    var url : String=""
    var count: Int = 0

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    )

    init {
        this.name = name!!
        this.url = url!!
        this.count = count!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(url)
        parcel.writeInt(count)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Make> {
        override fun createFromParcel(parcel: Parcel): Make {
            return Make(parcel)
        }

        override fun newArray(size: Int): Array<Make?> {
            return arrayOfNulls(size)
        }
    }
}