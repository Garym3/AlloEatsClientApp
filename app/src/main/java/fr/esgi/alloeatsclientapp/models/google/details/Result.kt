package fr.esgi.alloeatsclientapp.models.google.details

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Result : Serializable, Parcelable {

    @SerializedName("address_components")
    @Expose
    var addressComponents: List<AddressComponent>? = null
    @SerializedName("adr_address")
    @Expose
    var adrAddress: String? = null
    @SerializedName("formatted_address")
    @Expose
    var formattedAddress: String? = null
    @SerializedName("formatted_phone_number")
    @Expose
    var formattedPhoneNumber: String? = null
    @SerializedName("geometry")
    @Expose
    var geometry: Geometry? = null
    @SerializedName("icon")
    @Expose
    var icon: String? = null
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("international_phone_number")
    @Expose
    var internationalPhoneNumber: String? = null
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
    @SerializedName("price_level")
    @Expose
    var priceLevel: Int? = null
    @SerializedName("rating")
    @Expose
    var rating: Double? = null
    @SerializedName("reference")
    @Expose
    var reference: String? = null
    @SerializedName("reviews")
    @Expose
    var reviews: List<Review>? = null
    @SerializedName("scope")
    @Expose
    var scope: String? = null
    @SerializedName("types")
    @Expose
    var types: List<String>? = null
    @SerializedName("url")
    @Expose
    var url: String? = null
    @SerializedName("utc_offset")
    @Expose
    var utcOffset: Int? = null
    @SerializedName("vicinity")
    @Expose
    var vicinity: String? = null
    @SerializedName("website")
    @Expose
    var website: String? = null

    protected constructor(`in`: Parcel) {
        `in`.readList(this.addressComponents, AddressComponent::class.java.classLoader)
        this.adrAddress = `in`.readValue(String::class.java.classLoader) as String
        this.formattedAddress = `in`.readValue(String::class.java.classLoader) as String
        this.formattedPhoneNumber = `in`.readValue(String::class.java.classLoader) as String
        this.geometry = `in`.readValue(Geometry::class.java.classLoader) as Geometry
        this.icon = `in`.readValue(String::class.java.classLoader) as String
        this.id = `in`.readValue(String::class.java.classLoader) as String
        this.internationalPhoneNumber = `in`.readValue(String::class.java.classLoader) as String
        this.name = `in`.readValue(String::class.java.classLoader) as String
        this.openingHours = `in`.readValue(OpeningHours::class.java.classLoader) as OpeningHours
        `in`.readList(this.photos, Photo::class.java.classLoader)
        this.placeId = `in`.readValue(String::class.java.classLoader) as String
        this.priceLevel = `in`.readValue(Int::class.java.classLoader) as Int
        this.rating = `in`.readValue(Double::class.java.classLoader) as Double
        this.reference = `in`.readValue(String::class.java.classLoader) as String
        `in`.readList(this.reviews, Review::class.java.classLoader)
        this.scope = `in`.readValue(String::class.java.classLoader) as String
        `in`.readList(this.types, java.lang.String::class.java.classLoader)
        this.url = `in`.readValue(String::class.java.classLoader) as String
        this.utcOffset = `in`.readValue(Int::class.java.classLoader) as Int
        this.vicinity = `in`.readValue(String::class.java.classLoader) as String
        this.website = `in`.readValue(String::class.java.classLoader) as String
    }

    /**
     * No args constructor for use in serialization
     *
     */
    constructor()

    /**
     *
     * @param icon
     * @param reviews
     * @param scope
     * @param priceLevel
     * @param website
     * @param openingHours
     * @param adrAddress
     * @param url
     * @param reference
     * @param geometry
     * @param internationalPhoneNumber
     * @param id
     * @param photos
     * @param vicinity
     * @param formattedPhoneNumber
     * @param placeId
     * @param name
     * @param utcOffset
     * @param formattedAddress
     * @param rating
     * @param types
     * @param addressComponents
     */
    constructor(addressComponents: List<AddressComponent>, adrAddress: String, formattedAddress: String, formattedPhoneNumber: String, geometry: Geometry, icon: String, id: String, internationalPhoneNumber: String, name: String, openingHours: OpeningHours, photos: List<Photo>, placeId: String, priceLevel: Int?, rating: Double?, reference: String, reviews: List<Review>, scope: String, types: List<String>, url: String, utcOffset: Int?, vicinity: String, website: String) : super() {
        this.addressComponents = addressComponents
        this.adrAddress = adrAddress
        this.formattedAddress = formattedAddress
        this.formattedPhoneNumber = formattedPhoneNumber
        this.geometry = geometry
        this.icon = icon
        this.id = id
        this.internationalPhoneNumber = internationalPhoneNumber
        this.name = name
        this.openingHours = openingHours
        this.photos = photos
        this.placeId = placeId
        this.priceLevel = priceLevel
        this.rating = rating
        this.reference = reference
        this.reviews = reviews
        this.scope = scope
        this.types = types
        this.url = url
        this.utcOffset = utcOffset
        this.vicinity = vicinity
        this.website = website
    }

    fun withAddressComponents(addressComponents: List<AddressComponent>): Result {
        this.addressComponents = addressComponents
        return this
    }

    fun withAdrAddress(adrAddress: String): Result {
        this.adrAddress = adrAddress
        return this
    }

    fun withFormattedAddress(formattedAddress: String): Result {
        this.formattedAddress = formattedAddress
        return this
    }

    fun withFormattedPhoneNumber(formattedPhoneNumber: String): Result {
        this.formattedPhoneNumber = formattedPhoneNumber
        return this
    }

    fun withGeometry(geometry: Geometry): Result {
        this.geometry = geometry
        return this
    }

    fun withIcon(icon: String): Result {
        this.icon = icon
        return this
    }

    fun withId(id: String): Result {
        this.id = id
        return this
    }

    fun withInternationalPhoneNumber(internationalPhoneNumber: String): Result {
        this.internationalPhoneNumber = internationalPhoneNumber
        return this
    }

    fun withName(name: String): Result {
        this.name = name
        return this
    }

    fun withOpeningHours(openingHours: OpeningHours): Result {
        this.openingHours = openingHours
        return this
    }

    fun withPhotos(photos: List<Photo>): Result {
        this.photos = photos
        return this
    }

    fun withPlaceId(placeId: String): Result {
        this.placeId = placeId
        return this
    }

    fun withPriceLevel(priceLevel: Int?): Result {
        this.priceLevel = priceLevel
        return this
    }

    fun withRating(rating: Double?): Result {
        this.rating = rating
        return this
    }

    fun withReference(reference: String): Result {
        this.reference = reference
        return this
    }

    fun withReviews(reviews: List<Review>): Result {
        this.reviews = reviews
        return this
    }

    fun withScope(scope: String): Result {
        this.scope = scope
        return this
    }

    fun withTypes(types: List<String>): Result {
        this.types = types
        return this
    }

    fun withUrl(url: String): Result {
        this.url = url
        return this
    }

    fun withUtcOffset(utcOffset: Int?): Result {
        this.utcOffset = utcOffset
        return this
    }

    fun withVicinity(vicinity: String): Result {
        this.vicinity = vicinity
        return this
    }

    fun withWebsite(website: String): Result {
        this.website = website
        return this
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(addressComponents)
        dest.writeValue(adrAddress)
        dest.writeValue(formattedAddress)
        dest.writeValue(formattedPhoneNumber)
        dest.writeValue(geometry)
        dest.writeValue(icon)
        dest.writeValue(id)
        dest.writeValue(internationalPhoneNumber)
        dest.writeValue(name)
        dest.writeValue(openingHours)
        dest.writeList(photos)
        dest.writeValue(placeId)
        dest.writeValue(priceLevel)
        dest.writeValue(rating)
        dest.writeValue(reference)
        dest.writeList(reviews)
        dest.writeValue(scope)
        dest.writeList(types)
        dest.writeValue(url)
        dest.writeValue(utcOffset)
        dest.writeValue(vicinity)
        dest.writeValue(website)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is Result) {
            return false
        }

        return this.placeId.equals(other.placeId)
    }

    companion object {
        val CREATOR: Parcelable.Creator<Result> = object : Parcelable.Creator<Result> {


            override fun createFromParcel(`in`: Parcel): Result {
                return Result(`in`)
            }

            override fun newArray(size: Int): Array<Result?> {
                return arrayOfNulls(size)
            }

        }
        private const val serialVersionUID = 720447507321906759L
    }

}
