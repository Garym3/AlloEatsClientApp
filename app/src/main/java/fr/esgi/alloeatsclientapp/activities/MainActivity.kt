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
import android.os.Looper
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
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.inaka.killertask.KillerTask
import fr.esgi.alloeatsclientapp.business.NearbyPlacesBuilder
import fr.esgi.alloeatsclientapp.utils.Google
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, LocationListener{
    private val GPS_INTERNET_LOCATION_PERIMISSIONS_REQUEST_CODE = 1
    private val tag = "MainActivity"
    private var mLocationManager: LocationManager? = null
    private var mLocation: Location? = Location("")
    private var shouldUpdate = true

    private lateinit var restaurantAdapter: RestaurantAdapter

    @BindView(R.id.restaurantsList)
    lateinit var restaurantListView: ListView

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

        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        requestLocation()

        findViewById<FloatingActionButton>(R.id.refreshButton).setOnClickListener { _ ->
            /*if(!restaurantAdapter.isEmpty && restaurantListView.count > 0) {
                restaurantListView.smoothScrollToPosition(0)
            }*/
            KillerTask(
                    {
                        while (restaurantListView.count <= 0 && !isLocationEnabled()){}
                        this@MainActivity.runOnUiThread({
                            requestLocation()
                        })
                    },
                    {
                        getNearbyRestaurants()
                        Toast.makeText(applicationContext,
                                "Refreshing...",
                                Toast.LENGTH_SHORT).show()
                    },
                    {
                        Log.e(tag, it?.message)
                    }).go()
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
        val usernameTextView = headerView.findViewById<TextView>(R.id.username_textView)
        val emailTextView = headerView.findViewById<TextView>(R.id.email_textView)

        setDisplayedCredentials(isStandardAccount, usernameTextView, emailTextView)
    }

    override fun onStart() {
        super.onStart()

        if (!hasLocationPermissions()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), GPS_INTERNET_LOCATION_PERIMISSIONS_REQUEST_CODE)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            GPS_INTERNET_LOCATION_PERIMISSIONS_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    requestLocation()
                    return
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

        val googlePlacesUrl =
                StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        .append("location=").append(mLocation?.latitude).append(",").append(mLocation?.longitude)
        .append("&radius=").append(Google.PROXIMITY_RADIUS)
        .append("&type=").append(Google.TYPE)
        .append("&keyword=").append(Google.KEYWORD)
        .append("&key=${Google.GOOGLE_BROWSER_API_KEY}")
        Log.i("Google API Request", googlePlacesUrl.toString())

        val queue: RequestQueue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, googlePlacesUrl.toString(), null,
                Response.Listener<JSONObject> { response ->
                    Log.i(tag, "onResponse: Result= " + response.toString())
                    val gson = GsonBuilder().serializeNulls().create()
                    val npBuilder = gson.fromJson(JsonParser().parse(response.toString()), NearbyPlacesBuilder::class.java)

                    if(!npBuilder.status.equals("OK")) return@Listener

                    restaurantAdapter = RestaurantAdapter(applicationContext, ArrayList(npBuilder.restaurants))
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
        return mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || mLocationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
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
