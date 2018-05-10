package fr.esgi.alloeatsclientapp.activities

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import fr.esgi.alloeatsclientapp.R


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private var mGeoDataClient: GeoDataClient? = null
    private var mPlaceDetectionClient: PlaceDetectionClient? = null
    private var mGoogleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
                "A connection error has occurred. P" +
                        "lease enable internet or restart the application if necessary.",
                Toast.LENGTH_LONG).show()
    }

    private val REQUESTINT: Int = 177

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

        // Enable current location
        try {
            configureLocation(mMap)
        } catch (ex: SecurityException){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                ), REQUESTINT)

                configureLocation(mMap)
            } else {
                Toast.makeText(this, "Insufficient permissions", Toast.LENGTH_SHORT)
                        .show()
                finish()
            }
        }
    }

    @Throws(SecurityException::class)
    private fun configureLocation(mMap: GoogleMap){
        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    // Got last known location. In some rare situations this can be null.
                    currentLocation = location
                    val position = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)

                    mMap.addMarker(MarkerOptions().position(position).title("You are here"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(position))
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14.0f))
                }.addOnFailureListener { Log.e("addOnFailureListener", "Failure") }
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun getCurrentLocationSettings(): LocationSettingsRequest.Builder{
        return LocationSettingsRequest.Builder()
                .addLocationRequest(createLocationRequest())
    }
}
