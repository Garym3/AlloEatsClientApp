package fr.esgi.alloeatsclientapp.models

import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import org.json.JSONException



data class Restaurant(val id: String, val name: String, val mainPhoto: String,
                      val isOpen: Boolean, val rating: Int,
                      val address: String, val coordinates: LatLng){

    fun formatIsOpenedNow(): String {
        return if (this.isOpen) "Opened" else "Closed"
    }

    //TODO: TO adapt
    fun fromJson(jsonObjects: JSONObject): ArrayList<Restaurant>{
        val restaurants = ArrayList<Restaurant>()

        for (i in 0 until jsonObjects.length()) {
            try {
                /*restaurants.add(Restaurant(
                        jsonObjects.getJSONObject("id").toString(),
                        jsonObjects.getJSONObject("name").toString(),
                        jsonObjects.getJSONObject("mainPhoto").toString(),
                        jsonObjects.getJSONObject("isOpen"),
                        jsonObjects.getJSONObject("rating").toString(),
                        jsonObjects.getJSONObject("address").toString(),
                        jsonObjects.getJSONObject("coordinates").toString()
                        ))*/
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        return restaurants
    }
}