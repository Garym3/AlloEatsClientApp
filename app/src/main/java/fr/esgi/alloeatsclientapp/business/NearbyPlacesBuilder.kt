package fr.esgi.alloeatsclientapp.business

import java.io.Serializable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import fr.esgi.alloeatsclientapp.models.nearbySearch.Restaurant

class NearbyPlacesBuilder : Serializable, Parcelable {

    @SerializedName("html_attributions")
    @Expose
    var htmlAttributions: List<Any>? = null
    @SerializedName("results")
    @Expose
    var results: List<Restaurant>? = null
    @SerializedName("status")
    @Expose
    var status: String? = null

    protected constructor(`in`: Parcel) {
        `in`.readList(this.htmlAttributions, Any::class.java.classLoader)
        `in`.readList(this.results, Restaurant::class.java.classLoader)
        this.status = `in`.readValue(String::class.java.classLoader) as String
    }

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param results
     * @param status
     * @param htmlAttributions
     */
    constructor(htmlAttributions: List<Any>, results: List<Restaurant>, status: String) : super() {
        this.htmlAttributions = htmlAttributions
        this.results = results
        this.status = status
    }

    fun withHtmlAttributions(htmlAttributions: List<Any>): NearbyPlacesBuilder {
        this.htmlAttributions = htmlAttributions
        return this
    }

    fun withResults(results: List<Restaurant>): NearbyPlacesBuilder {
        this.results = results
        return this
    }

    fun withStatus(status: String): NearbyPlacesBuilder {
        this.status = status
        return this
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(htmlAttributions)
        dest.writeList(results)
        dest.writeValue(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<NearbyPlacesBuilder> = object : Creator<NearbyPlacesBuilder> {


            override fun createFromParcel(`in`: Parcel): NearbyPlacesBuilder {
                return NearbyPlacesBuilder(`in`)
            }

            override fun newArray(size: Int): Array<NearbyPlacesBuilder?> {
                return arrayOfNulls(size)
            }

        }
        private const val serialVersionUID = 8602743088098613360L
    }

}
