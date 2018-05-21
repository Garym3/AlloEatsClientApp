package fr.esgi.alloeatsclientapp.business

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import fr.esgi.alloeatsclientapp.models.google.details.Result
import java.io.Serializable

class NearbyPlacesBuilder : Serializable, Parcelable {

    @SerializedName("html_attributions")
    @Expose
    var htmlAttributions: List<Any>? = null
    @SerializedName("next_page_token")
    @Expose
    var nextPageToken: String? = null
    @SerializedName("results")
    @Expose
    var results: List<Result>? = null
    @SerializedName("status")
    @Expose
    var status: String? = null

    protected constructor(`in`: Parcel) {
        `in`.readList(this.htmlAttributions, Any::class.java.classLoader)
        this.nextPageToken = `in`.readValue(String::class.java.classLoader) as String
        `in`.readList(this.results, Result::class.java.classLoader)
        this.status = `in`.readValue(String::class.java.classLoader) as String
    }

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param results
     * @param status
     * @param nextPageToken
     * @param htmlAttributions
     */
    constructor(htmlAttributions: List<Any>, nextPageToken: String, results: List<Result>, status: String) : super() {
        this.htmlAttributions = htmlAttributions
        this.nextPageToken = nextPageToken
        this.results = results
        this.status = status
    }

    fun withHtmlAttributions(htmlAttributions: List<Any>): NearbyPlacesBuilder {
        this.htmlAttributions = htmlAttributions
        return this
    }

    fun withNextPageToken(nextPageToken: String): NearbyPlacesBuilder {
        this.nextPageToken = nextPageToken
        return this
    }

    fun withResults(results: List<Result>): NearbyPlacesBuilder {
        this.results = results
        return this
    }

    fun withStatus(status: String): NearbyPlacesBuilder {
        this.status = status
        return this
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(htmlAttributions)
        dest.writeValue(nextPageToken)
        dest.writeList(results)
        dest.writeValue(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<NearbyPlacesBuilder> = object : Parcelable.Creator<NearbyPlacesBuilder> {


            override fun createFromParcel(`in`: Parcel): NearbyPlacesBuilder {
                return NearbyPlacesBuilder(`in`)
            }

            override fun newArray(size: Int): Array<NearbyPlacesBuilder?> {
                return arrayOfNulls(size)
            }

        }
        private const val serialVersionUID = 2325882689872638375L
    }

}
