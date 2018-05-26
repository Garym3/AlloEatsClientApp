package fr.esgi.alloeatsclientapp.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnItemClick
import com.inaka.killertask.KillerTask
import com.jaychang.sa.SocialUser
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.api.users.SocialUserAuth
import fr.esgi.alloeatsclientapp.business.Finder
import fr.esgi.alloeatsclientapp.business.PlaceAutocomplete
import fr.esgi.alloeatsclientapp.business.adapters.OrderAdapter
import fr.esgi.alloeatsclientapp.business.adapters.RestaurantAdapter
import fr.esgi.alloeatsclientapp.fragments.IOnCodePassListener
import fr.esgi.alloeatsclientapp.fragments.RestaurantCardDialogFragment
import fr.esgi.alloeatsclientapp.models.google.details.Result
import fr.esgi.alloeatsclientapp.utils.Global
import fr.esgi.alloeatsclientapp.utils.Google
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
        IOnCodePassListener {

    private val TAG = "MainActivity"
    private lateinit var mFinder: Finder
    private var mPlaceAutocomplete: PlaceAutocomplete? = null

    @BindView(R.id.restaurantsList)
    lateinit var mainListView: ListView

    @OnItemClick(R.id.restaurantsList)
    internal fun onItemClick(position: Int) {
        Google.selectedRestaurant = mFinder.mMainAdapter!!.getItem(position) as Result
        mFinder.showClickedRestaurantAlert()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.setDebug(true)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(Color.parseColor("#ffb200"))

        mFinder = Finder(this@MainActivity, fragmentManager, mainListView)

        mFinder.mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mFinder.requestLocation()

        setRefreshButton()

        setActionBarDrawer()

        setNavigationView()

        setDisplayedCredentials()

        mPlaceAutocomplete = PlaceAutocomplete(mFinder)
        mPlaceAutocomplete!!.setPlaceAutocompleteFragment()
    }

    override fun onStart() {
        super.onStart()

        if(mFinder.checkLocation()){
            mFinder.requestLocation()
            mFinder.mShouldUpdate = true
        }
     }

    override fun onStop() {
        super.onStop()
        mFinder.mShouldUpdate = true
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
        mFinder.whenOnRequestCode(requestCode, grantResults)
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
                refresh_button.show()
                startActivity(Intent(this, RestaurantPickerActivity::class.java))
            }
            R.id.nav_restaurantsList -> {
                refresh_button.show()
                mFinder.mShouldUpdate = true
                mFinder.getNearbyRestaurants()
            }
            R.id.nav_orders -> {
                if (!setNavOrdersResult()) return false
            }
            R.id.nav_favorites -> {
                if (!setNavFavoritesResult()) return false
            }
            R.id.nav_logout -> {
                finish()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCodePass(code: Int) {
        if(Google.selectedRestaurant == null) return

        if(code == Global.codeShowRestaurantPage){
            val fragment = RestaurantCardDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable("mSelectedRestaurant", Google.selectedRestaurant)
            fragment.arguments = bundle
            fragment.show(fragmentManager, "RestaurantCardDialogFragment")
        } else if (code == Global.codeAddRestaurantToFavorite){
            if(Global.favoriteRestaurants.contains(Google.selectedRestaurant!!)) {
                Toast.makeText(this, "This restaurant is already in your favorites"
                        , Toast.LENGTH_SHORT).show()
                return
            }
            Global.favoriteRestaurants.add(Google.selectedRestaurant!!)
        }
    }

    private fun setNavOrdersResult(): Boolean {
        if (Global.myOrders.size <= 0) {
            Toast.makeText(this, "No orders yet", Toast.LENGTH_SHORT).show()
            return false
        }
        refresh_button.hide()
        mFinder.mShouldUpdate = false

        mainListView.adapter = null
        mFinder.mMainAdapter = OrderAdapter(this, Global.myOrders)
        mainListView.adapter = mFinder.mMainAdapter
        return true
    }

    private fun setNavFavoritesResult(): Boolean {
        if (Global.favoriteRestaurants.size <= 0) {
            Toast.makeText(this, "No favorite restaurants yet"
                    , Toast.LENGTH_SHORT).show()
            return false
        }
        refresh_button.hide()
        mFinder.mShouldUpdate = false

        mainListView.adapter = null
        mFinder.mMainAdapter = RestaurantAdapter(this, Global.favoriteRestaurants)
        mainListView.adapter = mFinder.mMainAdapter
        return true
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
        refresh_button.setOnClickListener { _ ->
            KillerTask(
                    {
                        while (mainListView.count <= 0 && !mFinder.isLocationEnabled()) {}
                        this@MainActivity.runOnUiThread({
                            mFinder.requestLocation()
                        })
                    },
                    {
                        mFinder.getNearbyRestaurants()
                    },
                    {
                        Log.e(TAG, it?.message)
                    }).go()
        }
    }

    private fun setDisplayedCredentials() {
        val isStandardAccount = Global.CurrentUser.user != null

        val headerView = nav_view.getHeaderView(0)

        val socialUser: SocialUser? =
                if (!isStandardAccount)
                    intent.getParcelableExtra("socialUser") as SocialUser
                else null

        headerView.findViewById<TextView>(R.id.username_textView).text =
                when {
                    isStandardAccount -> Global.CurrentUser.user?.username
                    socialUser?.username != null -> socialUser.username
                    else -> socialUser?.fullName
                }

        headerView.findViewById<TextView>(R.id.email_textView).text =
                if (isStandardAccount) Global.CurrentUser.user?.mailAddress
                else socialUser?.email
    }

    private fun disconnectUser(){
        if(Global.CurrentUser.user != null) {
            Global.CurrentUser.user = null
            return
        }
        when (SocialUserAuth.usedSocialNetwork){
            SocialUserAuth.FACEBOOK -> {
                SocialUserAuth.disconnect(this@MainActivity, SocialUserAuth.FACEBOOK)
            }
            SocialUserAuth.GOOGLE -> {
                SocialUserAuth.disconnect(this@MainActivity, SocialUserAuth.GOOGLE)
            }
            SocialUserAuth.TWITTER -> {
                SocialUserAuth.disconnect(this@MainActivity, SocialUserAuth.TWITTER)
            }
        }
    }
}
