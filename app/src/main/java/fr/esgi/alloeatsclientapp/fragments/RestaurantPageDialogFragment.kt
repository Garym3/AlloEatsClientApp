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
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.business.adapters.CarouselAdapter
import fr.esgi.alloeatsclientapp.business.builders.DetailsBuilder
import fr.esgi.alloeatsclientapp.models.google.details.Photo
import fr.esgi.alloeatsclientapp.models.google.details.Result
import fr.esgi.alloeatsclientapp.utils.Google
import hyogeun.github.com.colorratingbarlib.ColorRatingBar
import org.json.JSONObject


class RestaurantPageDialogFragment : DialogFragment() {
    private lateinit var selectedRestaurant: Result
    private val TAG: String = "RestaurantPageDialogFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.selected_restaurant_page_dialog_layout,
                container, false)

        selectedRestaurant = arguments!!.getParcelable("selectedRestaurant")

        setUIElements(rootView)

        setCarousel(rootView)

        return rootView
    }

    private fun setUIElements(rootView: View) {
        dialog.setTitle(selectedRestaurant.name)

        rootView.findViewById<TextView>(R.id.restaurantName_TextView).text =
                selectedRestaurant.name
        rootView.findViewById<TextView>(R.id.restaurantIsOpen_TextView).text =
                toReadableOpenNow(selectedRestaurant.openingHours!!.openNow)
        rootView.findViewById<ColorRatingBar>(R.id.restaurantRating_Stars).rating =
                selectedRestaurant.rating!!.toFloat()
        rootView.findViewById<TextView>(R.id.restaurantAddress_TextView).text =
                selectedRestaurant.vicinity

        (rootView.findViewById(R.id.placeOrder_Button) as Button).setOnClickListener({placeOrder()})
        (rootView.findViewById(R.id.dismissRestaurantPageFragmentDialog_button) as Button)
                .setOnClickListener({ dismiss() })
    }

    private fun placeOrder() {
        //TODO: Start MyOrder fragment

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
        carouselRecyclerView.adapter = carouselAdapter

        val googleDetailsUrl = buildGoogleDetailsUrlRequest()

        val request = JsonObjectRequest(Request.Method.GET, googleDetailsUrl.toString(),
                null, Response.Listener<JSONObject> { response ->

            Log.i(tag, "$TAG: onResponse - GoogleRestaurant= $response")
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
                    Log.e(TAG, "onErrorResponse: Error= $error")
                    Log.e(TAG, "onErrorResponse: Error= " + error?.message)
                }
        )

        queue.add(request)
    }

    private fun buildGooglePhotoUrlRequest(photoLink: Uri?): StringBuilder? {
        val googlePhotosUri =
                StringBuilder("https://maps.googleapis.com/maps/api/place/photo?")
                        .append("photoreference=").append(photoLink)
                        .append("&maxheight=").append("512")
                        .append("&maxwidth=").append("512")
                        .append("&key=${Google.GOOGLE_BROWSER_API_KEY}")

        Log.i("$TAG: Google Photos API Request", googlePhotosUri.toString())

        return googlePhotosUri
    }

    private fun buildGoogleDetailsUrlRequest(): StringBuilder? {
        val googleDetailsUri =
                StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?")
                        .append("placeid=").append(selectedRestaurant.placeId)
                        .append("&key=${Google.GOOGLE_BROWSER_API_KEY}")

        Log.i("$TAG: Google Details API Request", googleDetailsUri.toString())

        return googleDetailsUri
    }

    private fun toReadableOpenNow(isOpenNow: Boolean?): String{
        val str: String? = "Is open: "
        if(isOpenNow == null) return str + "Unknown"
        if(isOpenNow) { return str + "Yes" }
        return str + "No"
    }
}