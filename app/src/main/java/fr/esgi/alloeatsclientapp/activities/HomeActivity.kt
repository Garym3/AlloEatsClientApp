package fr.esgi.alloeatsclientapp.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import fr.esgi.alloeatsclientapp.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Dialing AlloEats...", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val btnLogout = findViewById<Button>(R.id.logout_button)

        btnLogout.setOnClickListener({
            // Facebook Logout
            if (AccessToken.getCurrentAccessToken() != null) {
                GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/",
                        null, HttpMethod.DELETE, GraphRequest.Callback {
                    AccessToken.setCurrentAccessToken(null)
                    LoginManager.getInstance().logOut()

                    finish()
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                }).executeAsync()
            } else {
                // Standard logout
                finish()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
        })
    }
}
