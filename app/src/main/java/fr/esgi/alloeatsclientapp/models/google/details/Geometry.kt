package fr.esgi.alloeatsclientapp.models.google.details

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Geometry : Serializable, Parcelable {

    @SerializedName("location")
    @Expose
    var location: Location? = null
    @SerializedName("viewport")
    @Expose
    var viewport: Viewport? = null

    protected constructor(`in`: Parcel) {
        this.location = `in`.readValue(Location::class.java.classLoader) as Location
        this.viewport = `in`.readValue(Viewport::class.java.classLoader) as Viewport
    }

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param viewport
     * @param location
     */
    constructor(location: Location, viewport: Viewport) : super() {
        this.location = location
        this.viewport = viewport
    }

    fun withLocation(location: Location): Geometry {
        this.location = location
        return this
    }

    fun withViewport(viewport: Viewport): Geometry {
        this.viewport = viewport
        return this
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(location)
        dest.writeValue(viewport)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<Geometry> = object : Parcelable.Creator<Geometry> {


            override fun createFromParcel(`in`: Parcel): Geometry {
                return Geometry(`in`)
            }

            override fun newArray(size: Int): Array<Geometry?> {
                return arrayOfNulls(size)
            }

        }
        private const val serialVersionUID = 8331642925472695278L
    }

}
