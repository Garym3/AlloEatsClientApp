package fr.esgi.alloeatsclientapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.utils.Global.Companion.GEOMETRY
import fr.esgi.alloeatsclientapp.utils.Global.Companion.LATITUDE
import fr.esgi.alloeatsclientapp.utils.Global.Companion.LOCATION
import fr.esgi.alloeatsclientapp.utils.Global.Companion.LONGITUDE
import fr.esgi.alloeatsclientapp.utils.Global.Companion.NAME
import fr.esgi.alloeatsclientapp.utils.Global.Companion.VICINITY
import fr.esgi.alloeatsclientapp.utils.Google
import org.json.JSONException
import org.json.JSONObject


class RestaurantPickerActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener {
    private val TAG: String = "RestaurantPickerActivity"
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mGeoDataClient: GeoDataClient? = null
    private var mPlaceDetectionClient: PlaceDetectionClient? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var locationManager: LocationManager? = null
    private var mainCoordinatorLayout: CoordinatorLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isGooglePlayServicesAvailable()) {
            return
        }

        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mainCoordinatorLayout = (CoordinatorLayout(this)).findViewById(R.id.mainCoordinatorLayout)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        if (!locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!) {
            showLocationSettings()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this)

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this)

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build()

    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e("onConnectionFailed",
                "Error code: " + p0.errorCode.toString()+ "\n" + p0.errorMessage)

        Toast.makeText(this,
                "A connection error has occurred. " +
                        "Please enable internet or restart the application if necessary.",
                Toast.LENGTH_LONG).show()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("ObsoleteSdkInt")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable current coordinates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        showCurrentLocation()
    }

    private fun showLocationSettings() {
        val snackBar: Snackbar = Snackbar.make(findViewById(R.id.mainCoordinatorLayout),
                "Location Error: GPS Disabled!", Snackbar.LENGTH_LONG)
                .setAction("Enable", {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                })
        snackBar.setActionTextColor(Color.RED)
        snackBar.duration = Snackbar.LENGTH_INDEFINITE

        val sbView: View = snackBar.view
        val textView: TextView = sbView.findViewById(android.support.design.R.id.snackbar_text)
        textView.setTextColor(Color.YELLOW)
        snackBar.show()
    }


    private fun showCurrentLocation() {
        val criteria = Criteria()
        val bestProvider = locationManager?.getBestProvider(criteria, true)

        if (ActivityCompat.checkSelfPermission(this,
         android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                 != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val location = locationManager?.getLastKnownLocation(bestProvider)

        if (location != null) {
            onLocationChanged(location)
        }

        locationManager?.requestLocationUpdates(bestProvider, Google.MIN_TIME_BW_UPDATES,
                Google.MIN_DISTANCE_CHANGE_FOR_UPDATES, this)
    }

    private fun loadNearbyPlaces(latitude: Double?, longitude: Double?) {
        val type = "restaurant"
        val googlePlacesUrl =
                StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")

        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude)
        googlePlacesUrl.append("&radius=").append(Google.PROXIMITY_RADIUS)
        googlePlacesUrl.append("&type=").append(type)
        googlePlacesUrl.append("&key=").append(Google.GOOGLE_BROWSER_API_KEY)
        Log.i("Google API Request", googlePlacesUrl.toString())

        val queue: RequestQueue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, googlePlacesUrl.toString(), null,
                Response.Listener<JSONObject> { response ->
                    Log.i(TAG, "onResponse: GoogleRestaurant= " + response.toString())
                    parseLocationResult(response)
                },
                Response.ErrorListener { error ->
                    Log.e(TAG, "onErrorResponse: Error= $error")
                    Log.e(TAG, "onErrorResponse: Error= " + error?.message)
                }
        )

        queue.add(request)
    }

    private fun parseLocationResult(result: JSONObject?) {
        //var id: String? = null
        //var place_id: String? = null
        var placeName: String? = null
        //var reference: String? = null
        //var icon: String? = null
        var vicinity: String? = null

        var latitude: Double
        var longitude: Double

        try {
            val jsonArray = result?.getJSONArray("results")

            if (result?.getString(Google.STATUS).equals(Google.OK, ignoreCase = true)) {

                mMap.clear()

                for (i in 0 until jsonArray!!.length()) {
                    val place: JSONObject = jsonArray.getJSONObject(i)

                    //id = place.getString(RESTAURANT_ID)
                    //place_id = place.getString(PLACE_ID)

                    if (!place.isNull(NAME)) {
                        placeName = place.getString(NAME)
                    }
                    if (!place.isNull(VICINITY)) {
                        vicinity = place.getString(VICINITY)
                    }
                    latitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION).getDouble(LATITUDE)
                    longitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION).getDouble(LONGITUDE)
                    //reference = place.getString(REFERENCE)
                    //icon = place.getString(ICON)

                    val markerOptions = MarkerOptions()
                    val latLng = LatLng(latitude, longitude)
                    markerOptions.position(latLng)
                    markerOptions.title("$placeName : $vicinity")

                    mMap.addMarker(markerOptions)
                }

                Toast.makeText(baseContext,
                        jsonArray.length().toString() + " restaurants found!",
                        Toast.LENGTH_LONG).show()

            } else if (result?.getString(Google.STATUS).equals(Google.ZERO_RESULTS, ignoreCase = true)) {

                Toast.makeText(baseContext,
                        "No restaurant found in ${Google.PROXIMITY_RADIUS / 1000}KM radius!",
                        Toast.LENGTH_LONG).show()
            }

        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e(TAG, "parseLocationResult: Error = " + e.message)
        }
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val apiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode: Int = apiAvailability.isGooglePlayServicesAvailable(this)

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode,
                        Google.PLAY_SERVICES_RESOLUTION_REQUEST).show()
            } else {
                Log.i(TAG, "This device is not supported.")
                finish()
            }
            return false
        }
        return true
    }

    override fun onLocationChanged(location: Location?) {
        val latitude = location?.latitude
        val longitude = location?.longitude

        val latLng = LatLng(latitude!!, longitude!!)
        mMap.addMarker(MarkerOptions().position(latLng).title("My Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))

        loadNearbyPlaces(latitude, longitude)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String?) {}

    override fun onProviderDisabled(provider: String?) {}
}
