package fr.esgi.alloeatsclientapp.models.nearbySearch

import java.io.Serializable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Location : Serializable, Parcelable {

    @SerializedName("lat")
    @Expose
    var lat: Double? = null
    @SerializedName("lng")
    @Expose
    var lng: Double? = null

    private constructor(`in`: Parcel) {
        this.lat = `in`.readValue(Double::class.java.classLoader) as Double
        this.lng = `in`.readValue(Double::class.java.classLoader) as Double
    }

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param lng
     * @param lat
     */
    constructor(lat: Double?, lng: Double?) : super() {
        this.lat = lat
        this.lng = lng
    }

    fun withLat(lat: Double?): Location {
        this.lat = lat
        return this
    }

    fun withLng(lng: Double?): Location {
        this.lng = lng
        return this
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(lat)
        dest.writeValue(lng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<Location> = object : Creator<Location> {


            override fun createFromParcel(`in`: Parcel): Location {
                return Location(`in`)
            }

            override fun newArray(size: Int): Array<Location?> {
                return arrayOfNulls(size)
            }

        }
        private const val serialVersionUID = -5225164070828355264L
    }

}
