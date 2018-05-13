package fr.esgi.alloeatsclientapp.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import com.jaychang.sa.SocialUser
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.api.user.SocialUserAuth
import fr.esgi.alloeatsclientapp.models.Restaurant
import fr.esgi.alloeatsclientapp.utils.Global
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import fr.esgi.alloeatsclientapp.utils.CustomAdapter
import android.widget.AdapterView
import com.google.android.gms.maps.model.LatLng


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var restaurants: ArrayList<Restaurant>? = null
    private var listView: ListView? = null
    private var adapter: CustomAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        val hView = navigationView.getHeaderView(0)
        val usernameTextView = hView.findViewById<TextView>(R.id.username_textview)
        val emailTextView = hView.findViewById<TextView>(R.id.email_textview)

        setDisplayedValues(isStandardAccount, usernameTextView, emailTextView)
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

    private fun setDisplayedValues(isStandardAccount: Boolean, usernameTextView: TextView, emailTextView: TextView) {
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

    private fun setNearbyRestaurants(){
        listView = findViewById(R.id.restaurantsList)
        val test: List<String>? = null

        restaurants?.add(Restaurant("id", "Marly Pizza", "photo", test!!, true, 3, "address", LatLng(10.0, 10.0)))

        adapter = CustomAdapter(restaurants!!, applicationContext)

        listView!!.adapter = adapter
        listView!!.onItemClickListener = AdapterView.OnItemClickListener {
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
