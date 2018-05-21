package fr.esgi.alloeatsclientapp.fragments

import android.app.DialogFragment
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.azoft.carousellayoutmanager.CarouselLayoutManager
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener
import com.azoft.carousellayoutmanager.CenterScrollListener
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.inaka.killertask.KillerTask
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.business.CarouselAdapter
import fr.esgi.alloeatsclientapp.business.DetailsBuilder
import fr.esgi.alloeatsclientapp.models.google.details.Photo
import fr.esgi.alloeatsclientapp.models.google.details.Result
import fr.esgi.alloeatsclientapp.utils.Google
import org.json.JSONObject


class RestaurantPageDialogFragment : DialogFragment() {
    private lateinit var selectedGoogleRestaurant: Result
    private val TAG: String = "RestaurantPageDialogFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.selected_restaurant_page_alertdialog_layout,
                container, false)

        setUIElements(rootView)

        setCarousel(rootView)

        return rootView
    }

    private fun setUIElements(rootView: View) {
        selectedGoogleRestaurant = arguments!!.getParcelable("selectedGoogleRestaurant")

        dialog.setTitle(selectedGoogleRestaurant.name)

        rootView.findViewById<TextView>(R.id.restaurantName_TextView).text =
                selectedGoogleRestaurant.name
        rootView.findViewById<TextView>(R.id.restaurantIsOpen_TextView).text =
                toReadableOpenNow(selectedGoogleRestaurant.openingHours!!.openNow)
        rootView.findViewById<TextView>(R.id.restaurantRating_TextView).text =
                toReadableRating()
        rootView.findViewById<TextView>(R.id.restaurantAddress_TextView).text =
                selectedGoogleRestaurant.vicinity

        (rootView.findViewById(R.id.dismissRestaurantPageFragmentDialog_button) as Button)
                .setOnClickListener({ dismiss() })
    }

    private fun setCarousel(view: View?){
        val layoutManager = CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL,true)
        val carouselRecyclerView: RecyclerView = view!!.findViewById(R.id.carouselLayout)
        carouselRecyclerView.layoutManager = layoutManager
        carouselRecyclerView.setHasFixedSize(true)
        carouselRecyclerView.addOnScrollListener(CenterScrollListener())
        layoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())

        setRestaurantImages(carouselRecyclerView)
    }

    private fun setRestaurantImages(carouselRecyclerView: RecyclerView) {
        val queue: RequestQueue = Volley.newRequestQueue(context)
        val carouselAdapter = CarouselAdapter(context)

        KillerTask(
                {
                    val googleDetailsUrl = buildGoogleDetailsUrlRequest()

                    val request = JsonObjectRequest(Request.Method.GET, googleDetailsUrl.toString(),
                            null, Response.Listener<JSONObject> { response ->

                        Log.i(tag, "onResponse: GoogleRestaurant= " + response.toString())
                        val gson = GsonBuilder().serializeNulls().create()
                        val detailsBuilder =
                                gson.fromJson(JsonParser().parse(response.toString()),
                                        DetailsBuilder::class.java)

                        if (!detailsBuilder.status.equals("OK")) return@Listener

                        val restaurantDetails = detailsBuilder.result?.photos!!

                        for (photo: Photo in restaurantDetails) {
                            if(photo.photoReference == null) continue

                            val googleDetailsStr =
                                    buildGooglePhotoUrlRequest(Uri.parse(photo.photoReference))

                            val googleDetailsUri = Uri.parse(googleDetailsStr.toString())
                            carouselAdapter.add(googleDetailsUri)
                        }
                    },
                            Response.ErrorListener { error ->
                                Log.e(tag, "onErrorResponse: Error= $error")
                                Log.e(tag, "onErrorResponse: Error= " + error?.message)
                            }
                    )

                    queue.add(request)

                },
                {
                    carouselRecyclerView.adapter = carouselAdapter
                    Log.i(TAG, "All photos retrieved.")
                },
                {
                    carouselRecyclerView.adapter = carouselAdapter
                    Log.e(TAG, it?.message)
                }).go()
    }

    private fun buildGooglePhotoUrlRequest(photoLink: Uri?): StringBuilder? {
        val googlePhotosUri =
                StringBuilder("https://maps.googleapis.com/maps/api/place/photo?")
                        .append("photoreference=").append(photoLink)
                        .append("&maxheight=").append("512")
                        .append("&maxwidth=").append("512")
                        .append("&key=${Google.GOOGLE_BROWSER_API_KEY}")

        Log.i("Google Photos API Request", googlePhotosUri.toString())

        return googlePhotosUri
    }

    private fun buildGoogleDetailsUrlRequest(): StringBuilder? {
        val googleDetailsUri =
                StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?")
                        .append("placeid=").append(selectedGoogleRestaurant.placeId)
                        .append("&key=${Google.GOOGLE_BROWSER_API_KEY}")

        Log.i("Google Details API Request", googleDetailsUri.toString())

        return googleDetailsUri
    }

    private fun toReadableRating() : String{
        return if(selectedGoogleRestaurant.rating == null) "No rating" else selectedGoogleRestaurant.rating.toString()
    }

    private fun toReadableOpenNow(isOpenNow: Boolean?): String{
        if(isOpenNow == null) return "Unknown"
        if(isOpenNow) { return "Yes" }
        return "No"
    }
}