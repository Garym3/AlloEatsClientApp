package fr.esgi.alloeatsclientapp.models.nearbySearch

import java.io.Serializable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OpeningHours : Serializable, Parcelable {

    @SerializedName("open_now")
    @Expose
    var openNow: Boolean? = null
    @SerializedName("weekday_text")
    @Expose
    var weekdayText: List<Any>? = null

    protected constructor(`in`: Parcel) {
        this.openNow = `in`.readValue(Boolean::class.java.classLoader) as Boolean
        `in`.readList(this.weekdayText, Any::class.java.classLoader)
    }

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param weekdayText
     * @param openNow
     */
    constructor(openNow: Boolean?, weekdayText: List<Any>) : super() {
        this.openNow = openNow
        this.weekdayText = weekdayText
    }

    fun withOpenNow(openNow: Boolean?): OpeningHours {
        this.openNow = openNow
        return this
    }

    fun withWeekdayText(weekdayText: List<Any>): OpeningHours {
        this.weekdayText = weekdayText
        return this
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(openNow)
        dest.writeList(weekdayText)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<OpeningHours> = object : Creator<OpeningHours> {


            override fun createFromParcel(`in`: Parcel): OpeningHours {
                return OpeningHours(`in`)
            }

            override fun newArray(size: Int): Array<OpeningHours?> {
                return arrayOfNulls(size)
            }

        }
        private const val serialVersionUID = 6604596531706430870L
    }

}
