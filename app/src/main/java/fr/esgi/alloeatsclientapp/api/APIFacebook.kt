package fr.esgi.alloeatsclientapp.api

import android.os.Bundle
import android.util.Log
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult


class APIFacebook{
    private var callbackManager: CallbackManager? = null

    fun GetUserInfo(){
        val accessToken = AccessToken.getCurrentAccessToken()

        LoginManager.getInstance().registerCallback(callbackManager!!, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val request = GraphRequest.newMeRequest(accessToken) { jsonObject, _ ->
                    val facebookId: String?
                    val facebookName: String?
                    val facebookEmail: String?
                    val facebookPhone: String?
                    val facebookAddress: String?

                    try {
                        if (jsonObject.has("id")) {
                            facebookId = jsonObject.getString("id")
                        }

                        if (jsonObject.has("name")) {
                            facebookName = jsonObject.getString("name")
                        }

                        if (jsonObject.has("email")) {
                            facebookEmail = jsonObject.getString("email")
                        }

                        if(jsonObject.has("phone")) {
                            facebookPhone = jsonObject.getString("phone")
                        }

                        if (jsonObject.has("address")) {
                            facebookAddress = jsonObject.getString("address")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,phone,address")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                Log.d("onCancel", "Facebook onCancel.")
            }

            override fun onError(exception : FacebookException?) {
                Log.e("onError", "Facebook onError.\n" + exception?.message)
            }
        })
    }
}
