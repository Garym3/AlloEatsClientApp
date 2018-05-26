package fr.esgi.alloeatsclientapp.models.google.details

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class AddressComponent : Serializable, Parcelable {

    @SerializedName("long_name")
    @Expose
    var longName: String? = null
    @SerializedName("short_name")
    @Expose
    var shortName: String? = null
    @SerializedName("types")
    @Expose
    var types: List<String>? = null

    protected constructor(`in`: Parcel) {
        this.longName = `in`.readValue(String::class.java.classLoader) as String
        this.shortName = `in`.readValue(String::class.java.classLoader) as String
        `in`.readList(this.types, java.lang.String::class.java.classLoader)
    }

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param longName
     * @param types
     * @param shortName
     */
    constructor(longName: String, shortName: String, types: List<String>) : super() {
        this.longName = longName
        this.shortName = shortName
        this.types = types
    }

    fun withLongName(longName: String): AddressComponent {
        this.longName = longName
        return this
    }

    fun withShortName(shortName: String): AddressComponent {
        this.shortName = shortName
        return this
    }

    fun withTypes(types: List<String>): AddressComponent {
        this.types = types
        return this
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(longName)
        dest.writeValue(shortName)
        dest.writeList(types)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<AddressComponent> = object : Parcelable.Creator<AddressComponent> {


            override fun createFromParcel(`in`: Parcel): AddressComponent {
                return AddressComponent(`in`)
            }

            override fun newArray(size: Int): Array<AddressComponent?> {
                return arrayOfNulls(size)
            }

        }
        private const val serialVersionUID = -2991596393005890123L
    }

}
