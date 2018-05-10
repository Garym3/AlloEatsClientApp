package fr.esgi.alloeatsclientapp.controllers

import android.content.Context
import android.location.Location
import android.location.LocationManager

class FallbackLocationTracker(context: Context) : ILocationTracker, ILocationTracker.LocationUpdateListener {

    private var isRunning: Boolean = false

    private val gps: ProviderLocationTracker = ProviderLocationTracker(context, ProviderLocationTracker.ProviderType.GPS)
    private val net: ProviderLocationTracker = ProviderLocationTracker(context, ProviderLocationTracker.ProviderType.NETWORK)

    private var listener: ILocationTracker.LocationUpdateListener? = null

    private var lastLoc: Location? = null
    private var lastTime: Long = 0

    override val location: Location
        get() = gps.location!!

    override val possiblyStaleLocation: Location
        get() = gps.possiblyStaleLocation!!

    override fun start() {
        if (isRunning) {
            //Already running, do nothing
            return
        }

        //Start both
        gps.start(this)
        net.start(this)
        isRunning = true
    }

    override fun start(update: ILocationTracker.LocationUpdateListener) {
        start()
        listener = update
    }


    override fun stop() {
        if (isRunning) {
            gps.stop()
            net.stop()
            isRunning = false
            listener = null
        }
    }

    override fun hasLocation(): Boolean {
        //If either has a location, use it
        return gps.hasLocation() || net.hasLocation()
    }

    override fun hasPossiblyStaleLocation(): Boolean {
        //If either has a location, use it
        return gps.hasPossiblyStaleLocation() || net.hasPossiblyStaleLocation()
    }

    override fun onUpdate(oldLoc: Location, oldTime: Long, newLoc: Location, newTime: Long) {
        var update = false

        //We should update only if there is no last location, the provider is the same, or the provider is more accurate, or the old location is stale
        when {
            lastLoc == null -> update = true
            lastLoc!!.provider == newLoc.provider -> update = true
            newLoc.provider == LocationManager.GPS_PROVIDER -> update = true
            newTime - lastTime > 5 * 60 * 1000 -> update = true
        }

        if (update) {
            if (listener != null) {
                listener!!.onUpdate(lastLoc!!, lastTime, newLoc, newTime)
            }
            lastLoc = newLoc
            lastTime = newTime
        }

    }
}
