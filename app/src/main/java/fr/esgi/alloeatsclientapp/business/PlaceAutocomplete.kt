package fr.esgi.alloeatsclientapp.business

/*class PlaceAutocomplete(private val mContext: Context, private val mFragmentManager: FragmentManager,
                        private val mLocation: Location?) {
    private val TAG = "PlaceAutocomplete"

    fun setPlaceAutocompleteFragment() {
        val autocompleteFragment =
                mFragmentManager.findFragmentById(R.id.place_autocomplete_fragment)
                        as PlaceAutocompleteFragment
        val typeFilter =
                AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                        .setCountry("FR")
                        .build()

        autocompleteFragment.setHint("Find a restaurant!")
        autocompleteFragment.setBoundsBias(LatLngBounds(
                LatLng(mLocation!!.latitude - 1, mLocation.longitude - 1),
                LatLng(mLocation.latitude + 1, mLocation.longitude + 1)
        ))
        autocompleteFragment.setFilter(typeFilter)
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                getSelectedAutocompletePlace(place)

                Log.i(TAG, "Place: ${place.name}")
            }

            override fun onError(status: Status) {
                Toast.makeText(mContext, "An error occurred.",
                        Toast.LENGTH_SHORT).show()

                Log.i(TAG, "An error occurred: $status")
            }
        })
    }

    private fun getSelectedAutocompletePlace(place: Place){
        val queue: RequestQueue = Volley.newRequestQueue(mContext)

        val googleDetailsUrl = buildGoogleDetailsUrlRequest(place.id)

        val request = JsonObjectRequest(Request.Method.GET, googleDetailsUrl.toString(),
                null, Response.Listener<JSONObject> { response ->

            Log.i(TAG, "onResponse - Place = $response")
            val gson = GsonBuilder().serializeNulls().create()
            val detailsBuilder =
                    gson.fromJson(JsonParser().parse(response.toString()),
                            DetailsBuilder::class.java)

            if (!detailsBuilder.status.equals("OK")) return@Listener

            Google.selectedRestaurant = detailsBuilder.result
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
}*/