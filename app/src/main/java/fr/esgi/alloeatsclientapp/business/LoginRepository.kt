package fr.esgi.alloeatsclientapp.business

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.utils.Check

public class LoginRepository(private val context: Context?, private val loginField: TextView?, private val passwordField: TextView?){

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public fun attemptLogin(): Boolean {
        // Reset
        setCredentials(null, null)

        // Store values at the time of the login attempt.
        val emailStr = loginField?.text.toString()
        val passwordStr = passwordField?.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            loginField?.error = context?.getString(R.string.error_field_required)
            focusView = loginField
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            loginField?.error = context?.getString(R.string.error_invalid_email)
            focusView = loginField
            cancel = true
        }

        // Check for a valid password.
        if (TextUtils.isEmpty(passwordStr)) {
            passwordField?.error = context?.getString(R.string.error_field_required)
            focusView = passwordField
            cancel = true
        } else if (!isPasswordValid(passwordStr)) {
            passwordField?.error = context?.getString(R.string.error_invalid_password)
            focusView = passwordField
            cancel = true
        }

        return if (cancel) {
            focusView?.requestFocus()
            false
        } else {
            true
        }
    }

    public fun setCredentials(emailValue: String?, passwordValue: String?) {
        loginField?.setText(emailValue, TextView.BufferType.EDITABLE)
        passwordField?.setText(passwordValue, TextView.BufferType.EDITABLE)
    }

    public fun isEmailValid(email: String): Boolean {
        return Check.emailStructure(email)
    }

    public fun isPasswordValid(password: String): Boolean {
        return Check.passwordStructure(password)
    }

    public fun getEmailValue(): String{
        return loginField?.text.toString()
    }

    public fun getPasswordValue(): String{
        return passwordField?.text.toString()
    }
}