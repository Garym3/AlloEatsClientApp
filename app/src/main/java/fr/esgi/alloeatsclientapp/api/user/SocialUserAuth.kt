package fr.esgi.alloeatsclientapp.api.user

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.jaychang.sa.AuthCallback
import com.jaychang.sa.SimpleAuth
import com.jaychang.sa.SocialUser
import com.jaychang.sa.facebook.connectFacebook
import com.jaychang.sa.facebook.disconnectFacebook
import com.jaychang.sa.facebook.revokeFacebook
import com.jaychang.sa.google.connectGoogle
import com.jaychang.sa.google.disconnectGoogle
import com.jaychang.sa.google.revokeGoogle
import com.jaychang.sa.twitter.connectTwitter
import com.jaychang.sa.twitter.disconnectTwitter
import fr.esgi.alloeatsclientapp.activities.MainActivity
import java.util.*
import android.os.Looper




class SocialUserAuth{
    companion object {
        const val FACEBOOK = "FACEBOOK"
        const val GOOGLE = "GOOGLE"
        const val TWITTER = "TWITTER"
        const val FACEBOOK_ERROR = FACEBOOK + "_ERROR"
        const val GOOGLE_ERROR = GOOGLE + "_ERROR"
        const val TWITTER_ERROR = TWITTER + "_ERROR"
        const val FACEBOOK_SUCCESS = FACEBOOK + "_SUCCESS"
        const val GOOGLE_SUCCESS = GOOGLE + "_SUCCESS"
        const val TWITTER_SUCCESS = TWITTER + "_SUCCESS"
        const val FACEBOOK_CANCEL = FACEBOOK + "_CANCEL"
        const val GOOGLE_CANCEL = GOOGLE + "_CANCEL"
        const val TWITTER_CANCEL = TWITTER + "_CANCEL"

        fun connectFacebook(context: Context) {
            val scopes = Arrays.asList("user_location", "user_link", "public_profile")

            SimpleAuth.connectFacebook(scopes, object : AuthCallback {
                override fun onSuccess(socialUser: SocialUser) {
                    putExtraSocialUser(context, socialUser)

                    runnableMakeText(context, "Connected with Facebook account")

                    Log.i(FACEBOOK_SUCCESS, "Facebook connection success. Connected User: \n"
                            + socialUser.toString())
                }

                override fun onError(error: Throwable) {
                    runnableMakeText(context, "Error with Facebook connection")

                    Log.e(FACEBOOK_ERROR, error.message + "\n" + error.stackTrace)

                }

                override fun onCancel() {
                    Log.i(FACEBOOK_CANCEL, "Facebook connection cancelled.")
                }
            })
        }

        fun connectGoogle(context: Context) {
            val scopes = Arrays.asList(
                    "https://www.googleapis.com/auth/user.addresses.read",
                    "https://www.googleapis.com/auth/user.emails.read",
                    "https://www.googleapis.com/auth/user.phonenumbers.read",
                    "https://www.googleapis.com/auth/userinfo.email",
                    "https://www.googleapis.com/auth/userinfo.profile"
            )

            SimpleAuth.connectGoogle(scopes, object : AuthCallback {
                override fun onSuccess(socialUser: SocialUser) {
                    putExtraSocialUser(context, socialUser)

                    runnableMakeText(context, "Connected with Google Plus account")

                    Log.i(GOOGLE_SUCCESS, "Google connection success. Connected User: \n"
                            + socialUser.toString())
                }

                override fun onError(error: Throwable) {
                    runnableMakeText(context, "Error with Google Plus connection")

                    Log.e(GOOGLE_ERROR, error.message + "\n" + error.stackTrace)
                }

                override fun onCancel() {
                    Log.i(GOOGLE_CANCEL, "Google connection cancelled.")
                }
            })
        }

        fun connectTwitter(context: Context) {
            SimpleAuth.connectTwitter(object : AuthCallback {
                override fun onSuccess(socialUser: SocialUser) {
                    putExtraSocialUser(context, socialUser)

                    runnableMakeText(context, "Connected with Twitter account")

                    Log.i(TWITTER_SUCCESS, "Twitter connection success. Connected User: \n"
                            + socialUser.toString())
                }

                override fun onError(error: Throwable) {
                    runnableMakeText(context, "Error with Twitter connection.")

                    Log.e(TWITTER_ERROR, error.message + "\n" + error.stackTrace)
                }

                override fun onCancel() {
                    Log.i(TWITTER_CANCEL, "Twitter connection cancelled.")
                }
            })
        }

        fun disconnect(type: String?) {
            when (type) {
                FACEBOOK -> SimpleAuth.disconnectFacebook()
                GOOGLE -> SimpleAuth.disconnectGoogle()
                TWITTER -> SimpleAuth.disconnectTwitter()
            }

            Log.i("DISCONNECT", "Disconnected")
        }

        fun revoke(type: String?) {
            when (type) {
                FACEBOOK -> SimpleAuth.revokeFacebook()
                GOOGLE -> SimpleAuth.revokeGoogle()
                TWITTER -> { // no-op }
                }
            }

            Log.i("REVOKE", "Revoked")
        }

        private fun putExtraSocialUser(context: Context, socialUser: SocialUser) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("socialUser", socialUser)
            context.startActivity(intent)
        }

        private fun runnableMakeText(context: Context, text: String){
            Handler(Looper.getMainLooper()).post(
                    {
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                    }
            )
        }
    }
}
