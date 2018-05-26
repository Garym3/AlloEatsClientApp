package fr.esgi.alloeatsclientapp.models.google.details

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Close : Serializable, Parcelable {

    @SerializedName("day")
    @Expose
    var day: Int? = null
    @SerializedName("time")
    @Expose
    var time: String? = null

    protected constructor(`in`: Parcel) {
        this.day = `in`.readValue(Int::class.java.classLoader) as Int
        this.time = `in`.readValue(String::class.java.classLoader) as String
    }

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param time
     * @param day
     */
    constructor(day: Int?, time: String) : super() {
        this.day = day
        this.time = time
    }

    fun withDay(day: Int?): Close {
        this.day = day
        return this
    }

    fun withTime(time: String): Close {
        this.time = time
        return this
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(day)
        dest.writeValue(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<Close> = object : Parcelable.Creator<Close> {


            override fun createFromParcel(`in`: Parcel): Close {
                return Close(`in`)
            }

            override fun newArray(size: Int): Array<Close?> {
                return arrayOfNulls(size)
            }

        }
        private const val serialVersionUID = -8255718197557012235L
    }

}
