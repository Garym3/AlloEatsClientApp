package fr.esgi.alloeatsclientapp.models

import com.google.android.gms.maps.model.LatLng

data class Restaurant(val id: String, val name: String, val mainPhoto: String,
                      val photos: List<String>, val isOpenedNow: Boolean, val rating: Int,
                      val address: String, val coordinates: LatLng)