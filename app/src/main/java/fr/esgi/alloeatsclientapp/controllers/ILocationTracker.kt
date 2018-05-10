package fr.esgi.alloeatsclientapp.controllers

import android.location.Location

interface ILocationTracker {

    val location: Location?

    val possiblyStaleLocation: Location?

    interface LocationUpdateListener {
        fun onUpdate(oldLoc: Location, oldTime: Long, newLoc: Location, newTime: Long)
    }

    fun start()

    fun start(update: LocationUpdateListener)

    fun stop()

    fun hasLocation(): Boolean

    fun hasPossiblyStaleLocation(): Boolean
}
