package fr.esgi.alloeatsclientapp.business

/*class Finder(private val mContext: Context, private var fragmentManager: FragmentManager,
             private val mLocationManager: LocationManager?, private var mLocation: Location?,
             private var mShouldUpdate: Boolean, var mMainAdapter: BaseAdapter?,
             var mainListView: ListView?)
    : LocationListener, IOnCodePassListener {

    private val TAG = "Finder"
    private val LOCATION_PERMISSIONS_REQUEST_CODE = 1

    override fun onCodePass(code: Int) {
        if(Google.selectedRestaurant == null) return

        if(code == Global.codeShowRestaurantPage){
            val fragment = RestaurantCardDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable("selectedRestaurant", Google.selectedRestaurant)
            fragment.arguments = bundle
            fragment.show(fragmentManager, "RestaurantCardDialogFragment")
        } else if (code == Global.codeAddRestaurantToFavorite){
            if(Global.favoriteRestaurants.contains(Google.selectedRestaurant!!)) {
                Toast.makeText(mContext, "This restaurant is already in your favorites",
                        Toast.LENGTH_SHORT).show()
                return
            }
            Global.favoriteRestaurants.add(Google.selectedRestaurant!!)
        }
    }

    override fun onLocationChanged(location: Location?) {
        if(hasLocationPermissions() && isLocationEnabled() && mShouldUpdate) {
            mLocation = location
            try {
                getNearbyRestaurants()
            } catch (ex: Exception){
                Log.e(TAG, ex.message)
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, bundle: Bundle?) {
        if(status == LocationProvider.TEMPORARILY_UNAVAILABLE || status == LocationProvider.OUT_OF_SERVICE) {
            mShouldUpdate = false
        }
    }

    override fun onProviderEnabled(p0: String?) {
        requestLocation()
        mShouldUpdate = true
    }

    override fun onProviderDisabled(p0: String?) {
        mShouldUpdate = false
    }

    fun requestLocation(){
        Toast.makeText(mContext, "Refreshing location results...", Toast.LENGTH_SHORT).show()

        if(!hasLocationPermissions()) {
            requestLocationPermissions()
            while (!hasLocationPermissions()){}
            requestingLocation()
        } else {
            requestingLocation()
        }
    }

    private fun requestingLocation() {
        when {
            isGpsEnabled() -> {
                mLocationManager?.removeUpdates(this)
                try {
                    mLocationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            10000, 0f, this)
                } catch (ex: SecurityException){
                    requestLocationPermissions()
                }
            }
            areMobileDataEnabled() -> {
                mLocationManager?.removeUpdates(this)
                try {
                    mLocationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            10000, 0f, this)
                } catch (ex: SecurityException){
                    requestLocationPermissions()
                }
            }
            else -> mContext.run {
                Toast.makeText(this,
                        "No result found, please enable GPS or INTERNET on your phone.",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(mContext as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSIONS_REQUEST_CODE)
    }

    private fun hasLocationPermissions(): Boolean{
        return  ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun getNearbyRestaurants() {
        if(mLocation?.latitude == 0.0 && mLocation?.longitude == 0.0) {
            mShouldUpdate = true
            return
        }

        val googleNSUrl = buildGoogleNSUrlRequest()
        val queue: RequestQueue = Volley.newRequestQueue(mContext)

        val request = JsonObjectRequest(Request.Method.GET, googleNSUrl.toString(), null,
                Response.Listener<JSONObject> { response ->
                    Log.i(TAG, "onResponse: Restaurant = " + response.toString())
                    val gson = GsonBuilder().serializeNulls().create()
                    val npBuilder = gson.fromJson(JsonParser().
                            parse(response.toString()), NearbyPlacesBuilder::class.java)

                    if(!npBuilder.status.equals("OK")) return@Listener

                    mMainAdapter = RestaurantAdapter(mContext, ArrayList(npBuilder.results))
                    mainListView!!.adapter = mMainAdapter
                },
                Response.ErrorListener { error ->
                    Log.e(TAG, "onErrorResponse: Error= $error")
                    Log.e(TAG, "onErrorResponse: Error= ${error?.message}")
                }
        )

        queue.add(request)

        mShouldUpdate = false
    }

    private fun buildGoogleNSUrlRequest(): StringBuilder? {
        val googlePlacesUrl =
                StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
                        .append("location=").append(mLocation?.latitude).append(",").append(mLocation?.longitude)
                        .append("&radius=").append(Google.PROXIMITY_RADIUS)
                        .append("&type=").append(Google.TYPE)
                        .append("&keyword=").append(Google.KEYWORD)
                        .append("&key=${Google.GOOGLE_BROWSER_API_KEY}")

        Log.i("Google Places API Request", googlePlacesUrl.toString())

        return googlePlacesUrl
    }

    private fun showLocationPermissionAlert() {
        val dialog = AlertDialog.Builder(mContext)
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'." +
                        "\nPlease Enable Location to use this app")
                .setPositiveButton("Location Settings", { _, _ ->
                    mContext.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                })
                .setNegativeButton("Cancel", { _, _ -> })

        dialog.show()
    }

    fun checkLocation(): Boolean {
        if(!isLocationEnabled()) {
            showLocationPermissionAlert()
            return false
        }
        return isLocationEnabled()
    }

    private fun isGpsEnabled(): Boolean{
        return mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun areMobileDataEnabled(): Boolean{
        return mLocationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun isLocationEnabled(): Boolean {
        return isGpsEnabled() || areMobileDataEnabled()
    }

    fun whenOnRequestCode(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSIONS_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    requestingLocation()
                } else {
                    (mContext as Activity).finish()
                }
            }
        }
    }
}*/