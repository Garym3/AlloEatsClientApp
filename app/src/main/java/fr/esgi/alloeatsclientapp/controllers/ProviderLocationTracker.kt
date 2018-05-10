package fr.esgi.alloeatsclientapp.controllers

import android.location.LocationListener
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log

class ProviderLocationTracker(context: Context, type: ProviderType) : LocationListener, ILocationTracker {

    private val lm: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var provider: String? = null

    private var lastLocation: Location? = null
    private var lastTime: Long = 0

    private var isRunning: Boolean = false

    private var listener: ILocationTracker.LocationUpdateListener? = null

    override//stale
    val location: Location?
        get() {
            if (lastLocation == null) {
                return null
            }
            return if (System.currentTimeMillis() - lastTime > 5 * MIN_UPDATE_TIME) {
                null
            } else lastLocation!!
        }

    override val possiblyStaleLocation: Location?
        get() {
            try {
                return if (lastLocation != null) {
                    lastLocation!!
                } else lm.getLastKnownLocation(provider)
            } catch (ex: SecurityException) {
                Log.e("SecurityException_GoogleMaps",
                        "User has probably not granted location permissions to the app.")
            }

            Log.w("WARNING", "Null Location returned by default.")
            return null
        }

    enum class ProviderType {
        NETWORK,
        GPS
    }

    init {
        provider = if (type == ProviderType.NETWORK) {
            LocationManager.NETWORK_PROVIDER
        } else {
            LocationManager.GPS_PROVIDER
        }
    }

    override fun start() {
        if (isRunning) {
            //Already running, do nothing
            return
        }

        //The provider is on, so start getting updates.  Update current location
        isRunning = true
        try {
            lm.requestLocationUpdates(provider, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE.toFloat(), this)
            lastLocation = null
            lastTime = 0
        } catch (ex: SecurityException) {
            Log.e("SecurityException_GoogleMaps",
                    "User has probably not granted location permissions to the app.")
        }

    }

    override fun start(update: ILocationTracker.LocationUpdateListener) {
        start()
        listener = update

    }


    override fun stop() {
        if (isRunning) {
            lm.removeUpdates(this)
            isRunning = false
            listener = null
        }
    }

    override fun hasLocation(): Boolean {
        return if (lastLocation == null) {
            false
        } else System.currentTimeMillis() - lastTime <= 5 * MIN_UPDATE_TIME
    }

    override fun hasPossiblyStaleLocation(): Boolean {
        try {
            return lastLocation != null || lm.getLastKnownLocation(provider) != null
        } catch (ex: SecurityException) {
            Log.e("SecurityException_GoogleMaps",
                    "User has probably not granted location permissions to the app.")
        }

        return false
    }

    override fun onLocationChanged(newLoc: Location) {
        val now = System.currentTimeMillis()
        if (listener != null) {
            listener!!.onUpdate(lastLocation!!, lastTime, newLoc, now)
        }
        lastLocation = newLoc
        lastTime = now
    }

    override fun onProviderDisabled(arg0: String) {

    }

    override fun onProviderEnabled(arg0: String) {

    }

    override fun onStatusChanged(arg0: String, arg1: Int, arg2: Bundle) {}

    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_UPDATE_DISTANCE: Long = 10

        // The minimum time between updates in milliseconds
        private const val MIN_UPDATE_TIME = (1000 * 60).toLong()
    }
}
