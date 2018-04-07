package fr.esgi.alloeatsclientapp.api.requests

import android.content.Context
import android.content.Intent
import android.util.Log

import com.android.volley.Request
import com.android.volley.VolleyError
import fr.esgi.alloeatsclientapp.activities.CreateAccountActivity
import fr.esgi.alloeatsclientapp.activities.MainActivity

import org.json.JSONException
import org.json.JSONObject

import fr.esgi.alloeatsclientapp.api.APICallback
import fr.esgi.alloeatsclientapp.api.APIRequester
import fr.esgi.alloeatsclientapp.models.User
import fr.esgi.alloeatsclientapp.utils.Global


class APIUser : APIRequester() {

    private val route : String? = "$apiAddress/API/authentication/"

    /**
     * Checks if the account already exists
     * @param context Context
     * @param email Email of the currently connected user
     * @param firstName Firstname of the currently connected user
     * @param lastName Lastname of the currently connected user
     * @throws JSONException JSONException describing the anomaly within the JSON object sent
     */
    @Throws(Exception::class)
    fun checkAccount(context: Context, username: String, password: String) {

        val contentNode = JSONObject()

        contentNode.put("username", username)
        contentNode.put("password", password)
        contentNode.put("wsPassword", Global.wsPassword)

        try {
            readFromUrl(route + "checkAccount", contentNode, Request.Method.POST, context,
                    object : APICallback {
                        override fun onSuccessResponse(result: JSONObject) {
                            try {
                                when {
                                    result.getString("result").equals("Wrong Password", ignoreCase = true) -> {
                                        Log.e("response", "WsPassword is incorrect")
                                    }
                                    result.getBoolean("result") -> {
                                        Log.i("response", "Account does exist")
                                        val intent = Intent(context, MainActivity::class.java)

                                        Global.currentUser = User(username, "a.a@gmail.com",
                                                "FirstName", "LastName",
                                                "0000000000", "City",
                                                "Address", "00000", "France")

                                        context.startActivity(intent)
                                    }
                                    else -> {
                                        Log.i("response", "Account doesn't exist")
                                        val intent = Intent(context, CreateAccountActivity::class.java)

                                        context.startActivity(intent)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("onSuccessResponse:Failure", e.message)

                            }
                        }

                        override fun onErrorResponse(error: VolleyError) {
                            if (error.networkResponse == null) return

                            val errorData = String(error.networkResponse.data)
                            Log.e("response", errorData)
                        }
                    })
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    /**
     * Creates a new user account
     * @param context Context
     * @param email Email of the currently connected user
     * @param firstName Firstname of the currently connected user
     * @param lastName Lastname of the currently connected user
     * @throws JSONException JSONException describing the anomaly within the JSON object sent
     */
    @Throws(Exception::class)
    fun createAccount(context: Context, username: String, password: String, mail: String,
                      city: String, address: String, zipCode: String, telNumber: String,
                      firstname: String, lastname: String) {

        val accountNode = JSONObject()
        val contentNode = JSONObject()

        contentNode.put("username", username)
        contentNode.put("password", password)
        contentNode.put("mail", mail)
        contentNode.put("city", city)
        contentNode.put("address", address)
        contentNode.put("zipcode", zipCode)
        contentNode.put("telnumber", telNumber)
        contentNode.put("firstname", firstname)
        contentNode.put("lastname", lastname)

        accountNode.put("account", contentNode)

        try {
            readFromUrl(route + "createAccount", accountNode, Request.Method.POST, context,
                    object : APICallback {
                        override fun onSuccessResponse(result: JSONObject) {
                            try {
                                if (result.getBoolean("result")) {
                                    Log.e("response", "Account successfully created")

                                }
                            } catch (e: Exception) {
                                Log.e("onSuccessResponse:Failure", e.message)
                            }
                        }

                        override fun onErrorResponse(error: VolleyError) {
                            if (error.networkResponse == null) return

                            val errorData = String(error.networkResponse.data)
                            Log.e("response", errorData)
                        }
                    })
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }
}
