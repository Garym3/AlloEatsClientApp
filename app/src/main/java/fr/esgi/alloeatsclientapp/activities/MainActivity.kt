package fr.esgi.alloeatsclientapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import com.jaychang.sa.SocialUser
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.api.user.SocialUserAuth
import fr.esgi.alloeatsclientapp.models.Restaurant
import fr.esgi.alloeatsclientapp.utils.Global
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import fr.esgi.alloeatsclientapp.utils.CustomAdapter
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnItemClick
import com.google.android.gms.maps.model.LatLng


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var restaurants: ArrayList<Restaurant>? = null
    private lateinit var restaurantAdapter: CustomAdapter

    @BindView(R.id.restaurantsList)
    lateinit var restaurantListView: ListView

    @OnItemClick(R.id.restaurantsList)
    internal fun onItemClick(position: Int) {
        Toast.makeText(this, "You clicked: " + restaurantAdapter.getItem(position),
                LENGTH_SHORT).show()
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.setDebug(true)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Hello", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
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

        setRestaurantListView()
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

    private fun setRestaurantListView(){
        val resto1 = Restaurant("1", "Resto1", "",
                true, 5, "2 impasse des rues", LatLng(10.0, 20.0))

        val resto2 = Restaurant("2", "Resto2", "",
                false, 4, "2 impasse des impasses", LatLng(15.0, 20.0))

        //val restos: ArrayList<Restaurant> = arrayListOf(resto1, resto2)

        restaurantAdapter = CustomAdapter(applicationContext, arrayListOf(resto1, resto2))
        //restaurantListView = findViewById(R.id.restaurantsList)
        restaurantListView.adapter = restaurantAdapter
    }

    private fun setNearbyRestaurants(){
        restaurantListView = findViewById(R.id.restaurantsList)

        restaurants?.add(Restaurant("id", "Marly Pizza", "photo",
                true, 3, "address", LatLng(10.0, 10.0)))

        restaurantAdapter = CustomAdapter(applicationContext, restaurants!!)

        restaurantListView.adapter = restaurantAdapter
        restaurantListView.onItemClickListener = AdapterView.OnItemClickListener {
            _, _, position, _ ->

            val restaurant = restaurants?.get(position)

            //TODO: Bring to RestaurantMenuActivity


        }
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
