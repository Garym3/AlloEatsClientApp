package fr.esgi.alloeatsclientapp.business

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import fr.esgi.alloeatsclientapp.models.google.details.Result
import java.io.Serializable

class DetailsBuilder : Serializable, Parcelable {

    @SerializedName("html_attributions")
    @Expose
    var htmlAttributions: List<Any>? = null
    @SerializedName("result")
    @Expose
    var result: Result? = null
    @SerializedName("status")
    @Expose
    var status: String? = null

    private constructor(`in`: Parcel) {
        `in`.readList(this.htmlAttributions, Any::class.java.classLoader)
        this.result = `in`.readValue(Result::class.java.classLoader) as Result
        this.status = `in`.readValue(String::class.java.classLoader) as String
    }

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param result
     * @param status
     * @param htmlAttributions
     */
    constructor(htmlAttributions: List<Any>, result: Result, status: String) : super() {
        this.htmlAttributions = htmlAttributions
        this.result = result
        this.status = status
    }

    fun withHtmlAttributions(htmlAttributions: List<Any>): DetailsBuilder {
        this.htmlAttributions = htmlAttributions
        return this
    }

    fun withResult(result: Result): DetailsBuilder {
        this.result = result
        return this
    }

    fun withStatus(status: String): DetailsBuilder {
        this.status = status
        return this
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(htmlAttributions)
        dest.writeValue(result)
        dest.writeValue(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<DetailsBuilder> = object : Parcelable.Creator<DetailsBuilder> {


            override fun createFromParcel(`in`: Parcel): DetailsBuilder {
                return DetailsBuilder(`in`)
            }

            override fun newArray(size: Int): Array<DetailsBuilder?> {
                return arrayOfNulls(size)
            }

        }
        private const val serialVersionUID = 5657549986979665482L
    }

}
