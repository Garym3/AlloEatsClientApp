package fr.esgi.alloeatsclientapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnItemClick
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.inaka.killertask.KillerTask
import com.jaychang.sa.SocialUser
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.api.user.SocialUserAuth
import fr.esgi.alloeatsclientapp.business.NearbyPlacesBuilder
import fr.esgi.alloeatsclientapp.business.RestaurantAdapter
import fr.esgi.alloeatsclientapp.fragments.IOnCodePassListener
import fr.esgi.alloeatsclientapp.fragments.RestaurantItemDialogFragment
import fr.esgi.alloeatsclientapp.fragments.RestaurantPageDialogFragment
import fr.esgi.alloeatsclientapp.models.google.details.Result
import fr.esgi.alloeatsclientapp.utils.Global
import fr.esgi.alloeatsclientapp.utils.Google
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
        LocationListener, IOnCodePassListener {

    private val LOCATION_PERMISSIONS_REQUEST_CODE = 1
    private val tag = "MainActivity"
    private var mLocationManager: LocationManager? = null
    private var mLocation: Location? = Location("")
    private var shouldUpdate = true
    private var selectedGoogleRestaurant: Result? = null
    private lateinit var restaurantAdapter: RestaurantAdapter

    @BindView(R.id.restaurantsList)
    lateinit var restaurantListView: ListView

    @OnItemClick(R.id.restaurantsList)
    internal fun onItemClick(position: Int) {
        selectedGoogleRestaurant = restaurantAdapter.getItem(position)
        showClickedRestaurantAlert()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.setDebug(true)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        requestLocation()

        setRefreshButton()

        setActionBarDrawer()

        setNavigationView()

        setDisplayedCredentials()
    }

    override fun onStart() {
        super.onStart()

        if (!hasLocationPermissions()) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCATION_PERMISSIONS_REQUEST_CODE)
        } else if(checkLocation()){
            requestLocation()
            shouldUpdate = true
        }
     }

    override fun onStop() {
        super.onStop()
        shouldUpdate = true
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnectUser()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSIONS_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty()
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    requestLocation()

                } else {
                    finish()
                }
            }
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
            R.id.nav_logout -> {
                finish()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCodePass(code: Int) {
        if(code != 100) return
        if(selectedGoogleRestaurant == null) return

        val fragmentManager = fragmentManager
        val fragment = RestaurantPageDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("selectedGoogleRestaurant", selectedGoogleRestaurant)
        fragment.arguments = bundle
        fragment.show(fragmentManager, "RestaurantPageDialogFragment")
    }

    private fun setNavigationView() {
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.itemIconTintList = null
    }

    private fun setActionBarDrawer() {
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setRefreshButton() {
        findViewById<FloatingActionButton>(R.id.refreshButton).setOnClickListener { _ ->
            KillerTask(
                    {
                        while (restaurantListView.count <= 0 && !isLocationEnabled()) {
                        }
                        this@MainActivity.runOnUiThread({
                            requestLocation()
                        })
                    },
                    {
                        getNearbyRestaurants()
                        Toast.makeText(applicationContext,
                                "Refreshing...",
                                LENGTH_SHORT).show()
                    },
                    {
                        Log.e(tag, it?.message)
                    }).go()
        }
    }

    private fun setDisplayedCredentials() {
        val isStandardAccount = Global.CurrentUser.user != null

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val usernameTextView = headerView.findViewById<TextView>(R.id.username_textView)
        val emailTextView = headerView.findViewById<TextView>(R.id.email_textView)

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

    private fun showClickedRestaurantAlert() {
        if(selectedGoogleRestaurant == null) return

        val fragmentManager = fragmentManager
        RestaurantItemDialogFragment().show(fragmentManager, "RestaurantItemDialogFragment")
    }

    ///// LOCATION /////

    @SuppressLint("MissingPermission")
    override fun onLocationChanged(location: Location?) {
        if(hasLocationPermissions() && isLocationEnabled() && shouldUpdate) {
            mLocation = location
            try {
                getNearbyRestaurants()
            } catch (ex: Exception){
                Log.e(tag, ex.message)
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, bundle: Bundle?) {
        if(status == LocationProvider.TEMPORARILY_UNAVAILABLE || status == LocationProvider.OUT_OF_SERVICE) {
            shouldUpdate = false
        }
    }

    override fun onProviderEnabled(p0: String?) {
        requestLocation()
        shouldUpdate = true
    }

    override fun onProviderDisabled(p0: String?) {
        shouldUpdate = false
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation(){
        if(isGpsEnabled()){
            mLocationManager?.removeUpdates(this)
            mLocationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    10000, 0f, this)
        } else if(areMobileDataEnabled()){
            mLocationManager?.removeUpdates(this)
            mLocationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    10000, 0f, this)
        }
    }

    private fun hasLocationPermissions(): Boolean{
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun getNearbyRestaurants() {
        if(mLocation?.latitude == 0.0 && mLocation?.longitude == 0.0) {
            shouldUpdate = true
            return
        }

        val googleNSUrl = buildGoogleNSUrlRequest()

        val queue: RequestQueue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, googleNSUrl.toString(), null,
                Response.Listener<JSONObject> { response ->
                    Log.i(tag, "onResponse: GoogleRestaurant= " + response.toString())
                    val gson = GsonBuilder().serializeNulls().create()
                    val npBuilder = gson.fromJson(JsonParser().
                            parse(response.toString()), NearbyPlacesBuilder::class.java)

                    if(!npBuilder.status.equals("OK")) return@Listener

                    val nearbyRestaurants =
                            ArrayList(npBuilder.results)

                    Google.nearbyRestaurants = nearbyRestaurants

                    restaurantAdapter = RestaurantAdapter(applicationContext, nearbyRestaurants)
                    restaurantListView.adapter = restaurantAdapter
                },
                Response.ErrorListener { error ->
                    Log.e(tag, "onErrorResponse: Error= $error")
                    Log.e(tag, "onErrorResponse: Error= " + error?.message)
                }
        )

        queue.add(request)
        shouldUpdate = false
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

    private fun checkLocation(): Boolean {
        if(!isLocationEnabled()) {
            showAlert()
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

    private fun isLocationEnabled(): Boolean {
        return isGpsEnabled() || areMobileDataEnabled()
    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'." +
                        "\nPlease Enable Location to use this app")
                .setPositiveButton("Location Settings", { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                })
                .setNegativeButton("Cancel", { _, _ -> })

        dialog.show()
    }

    ///// END - LOCATION /////
}
