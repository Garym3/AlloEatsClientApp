package fr.esgi.alloeatsclientapp.models.google.details

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Southwest : Serializable, Parcelable {

    @SerializedName("lat")
    @Expose
    var lat: Double? = null
    @SerializedName("lng")
    @Expose
    var lng: Double? = null

    protected constructor(`in`: Parcel) {
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

    fun withLat(lat: Double?): Southwest {
        this.lat = lat
        return this
    }

    fun withLng(lng: Double?): Southwest {
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
        val CREATOR: Parcelable.Creator<Southwest> = object : Parcelable.Creator<Southwest> {


            override fun createFromParcel(`in`: Parcel): Southwest {
                return Southwest(`in`)
            }

            override fun newArray(size: Int): Array<Southwest?> {
                return arrayOfNulls(size)
            }

        }
        private const val serialVersionUID = 6648847875863693764L
    }

}
