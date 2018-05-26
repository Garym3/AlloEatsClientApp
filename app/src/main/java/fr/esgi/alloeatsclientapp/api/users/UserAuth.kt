package fr.esgi.alloeatsclientapp.api.users

import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.esgi.alloeatsclientapp.activities.CreateAccountActivity
import fr.esgi.alloeatsclientapp.activities.MainActivity
import fr.esgi.alloeatsclientapp.models.User
import fr.esgi.alloeatsclientapp.utils.Global
import org.json.JSONException
import org.json.JSONObject


class UserAuth {

    private val route : String? = "${Global.apiAddress}/API/authentication/"
    private lateinit var requestQueue: RequestQueue

    /**
     * Checks if the account already exists
     * @param context Context
     * @param username Username of the currently connected User
     * @param password Password of the currently connected User
     * @throws JSONException JSONException describing the anomaly within the JSON object sent
     */
    @Throws(Exception::class)
    fun checkAccount(context: Context, username: String, password: String) {

        val contentNode = JSONObject()

        contentNode.put("username", username)
        contentNode.put("password", password)
        contentNode.put("wsPassword", Global.wsPassword)

        requestQueue = Volley.newRequestQueue(context)

        val request = JsonObjectRequest(Request.Method.GET, route + "checkAccount",
                contentNode, Response.Listener<JSONObject> { response ->

                when {
                    response.getString("result").equals("Wrong Password", ignoreCase = true) -> {
                        Log.e("onSuccessResponse:Failure", "WsPassword is incorrect")
                    }
                    response.getBoolean("result") -> {
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
        },
                Response.ErrorListener { error ->
                    if (error.networkResponse == null) return@ErrorListener

                    val errorData = String(error.networkResponse.data)
                    Log.e("onErrorResponse", errorData)
                }
        )

        requestQueue.add(request)

        /*readFromUrl(route + "checkAccount", contentNode, Request.Method.POST, context,
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
                    })*/
    }

    /**
     * Creates a new user account
     * @param context Context
     * @param fields Fields for creating a new User
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

        requestQueue = Volley.newRequestQueue(context)

        val request = JsonObjectRequest(Request.Method.GET, route + "createAccount",
                contentNode, Response.Listener<JSONObject> { response ->

            if (response.getBoolean("result")) {
                Global.CurrentUser.user = User(fields!![0], fields[1], fields[0],
                        fields[2], fields[3], fields[4], fields[5], fields[6],
                        fields[7])

                Log.i("response", "Account successfully created")

                context.startActivity(Intent(context, MainActivity::class.java))
            } else {
                Log.e("onSuccessResponse:Failure", "Couldn't create account.")
            }
        },
                Response.ErrorListener { error ->
                    if (error.networkResponse == null) return@ErrorListener

                    val errorData = String(error.networkResponse.data)
                    Log.e("onErrorResponse", errorData)
                }
        )

        requestQueue.add(request)


        /*try {
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
        }*/
    }
}
