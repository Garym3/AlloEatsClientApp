package fr.esgi.alloeatsclientapp.models.google.details

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import fr.esgi.alloeatsclientapp.models.google.details.Close
import java.io.Serializable

class Period : Serializable, Parcelable {

    @SerializedName("close")
    @Expose
    var close: Close? = null
    @SerializedName("open")
    @Expose
    var open: Open? = null

    protected constructor(`in`: Parcel) {
        this.close = `in`.readValue(Close::class.java.classLoader) as Close
        this.open = `in`.readValue(Open::class.java.classLoader) as Open
    }

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param open
     * @param close
     */
    constructor(close: Close, open: Open) : super() {
        this.close = close
        this.open = open
    }

    fun withClose(close: Close): Period {
        this.close = close
        return this
    }

    fun withOpen(open: Open): Period {
        this.open = open
        return this
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(close)
        dest.writeValue(open)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<Period> = object : Parcelable.Creator<Period> {


            override fun createFromParcel(`in`: Parcel): Period {
                return Period(`in`)
            }

            override fun newArray(size: Int): Array<Period?> {
                return arrayOfNulls(size)
            }

        }
        private const val serialVersionUID = -4552056601972551744L
    }

}
