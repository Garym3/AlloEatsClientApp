package fr.esgi.alloeatsclientapp.api.requests

import android.content.Context
import android.util.Log
import android.widget.Toast

import com.android.volley.Request
import com.android.volley.VolleyError

import org.json.JSONException
import org.json.JSONObject

import fr.esgi.alloeatsclientapp.api.APICallback
import fr.esgi.alloeatsclientapp.api.APIRequester


class APIUser : APIRequester() {

    private val publicRoute = baseURL + "/public/accounts/"
    private val route = baseURL + "/accounts/"

    /**
     * Stores the connected Google Account in the application and logs in the user
     * @param context Context
     * @param email Email of the currently connected user
     * @param firstName Firstname of the currently connected user
     * @param lastName Lastname of the currently connected user
     * @throws JSONException JSONException describing the anomaly within the JSON object sent
     */
    @Throws(Exception::class)
    fun Login(context: Context, email: String, firstName: String,
              lastName: String) {

        val accountNode = JSONObject()
        val contentNode = JSONObject()

        contentNode.put("email", email)
        contentNode.put("lastname", lastName)
        contentNode.put("firstname", firstName)

        accountNode.put("account", contentNode)

        try {
            readFromUrl(publicRoute + "create-account", accountNode, Request.Method.POST, context,
                    object : APICallback {
                        override fun onSuccessResponse(result: JSONObject) {
                            try {
                                /*if (result.getBoolean("result")) {
                                    (context as Logon).finishAffinity()
                                    val intent = Intent(context, Home::class.java)

                                    GlobalVariables.CurrentAccount = Account(lastName, firstName, email,
                                            result.getString("token"))

                                    (context as Logon).makeToast("Connexion réussie")
                                    context.startActivity(intent)
                                }*/
                            } catch (e: Exception) {
                                Log.e("onSuccessResponse:Failure", e.message)
                            }

                        }


                        override fun onErrorResponse(error: VolleyError) {
                            Toast.makeText(context, "Connexion échouée", Toast.LENGTH_SHORT).show()
                            Log.e(APIUser::class.java.name, "Callback Error :\nMessage : " + error.message)

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
