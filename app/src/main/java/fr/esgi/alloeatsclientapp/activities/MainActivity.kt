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
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
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
import fr.esgi.alloeatsclientapp.api.users.SocialUserAuth
import fr.esgi.alloeatsclientapp.business.adapters.OrderAdapter
import fr.esgi.alloeatsclientapp.business.builders.NearbyPlacesBuilder
import fr.esgi.alloeatsclientapp.business.adapters.RestaurantAdapter
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
    private val TAG = "MainActivity"
    private var mLocationManager: LocationManager? = null
    private var mLocation: Location? = Location("")
    private var shouldUpdate = true
    private var selectedRestaurant: Result? = null
    private lateinit var mainAdapter: BaseAdapter

    @BindView(R.id.restaurantsList)
    lateinit var mainListView: ListView

    @OnItemClick(R.id.restaurantsList)
    internal fun onItemClick(position: Int) {
        selectedRestaurant = mainAdapter.getItem(position) as Result
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
        val refreshButton: FloatingActionButton = findViewById(R.id.refreshButton)

        when (item.itemId) {
            R.id.nav_finder -> {
                refreshButton.show()
                startActivity(Intent(this, MapsActivity::class.java))
            }
            R.id.nav_restaurantsList -> {
                refreshButton.show()
                shouldUpdate = true
                getNearbyRestaurants()
            }
            R.id.nav_orders -> {
                if(Global.myOrders.size <= 0) {
                    Toast.makeText(this, "No orders yet" , Toast.LENGTH_SHORT).show()
                    return false
                }
                refreshButton.hide()
                shouldUpdate = false

                mainListView.adapter = null
                mainAdapter = OrderAdapter(this, Global.myOrders)
                mainListView.adapter = mainAdapter
            }
            R.id.nav_favorites -> {
                if(Global.favoriteRestaurants.size <= 0) {
                    Toast.makeText(this, "No favorite restaurants yet"
                            , Toast.LENGTH_SHORT).show()
                    return false
                }
                refreshButton.hide()
                shouldUpdate = false

                mainListView.adapter = null
                mainAdapter = RestaurantAdapter(this, Global.favoriteRestaurants)
                mainListView.adapter = mainAdapter
            }
            R.id.nav_logout -> {
                finish()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCodePass(code: Int) {
        if(selectedRestaurant == null) return

        if(code == Global.codeShowRestaurantPage){
            val fragment = RestaurantPageDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable("selectedRestaurant", selectedRestaurant)
            fragment.arguments = bundle
            fragment.show(fragmentManager, "RestaurantPageDialogFragment")
        } else if (code == Global.codeAddRestaurantToFavorite){
            if(Global.favoriteRestaurants.contains(selectedRestaurant!!)) {
                Toast.makeText(this, "This restaurant is already in your favorites"
                        , Toast.LENGTH_SHORT).show()
                return
            }
            Global.favoriteRestaurants.add(selectedRestaurant!!)
        }
    }

    private fun setNavigationView() {
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.itemIconTintList = null
        nav_view.setCheckedItem(R.id.nav_restaurantsList)
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
                        while (mainListView.count <= 0 && !isLocationEnabled()) {}
                        this@MainActivity.runOnUiThread({
                            requestLocation()
                        })
                    },
                    {
                        getNearbyRestaurants()
                    },
                    {
                        Log.e(TAG, it?.message)
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
                SocialUserAuth.disconnect(this, SocialUserAuth.FACEBOOK)
                SocialUserAuth.disconnect(this, SocialUserAuth.GOOGLE)
                SocialUserAuth.disconnect(this, SocialUserAuth.TWITTER)
            } catch (e: Exception){
                try {
                    SocialUserAuth.disconnect(this, SocialUserAuth.GOOGLE)
                    SocialUserAuth.disconnect(this, SocialUserAuth.TWITTER)
                } catch (e: Exception){
                    SocialUserAuth.disconnect(this, SocialUserAuth.TWITTER)
                }
            }
        }
    }

    private fun showClickedRestaurantAlert() {
        if(selectedRestaurant == null) return

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
                Log.e(TAG, ex.message)
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
        Toast.makeText(this, "Refreshing location results...", Toast.LENGTH_SHORT)
                .show()

        if(isGpsEnabled()){
            mLocationManager?.removeUpdates(this)
            mLocationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    10000, 0f, this)
            return
        } else if(areMobileDataEnabled()){
            mLocationManager?.removeUpdates(this)
            mLocationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    10000, 0f, this)
            return
        }

        Toast.makeText(this,
                "No result found, please enable GPS or INTERNET on your phone.",
                Toast.LENGTH_SHORT).show()
    }

    private fun hasLocationPermissions(): Boolean{
        return  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
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
                    Log.i(TAG, "onResponse: Restaurant = " + response.toString())
                    val gson = GsonBuilder().serializeNulls().create()
                    val npBuilder = gson.fromJson(JsonParser().
                            parse(response.toString()), NearbyPlacesBuilder::class.java)

                    if(!npBuilder.status.equals("OK")) return@Listener

                    mainAdapter = RestaurantAdapter(this, ArrayList(npBuilder.results))
                    mainListView.adapter = mainAdapter
                },
                Response.ErrorListener { error ->
                    Log.e(TAG, "onErrorResponse: Error= $error")
                    Log.e(TAG, "onErrorResponse: Error= ${error?.message}")
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
