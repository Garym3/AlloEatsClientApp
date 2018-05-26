package fr.esgi.alloeatsclientapp.business

import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.business.builders.DetailsBuilder
import fr.esgi.alloeatsclientapp.utils.Google
import org.json.JSONObject

class PlaceAutocomplete(val mFinder: Finder) {
    private val TAG = "PlaceAutocomplete"

    fun setPlaceAutocompleteFragment() {
        val autocompleteFragment =
                mFinder.fragmentManager.findFragmentById(R.id.place_autocomplete_fragment)
                        as PlaceAutocompleteFragment
        val typeFilter =
                AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT).build()

        autocompleteFragment.setHint("Find a restaurant!")

        autocompleteFragment.setBoundsBias(LatLngBounds(
                LatLng(mFinder.mLocation!!.latitude - 0.2, mFinder.mLocation!!.longitude - 0.2),
                LatLng(mFinder.mLocation!!.latitude + 0.2, mFinder.mLocation!!.longitude + 0.2)
        ))
        autocompleteFragment.setFilter(typeFilter)
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                if(!place.isDataValid) {
                    Toast.makeText(mFinder.mContext, "This place's data are invalid.",
                            Toast.LENGTH_SHORT).show()

                    Log.i(TAG, "This place's data are invalid: $place")

                    return
                }

                getSelectedAutocompletePlace(place)

                Log.i(TAG, "Place: " + place.name)
            }

            override fun onError(status: Status) {
                Toast.makeText(mFinder.mContext, "An error occurred.",
                        Toast.LENGTH_SHORT).show()

                Log.i(TAG, "An error occurred: $status")
            }
        })
    }

    private fun getSelectedAutocompletePlace(place: Place){
        val queue: RequestQueue = Volley.newRequestQueue(mFinder.mContext)

        val googleDetailsUrl = buildGoogleDetailsUrlRequest(place.id)

        val request = JsonObjectRequest(Request.Method.GET, googleDetailsUrl.toString(),
                null, Response.Listener<JSONObject> { response ->

            Log.i(TAG, "onResponse - Place = $response")
            val gson = GsonBuilder().serializeNulls().create()
            val detailsBuilder =
                    gson.fromJson(JsonParser().parse(response.toString()),
                            DetailsBuilder::class.java)

            if (!detailsBuilder.status.equals("OK")) return@Listener
            if (!detailsBuilder.result!!.types!!.containsAll(arrayOf("restaurant", "food").toList())){
                Toast.makeText(mFinder.mContext, "This place is not a restaurant.",
                        Toast.LENGTH_SHORT).show()

                Log.i(TAG, "This place is not a restaurant: ${detailsBuilder.result.toString()}")
                return@Listener
            }

            Google.selectedRestaurant = detailsBuilder.result

            mFinder.showClickedRestaurantAlert()
        },
                Response.ErrorListener { error ->
                    Log.e(TAG, "onErrorResponse: Error= $error")
                    Log.e(TAG, "onErrorResponse: Error= " + error?.message)
                }
        )

        queue.add(request)
    }

    private fun buildGoogleDetailsUrlRequest(id: String?): StringBuilder? {
        val googleDetailsUri =
                StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?")
                        .append("placeid=").append(id)
                        .append("&key=${Google.GOOGLE_BROWSER_API_KEY}")

        Log.i("$TAG: Google Details API Request", googleDetailsUri.toString())

        return googleDetailsUri
    }
}