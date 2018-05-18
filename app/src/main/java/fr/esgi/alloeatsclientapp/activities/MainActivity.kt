package fr.esgi.alloeatsclientapp.activities

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
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
import fr.esgi.alloeatsclientapp.business.CustomAdapter
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnItemClick
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import fr.esgi.alloeatsclientapp.business.LocationService
import fr.esgi.alloeatsclientapp.business.NearbyPlacesBuilder
import fr.esgi.alloeatsclientapp.models.nearbySearch.Restaurant
import fr.esgi.alloeatsclientapp.utils.Google
import org.json.JSONObject


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var restaurantAdapter: CustomAdapter
    private lateinit var locationCallback: LocationCallback
    private val locationRequest: LocationRequest = LocationRequest.create()

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
        setContentView(R.layout.activity_maps)

        /*locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    // Update UI with location data
                    // ...
                }
            }
        }



        locationRequest.apply { interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {

        }

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0f, LocationListener{
            override fun onLocationChanged(location: Location) {
                  Log.i("MainActivity", "Last Known Location :" + location.latitude + "," + location.longitude)
            }
        })*/
        startService(Intent(applicationContext, LocationService::class.java))

        //getNearbyRestaurants()

        goToTopButton.setOnClickListener { _ ->
            restaurantAdapter.notifyDataSetChanged()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
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

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(applicationContext, LocationService::class.java))
        disconnectUser()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        //if (requestingLocationUpdates) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        //fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun startLocationUpdates() {
        /*fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */)*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_maps -> {
                startActivity(Intent(applicationContext, MapsActivity::class.java))
            }
            R.id.nav_favorites -> {
                //startActivity(Intent(applicationContext, FavoritesActivity::class.java))
            }
            R.id.nav_manage -> {
                startActivity(Intent(applicationContext, SettingsActivity::class.java))
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
                    Log.i("MainActivity", "onResponse: Result= " + response.toString())
                    val restaurants: ArrayList<Restaurant> = ArrayList()
                    val gson = GsonBuilder().serializeNulls().create()
                    val npBuilder = gson.fromJson(JsonParser().parse(response.toString()), NearbyPlacesBuilder::class.java)

                    if(!npBuilder.status.equals("OK")) return@Listener

                    restaurants.addAll(npBuilder.results!!)

                    restaurantAdapter = CustomAdapter(applicationContext, restaurants)
                    restaurantListView.adapter = restaurantAdapter
                },
                Response.ErrorListener { error ->
                    Log.e("MainActivity", "onErrorResponse: Error= $error")
                    Log.e("MainActivity", "onErrorResponse: Error= " + error?.message)
                }
        )

        queue.add(request)
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
}
