package fr.esgi.alloeatsclientapp.models.google.details

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Review : Serializable, Parcelable {

    @SerializedName("author_name")
    @Expose
    var authorName: String? = null
    @SerializedName("author_url")
    @Expose
    var authorUrl: String? = null
    @SerializedName("language")
    @Expose
    var language: String? = null
    @SerializedName("profile_photo_url")
    @Expose
    var profilePhotoUrl: String? = null
    @SerializedName("rating")
    @Expose
    var rating: Int? = null
    @SerializedName("relative_time_description")
    @Expose
    var relativeTimeDescription: String? = null
    @SerializedName("text")
    @Expose
    var text: String? = null
    @SerializedName("time")
    @Expose
    var time: Int? = null

    protected constructor(`in`: Parcel) {
        this.authorName = `in`.readValue(String::class.java.classLoader) as String
        this.authorUrl = `in`.readValue(String::class.java.classLoader) as String
        this.language = `in`.readValue(String::class.java.classLoader) as String
        this.profilePhotoUrl = `in`.readValue(String::class.java.classLoader) as String
        this.rating = `in`.readValue(Int::class.java.classLoader) as Int
        this.relativeTimeDescription = `in`.readValue(String::class.java.classLoader) as String
        this.text = `in`.readValue(String::class.java.classLoader) as String
        this.time = `in`.readValue(Int::class.java.classLoader) as Int
    }

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param time
     * @param text
     * @param profilePhotoUrl
     * @param authorName
     * @param authorUrl
     * @param rating
     * @param language
     * @param relativeTimeDescription
     */
    constructor(authorName: String, authorUrl: String, language: String, profilePhotoUrl: String, rating: Int?, relativeTimeDescription: String, text: String, time: Int?) : super() {
        this.authorName = authorName
        this.authorUrl = authorUrl
        this.language = language
        this.profilePhotoUrl = profilePhotoUrl
        this.rating = rating
        this.relativeTimeDescription = relativeTimeDescription
        this.text = text
        this.time = time
    }

    fun withAuthorName(authorName: String): Review {
        this.authorName = authorName
        return this
    }

    fun withAuthorUrl(authorUrl: String): Review {
        this.authorUrl = authorUrl
        return this
    }

    fun withLanguage(language: String): Review {
        this.language = language
        return this
    }

    fun withProfilePhotoUrl(profilePhotoUrl: String): Review {
        this.profilePhotoUrl = profilePhotoUrl
        return this
    }

    fun withRating(rating: Int?): Review {
        this.rating = rating
        return this
    }

    fun withRelativeTimeDescription(relativeTimeDescription: String): Review {
        this.relativeTimeDescription = relativeTimeDescription
        return this
    }

    fun withText(text: String): Review {
        this.text = text
        return this
    }

    fun withTime(time: Int?): Review {
        this.time = time
        return this
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(authorName)
        dest.writeValue(authorUrl)
        dest.writeValue(language)
        dest.writeValue(profilePhotoUrl)
        dest.writeValue(rating)
        dest.writeValue(relativeTimeDescription)
        dest.writeValue(text)
        dest.writeValue(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<Review> = object : Parcelable.Creator<Review> {


            override fun createFromParcel(`in`: Parcel): Review {
                return Review(`in`)
            }

            override fun newArray(size: Int): Array<Review?> {
                return arrayOfNulls(size)
            }

        }
        private const val serialVersionUID = -1831301987318537304L
    }

}
