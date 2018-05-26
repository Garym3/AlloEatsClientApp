package fr.esgi.alloeatsclientapp.models.google.details

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class OpeningHours : Serializable, Parcelable {

    @SerializedName("open_now")
    @Expose
    var openNow: Boolean? = null
    @SerializedName("periods")
    @Expose
    var periods: List<Period>? = null
    @SerializedName("weekday_text")
    @Expose
    var weekdayText: List<String>? = null

    protected constructor(`in`: Parcel) {
        this.openNow = `in`.readValue(Boolean::class.java.classLoader) as Boolean
        `in`.readList(this.periods, Period::class.java.classLoader)
        `in`.readList(this.weekdayText, java.lang.String::class.java.classLoader)
    }

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param weekdayText
     * @param periods
     * @param openNow
     */
    constructor(openNow: Boolean?, periods: List<Period>, weekdayText: List<String>) : super() {
        this.openNow = openNow
        this.periods = periods
        this.weekdayText = weekdayText
    }

    fun withOpenNow(openNow: Boolean?): OpeningHours {
        this.openNow = openNow
        return this
    }

    fun withPeriods(periods: List<Period>): OpeningHours {
        this.periods = periods
        return this
    }

    fun withWeekdayText(weekdayText: List<String>): OpeningHours {
        this.weekdayText = weekdayText
        return this
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(openNow)
        dest.writeList(periods)
        dest.writeList(weekdayText)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<OpeningHours> = object : Parcelable.Creator<OpeningHours> {


            override fun createFromParcel(`in`: Parcel): OpeningHours {
                return OpeningHours(`in`)
            }

            override fun newArray(size: Int): Array<OpeningHours?> {
                return arrayOfNulls(size)
            }

        }
        private const val serialVersionUID = 4099933168907090067L
    }

}
