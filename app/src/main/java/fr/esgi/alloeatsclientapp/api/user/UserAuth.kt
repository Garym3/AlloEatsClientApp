package fr.esgi.alloeatsclientapp.api.user

import android.content.Context
import android.content.Intent
import android.util.Log

import com.android.volley.Request
import com.android.volley.VolleyError
import fr.esgi.alloeatsclientapp.activities.CreateAccountActivity
import fr.esgi.alloeatsclientapp.activities.MainActivity

import org.json.JSONException
import org.json.JSONObject

import fr.esgi.alloeatsclientapp.api.CallbackEngine
import fr.esgi.alloeatsclientapp.api.RequestEngine
import fr.esgi.alloeatsclientapp.models.User
import fr.esgi.alloeatsclientapp.utils.Global


class UserAuth : RequestEngine() {

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
                    object : CallbackEngine {
                        override fun onSuccessResponse(result: JSONObject) {
                            try {
                                when {
                                    result.getString("result").equals("Wrong Password", ignoreCase = true) -> {
                                        Log.e("onSuccessResponse:Failure", "WsPassword is incorrect")
                                    }
                                    result.getBoolean("result") -> {
                                        Log.i("onSuccessResponse:Success", "Account does exist")

                                        Global.CurrentUser.user = User(username, "a.a@gmail.com",
                                                "FirstName", "LastName",
                                                "0000000000", "City",
                                                "Address", "00000", "France")

                                        Log.i("onSuccessResponse:Success", "Account retrieved")

                                        context.startActivity(Intent(context, MainActivity::class.java))
                                    }
                                    else -> {
                                        Log.i("onSuccessResponse:Failure", "Account doesn't exist")

                                        context.startActivity(Intent(context, CreateAccountActivity::class.java))
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("onSuccessResponse:Failure", e.message)
                            }
                        }

                        override fun onErrorResponse(error: VolleyError) {
                            if (error.networkResponse == null) return

                            val errorData = String(error.networkResponse.data)
                            Log.e("onErrorResponse", errorData)
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
    fun createAccount(context: Context, fields: List<String>?) {

        val contentNode = JSONObject()

        contentNode.put("username", fields?.get(0))
        contentNode.put("password", fields?.get(1))
        contentNode.put("mail", fields?.get(0)) // mail == username (for the moment)
        contentNode.put("city", fields?.get(2))
        contentNode.put("address", fields?.get(3))
        contentNode.put("zipcode", fields?.get(4))
        contentNode.put("country", fields?.get(5))
        contentNode.put("number", fields?.get(6))
        contentNode.put("firstname", fields?.get(7))
        contentNode.put("lastname", fields?.get(8))
        contentNode.put("wspassword", Global.wsPassword)

        try {
            readFromUrl(route + "createAccount", contentNode, Request.Method.POST, context,
                    object : CallbackEngine {
                        override fun onSuccessResponse(result: JSONObject) {
                            try {
                                if (result.getBoolean("result")) {
                                    Global.CurrentUser.user = User(fields!![0], fields[1], fields[0],
                                            fields[2], fields[3], fields[4], fields[5], fields[6],
                                            fields[7])

                                    Log.i("response", "Account successfully created")

                                    context.startActivity(Intent(context, MainActivity::class.java))
                                } else {
                                    Log.e("onSuccessResponse:Failure", "Couldn't create account.")
                                }
                            } catch (e: Exception) {
                                Log.e("onSuccessResponse:Failure", e.message)
                            }
                        }

                        override fun onErrorResponse(error: VolleyError) {
                            if (error.networkResponse == null) return

                            val errorData = String(error.networkResponse.data)
                            Log.e("onErrorResponse", errorData)
                        }
                    })
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }
}
