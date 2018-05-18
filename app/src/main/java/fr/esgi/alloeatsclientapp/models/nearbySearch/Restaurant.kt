package fr.esgi.alloeatsclientapp.models.nearbySearch

import java.io.Serializable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.net.URI


/*
"Result" from Google Nearby Places JSON
 */
class Restaurant : Serializable, Parcelable {

    @SerializedName("geometry")
    @Expose
    var geometry: Geometry? = null
    @SerializedName("icon")
    @Expose
    var icon: URI? = null
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("opening_hours")
    @Expose
    var openingHours: OpeningHours? = null
    @SerializedName("photos")
    @Expose
    var photos: List<Photo>? = null
    @SerializedName("place_id")
    @Expose
    var placeId: String? = null
    @SerializedName("rating")
    @Expose
    var rating: Double? = null
    @SerializedName("reference")
    @Expose
    var reference: String? = null
    @SerializedName("scope")
    @Expose
    var scope: String? = null
    @SerializedName("types")
    @Expose
    var types: List<String>? = null
    @SerializedName("vicinity")
    @Expose
    var vicinity: String? = null

    private constructor(`in`: Parcel) {
        this.geometry = `in`.readValue(Geometry::class.java.classLoader) as Geometry
        this.icon = `in`.readValue(URI::class.java.classLoader) as URI
        this.id = `in`.readValue(String::class.java.classLoader) as String
        this.name = `in`.readValue(String::class.java.classLoader) as String
        this.openingHours = `in`.readValue(OpeningHours::class.java.classLoader) as OpeningHours
        `in`.readList(this.photos, Photo::class.java.classLoader)
        this.placeId = `in`.readValue(String::class.java.classLoader) as String
        this.rating = `in`.readValue(Double::class.java.classLoader) as Double
        this.reference = `in`.readValue(String::class.java.classLoader) as String
        this.scope = `in`.readValue(String::class.java.classLoader) as String
        `in`.readList(this.types, java.lang.String::class.java.classLoader)
        this.vicinity = `in`.readValue(String::class.java.classLoader) as String
    }

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param photos
     * @param id
     * @param icon
     * @param vicinity
     * @param scope
     * @param placeId
     * @param openingHours
     * @param name
     * @param rating
     * @param types
     * @param reference
     * @param geometry
     */
    constructor(geometry: Geometry, icon: URI, id: String, name: String, openingHours: OpeningHours, photos: List<Photo>, placeId: String, rating: Double?, reference: String, scope: String, types: List<String>, vicinity: String) : super() {
        this.geometry = geometry
        this.icon = icon
        this.id = id
        this.name = name
        this.openingHours = openingHours
        this.photos = photos
        this.placeId = placeId
        this.rating = rating
        this.reference = reference
        this.scope = scope
        this.types = types
        this.vicinity = vicinity
    }

    fun withGeometry(geometry: Geometry): Restaurant {
        this.geometry = geometry
        return this
    }

    fun withIcon(icon: URI): Restaurant {
        this.icon = icon
        return this
    }

    fun withId(id: String): Restaurant {
        this.id = id
        return this
    }

    fun withName(name: String): Restaurant {
        this.name = name
        return this
    }

    fun withOpeningHours(openingHours: OpeningHours): Restaurant {
        this.openingHours = openingHours
        return this
    }

    fun withPhotos(photos: List<Photo>): Restaurant {
        this.photos = photos
        return this
    }

    fun withPlaceId(placeId: String): Restaurant {
        this.placeId = placeId
        return this
    }

    fun withRating(rating: Double?): Restaurant {
        this.rating = rating
        return this
    }

    fun withReference(reference: String): Restaurant {
        this.reference = reference
        return this
    }

    fun withScope(scope: String): Restaurant {
        this.scope = scope
        return this
    }

    fun withTypes(types: List<String>): Restaurant {
        this.types = types
        return this
    }

    fun withVicinity(vicinity: String): Restaurant {
        this.vicinity = vicinity
        return this
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(geometry)
        dest.writeValue(icon)
        dest.writeValue(id)
        dest.writeValue(name)
        dest.writeValue(openingHours)
        dest.writeList(photos)
        dest.writeValue(placeId)
        dest.writeValue(rating)
        dest.writeValue(reference)
        dest.writeValue(scope)
        dest.writeList(types)
        dest.writeValue(vicinity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Parcelable.Creator<Restaurant> = object : Creator<Restaurant> {


            override fun createFromParcel(`in`: Parcel): Restaurant {
                return Restaurant(`in`)
            }

            override fun newArray(size: Int): Array<Restaurant?> {
                return arrayOfNulls(size)
            }

        }
        private const val serialVersionUID = -2946741624609942128L
    }

}
