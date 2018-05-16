package fr.esgi.alloeatsclientapp.utils

import fr.esgi.alloeatsclientapp.models.User
import com.paypal.android.sdk.payments.PayPalConfiguration




class Global {
    companion object {
        const val EMAILPATTERN = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        const val PASSWORDMINLENGTH = 8
        const val PASSWORDREQUIREDCHARS = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
        const val wsPassword = "FeNBGjzDG354@ofe*\$32Rfsss4F"
        const val TAG = "AlloEatsClientApp"

        const val RESULTS = "results"
        const val STATUS = "status"

        const val OK = "OK"
        const val ZERO_RESULTS = "ZERO_RESULTS"
        const val REQUEST_DENIED = "REQUEST_DENIED"
        const val OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT"
        const val UNKNOWN_ERROR = "UNKNOWN_ERROR"
        const val INVALID_REQUEST = "INVALID_REQUEST"

        // Key for nearby places JSON from Google
        const val GEOMETRY = "geometry"
        const val LOCATION = "location"
        const val LATITUDE = "lat"
        const val LONGITUDE = "lng"
        const val ICON = "icon"
        const val RESTAURANT_ID = "id"
        const val NAME = "name"
        const val PLACE_ID = "place_id"
        const val REFERENCE = "reference"
        const val VICINITY = "vicinity"
        const val PLACE_NAME = "place_name"

        const val GOOGLE_BROWSER_API_KEY = "AIzaSyByPm_M8iKQpDqteolcEYtz4YcfKDFZL_M"
        const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
        const val PROXIMITY_RADIUS = 5000 // meters

        // The minimum distance to change Updates in meters
        const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // meters

        // The minimum time between updates in minutes
        const val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // minutes

        private const val paypalClientId =
                "AabnpXbIPZviyvDhsA2ziJn8-Eq1Yupbyn3__73I7rZf9lX24fK2bMAed5Ix1ndFA-uxvxluPRxKbOhp"

        // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
        // or live (ENVIRONMENT_PRODUCTION)
        private const val paypalEnvironment = PayPalConfiguration.ENVIRONMENT_NO_NETWORK

                val paypalConfig = PayPalConfiguration()
                .environment(paypalEnvironment)
                .clientId(paypalClientId)
    }

    object CurrentUser {
        var user: User? = null
    }
}