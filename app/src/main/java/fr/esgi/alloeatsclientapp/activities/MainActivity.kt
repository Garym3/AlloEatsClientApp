package fr.esgi.alloeatsclientapp.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import com.jaychang.sa.SocialUser
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.api.user.SocialUserAuth
import fr.esgi.alloeatsclientapp.utils.Global
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import fr.esgi.alloeatsclientapp.business.RestaurantAdapter
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnItemClick
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import fr.esgi.alloeatsclientapp.business.NearbyPlacesBuilder
import fr.esgi.alloeatsclientapp.models.nearbySearch.Restaurant
import fr.esgi.alloeatsclientapp.utils.Google
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{
    
    private val tag = "MainActivity"
    private lateinit var mGoogleApiClient: GoogleApiClient
    private var mLocationManager: LocationManager? = null
    private lateinit var mLocation: Location
    private var mLocationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private val updateInterval = (2 * 1000).toLong()
    private val fastestInterval: Long = 2000
    private var mCurrentLocation: Location? = null

    private lateinit var restaurantAdapter: RestaurantAdapter

    @BindView(R.id.restaurantsList)
    private lateinit var restaurantListView: ListView

    @OnItemClick(R.id.restaurantsList)
    internal fun onItemClick(position: Int) {
        Toast.makeText(this, "You clicked: " + restaurantAdapter.getItem(position),
                LENGTH_SHORT).show()
        //TODO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.setDebug(true)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        setContentView(R.layout.activity_maps)

        createLocationCallback()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        checkLocation()
        getNearbyRestaurants(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude)

        goToTopButton.setOnClickListener { _ ->
            restaurantAdapter.notifyDataSetChanged()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, 
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.itemIconTintList = null

        val isStandardAccount = Global.CurrentUser.user != null

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val usernameTextView = headerView.findViewById<TextView>(R.id.username_textview)
        val emailTextView = headerView.findViewById<TextView>(R.id.email_textview)

        setDisplayedCredentials(isStandardAccount, usernameTextView, emailTextView)
    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient.connect()
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient.isConnected) mGoogleApiClient.disconnect()
        //TODO: Remettre dans le onDestroy si bug
        disconnectUser()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_finder -> {
                startActivity(Intent(applicationContext, MapsActivity::class.java))
            }
            R.id.nav_orders -> {
                //startActivity(Intent(applicationContext, OrdersActivity::class.java))
            }
            R.id.nav_favorites -> {
                //startActivity(Intent(applicationContext, FavoritesActivity::class.java))
            }
            R.id.nav_manage -> {
                //startActivity(Intent(applicationContext, SettingsActivity::class.java))
            }
            R.id.nav_logout -> {
                finish()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setDisplayedCredentials(isStandardAccount: Boolean, usernameTextView: TextView, emailTextView: TextView) {
        val socialUser: SocialUser? =
                if (!isStandardAccount)
                    intent.getParcelableExtra("socialUser") as SocialUser
                else null

        usernameTextView.text =
                when {
                    isStandardAccount -> Global.CurrentUser.user?.username
                    socialUser?.username != null -> socialUser.username
                    else -> socialUser?.fullName
                }

        emailTextView.text =
                if (isStandardAccount) Global.CurrentUser.user?.mailAddress
                else socialUser?.email
    }

    private fun getNearbyRestaurants(latitude: Double?, longitude: Double?) {
        val type = "restaurant"
        val googlePlacesUrl =
                StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")

        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude)
        googlePlacesUrl.append("&radius=").append(Google.PROXIMITY_RADIUS)
        googlePlacesUrl.append("&types=").append(type)
        googlePlacesUrl.append("&sensor=true")
        googlePlacesUrl.append("&key=${Google.GOOGLE_BROWSER_API_KEY}")

        val queue: RequestQueue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, googlePlacesUrl.toString(), null,
                Response.Listener<JSONObject> { response ->
                    Log.i(tag, "onResponse: Result= " + response.toString())
                    val restaurants: ArrayList<Restaurant> = ArrayList()
                    val gson = GsonBuilder().serializeNulls().create()
                    val npBuilder = gson.fromJson(JsonParser().parse(response.toString()), NearbyPlacesBuilder::class.java)

                    if(!npBuilder.status.equals("OK")) return@Listener

                    restaurants.clear()
                    restaurants.addAll(npBuilder.results!!)

                    restaurantAdapter = RestaurantAdapter(applicationContext, restaurants)
                    restaurantListView.adapter = restaurantAdapter
                },
                Response.ErrorListener { error ->
                    Log.e(tag, "onErrorResponse: Error= $error")
                    Log.e(tag, "onErrorResponse: Error= " + error?.message)
                }
        )

        queue.add(request)
    }

    ///// GPS LOCATION /////

    override fun onConnectionSuspended(p0: Int) {

        Log.i(tag, "Connection Suspended")
        mGoogleApiClient.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.i(tag, "Connection failed. Error: " + connectionResult.errorCode)
    }

    override fun onLocationChanged(location: Location) {
        val msg = "Updated Location: Latitude " + location.longitude + location.longitude
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onConnected(bundle: Bundle?) {

        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return
        }

        startLocationUpdates()

        val fusedLocationProviderClient :
                FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationProviderClient.lastLocation
                .addOnSuccessListener(this, { location ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        mLocation = location
                    }
                })
    }

    /**
     * Creates a callback for receiving location events.
     */
    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                mCurrentLocation = locationResult!!.lastLocation
            }
        }
    }

    private fun checkLocation(): Boolean {
        if(!isLocationEnabled())
            showAlert()
        return isLocationEnabled()
    }

    private fun isLocationEnabled(): Boolean {
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || mLocationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'." +
                        "\nPlease Enable Location to use this app")
                .setPositiveButton("Location Settings", { _, _ ->
                    val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(myIntent)
                })
                .setNegativeButton("Cancel", { _, _ -> })

        dialog.show()
    }

    private fun startLocationUpdates() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(updateInterval)
                .setFastestInterval(fastestInterval)

        // Request location updates
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    ///// END - GPS LOCATION /////

    private fun disconnectUser(){
        if(Global.CurrentUser.user != null) {
            Global.CurrentUser.user = null
        } else {
            try {
                SocialUserAuth.disconnect(SocialUserAuth.FACEBOOK)
                SocialUserAuth.disconnect(SocialUserAuth.GOOGLE)
                SocialUserAuth.disconnect(SocialUserAuth.TWITTER)
            } catch (e: Exception){
                try {
                    SocialUserAuth.disconnect(SocialUserAuth.GOOGLE)
                    SocialUserAuth.disconnect(SocialUserAuth.TWITTER)
                } catch (e: Exception){
                    SocialUserAuth.disconnect(SocialUserAuth.TWITTER)
                }
            }
        }
    }
}
