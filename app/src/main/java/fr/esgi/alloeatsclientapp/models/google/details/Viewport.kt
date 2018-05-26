package fr.esgi.alloeatsclientapp.models.google.details

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Viewport : Serializable, Parcelable {

    @SerializedName("northeast")
    @Expose
    var northeast: Northeast? = null
    @SerializedName("southwest")
    @Expose
    var southwest: Southwest? = null

    protected constructor(`in`: Parcel) {
        this.northeast = `in`.readValue(Northeast::class.java.classLoader) as Northeast
        this.southwest = `in`.readValue(Southwest::class.java.classLoader) as Southwest
    }

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param southwest
     * @param northeast
     */
    constructor(northeast: Northeast, southwest: Southwest) : super() {
        this.northeast = northeast
        this.southwest = southwest
    }

    fun withNortheast(northeast: Northeast): Viewport {
        this.northeast = northeast
        return this
    }

    fun withSouthwest(southwest: Southwest): Viewport {
        this.southwest = southwest
        return this
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(northeast)
        dest.writeValue(southwest)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<Viewport> = object : Parcelable.Creator<Viewport> {


            override fun createFromParcel(`in`: Parcel): Viewport {
                return Viewport(`in`)
            }

            override fun newArray(size: Int): Array<Viewport?> {
                return arrayOfNulls(size)
            }

        }
        private const val serialVersionUID = -5972651612795251455L
    }

}
