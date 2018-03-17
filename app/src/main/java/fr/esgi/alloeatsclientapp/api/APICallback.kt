package fr.esgi.alloeatsclientapp.api


import com.android.volley.VolleyError
import org.json.JSONObject


interface APICallback {
    // Handling the response from web server
    fun onSuccessResponse(result: JSONObject)

    // Handling of error request and action on the activity
    fun onErrorResponse(error: VolleyError)
}
