package fr.esgi.alloeatsclientapp.activities

import android.Manifest.permission.READ_CONTACTS
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.facebook.CallbackManager
import com.inaka.killertask.KillerTask
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.api.users.SocialUserAuth
import fr.esgi.alloeatsclientapp.api.users.UserAuth
import fr.esgi.alloeatsclientapp.business.LoginRepository
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity : AppCompatActivity(), LoaderCallbacks<Cursor> {
    private var callbackManager : CallbackManager? = null
    private var loginRepository: LoginRepository? = null
    private var userAuth : UserAuth? = UserAuth()

    private var emailTextView: TextView? = null
    private var passwordTextView: TextView? = null
    private var loginFacebookButton: Button? = null
    private var loginGoogleButton: Button? = null
    private var loginTwitterButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        populateAutoComplete()

        // Hides keyboard on focus only
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        emailTextView = findViewById(R.id.email_EditText)
        passwordTextView = findViewById(R.id.password_EditText)
        loginFacebookButton = findViewById(R.id.facebookLogin_Button)
        loginGoogleButton = findViewById(R.id.googleLogin_Button)
        loginTwitterButton = findViewById(R.id.twitterLogin_Button)

        handleConnection()

        loginRepository = LoginRepository(this.applicationContext,
                emailTextView, passwordTextView)
    }

    override fun onResume() {
        super.onResume()
        loginRepository?.setCredentials(null, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }

        loaderManager.initLoader(0, null, this)
    }

    private fun mayRequestContacts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(this.emailTextView!!, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok,
                            { requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS) })
        } else {
            requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
        return false
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete()
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        if(!loginRepository?.attemptLogin()!!) return

        val emailStr: String = loginRepository?.getEmailValue()!!
        val passwordStr: String = loginRepository?.getPasswordValue()!!

        showProgress(true)
        KillerTask(
                {
                    userAuth?.checkAccount(this.applicationContext, emailStr, passwordStr)
                },
                {
                    showProgress(false)
                },
                {
                    Log.e("LoginActivity", it?.message)
                    showProgress(false)
                }).go()
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            credentials_view.visibility = if (show) View.GONE else View.VISIBLE
            credentials_view.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            credentials_view.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            loginProgress_ProgressBar.visibility = if (show) View.VISIBLE else View.GONE
            loginProgress_ProgressBar.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            loginProgress_ProgressBar.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            loginProgress_ProgressBar.visibility = if (show) View.VISIBLE else View.GONE
            credentials_view.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        return CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?",
                arrayOf(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }

    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(this@LoginActivity,
                android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        email_EditText.setAdapter(adapter)
    }

    private fun handleConnection(){
        emailSignin_Button.setOnClickListener { attemptLogin() }

        password_EditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        loginFacebookButton?.setOnClickListener({
            SocialUserAuth.connectFacebook(applicationContext)
        })

        loginGoogleButton?.setOnClickListener({
            SocialUserAuth.connectGoogle(applicationContext)
        })

        loginTwitterButton?.setOnClickListener({
            SocialUserAuth.connectTwitter(applicationContext)
        })
    }

    object ProfileQuery {
        val PROJECTION = arrayOf(
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY)
        const val ADDRESS = 0
    }

    companion object {

        /**
         * Id to identity READ_CONTACTS permission request.
         */
        private const val REQUEST_READ_CONTACTS = 0
    }
}
