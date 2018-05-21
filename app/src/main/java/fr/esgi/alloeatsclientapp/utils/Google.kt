package fr.esgi.alloeatsclientapp.utils

import fr.esgi.alloeatsclientapp.models.google.details.Result

class Google {
    companion object {
        const val RESULTS = "results"
        const val STATUS = "status"

        const val OK = "OK"
        const val ZERO_RESULTS = "ZERO_RESULTS"
        const val REQUEST_DENIED = "REQUEST_DENIED"
        const val OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT"
        const val UNKNOWN_ERROR = "UNKNOWN_ERROR"
        const val INVALID_REQUEST = "INVALID_REQUEST"

        const val GOOGLE_BROWSER_API_KEY = "AIzaSyByPm_M8iKQpDqteolcEYtz4YcfKDFZL_M"
        const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
        const val PROXIMITY_RADIUS = 5000 // meters
        const val TYPE = "restaurant"
        const val KEYWORD = "restaurant"

        // The minimum distance to change Updates in meters
        const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // meters

        // The minimum time between updates in minutes
        const val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // minutes

        var favoriteRestaurants: ArrayList<Result> = ArrayList()
    }
}