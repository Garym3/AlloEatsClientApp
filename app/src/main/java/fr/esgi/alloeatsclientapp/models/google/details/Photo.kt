package fr.esgi.alloeatsclientapp.models.google.details

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Photo : Serializable, Parcelable {

    @SerializedName("height")
    @Expose
    var height: Int? = null
    @SerializedName("html_attributions")
    @Expose
    var htmlAttributions: List<String>? = null
    @SerializedName("photo_reference")
    @Expose
    var photoReference: String? = null
    @SerializedName("width")
    @Expose
    var width: Int? = null

    protected constructor(`in`: Parcel) {
        this.height = `in`.readValue(Int::class.java.classLoader) as Int
        `in`.readList(this.htmlAttributions, java.lang.String::class.java.classLoader)
        this.photoReference = `in`.readValue(String::class.java.classLoader) as String
        this.width = `in`.readValue(Int::class.java.classLoader) as Int
    }

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param height
     * @param width
     * @param htmlAttributions
     * @param photoReference
     */
    constructor(height: Int?, htmlAttributions: List<String>, photoReference: String?, width: Int?) : super() {
        this.height = height
        this.htmlAttributions = htmlAttributions
        this.photoReference = photoReference
        this.width = width
    }

    fun withHeight(height: Int?): Photo {
        this.height = height
        return this
    }

    fun withHtmlAttributions(htmlAttributions: List<String>): Photo {
        this.htmlAttributions = htmlAttributions
        return this
    }

    fun withPhotoReference(photoReference: String?): Photo {
        this.photoReference = photoReference
        return this
    }

    fun withWidth(width: Int?): Photo {
        this.width = width
        return this
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(height)
        dest.writeList(htmlAttributions)
        dest.writeValue(photoReference)
        dest.writeValue(width)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<Photo> = object : Parcelable.Creator<Photo> {


            override fun createFromParcel(`in`: Parcel): Photo {
                return Photo(`in`)
            }

            override fun newArray(size: Int): Array<Photo?> {
                return arrayOfNulls(size)
            }

        }
        private const val serialVersionUID = -4819298440302309192L
    }

}
