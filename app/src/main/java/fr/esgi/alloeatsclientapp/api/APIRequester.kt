package fr.esgi.alloeatsclientapp.api

import android.content.Context

import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.esgi.alloeatsclientapp.api.APICallback

import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.json.JSONObject

import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.HashMap
import java.util.Locale


abstract class APIRequester {

    /**
     * Build a HTTP Request and get the response of the API
     * @param URL API Route URL
     * @param content List for body HTTP Request
     */
    @Throws(Exception::class)
    protected fun readFromUrl(URL: String, content: JSONObject, methodRequest: Int, caller: Context, callback: APICallback) {
        try {
            val queue = Volley.newRequestQueue(caller)

            val request = object : JsonObjectRequest(methodRequest, URL, content,
                    ({ callback.onSuccessResponse(it) }), ({ callback.onErrorResponse(it) })
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params = HashMap<String, String>()

                    /*if (GlobalVariables.CurrentAccount != null)
                        params.put("Authorization", GlobalVariables.CurrentAccount.getToken())
                    */
                    return params
                }
            }

            queue.add(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Build a list for body HTTP Request
     * Unsupported by the API
     * @param listArguments If null create a new List, list of body HTTP Request data
     * @param contentName Name of body content
     * @param contentPOST Data of body content
     */
    fun buildPOSTList(listArguments: MutableList<NameValuePair>?, contentName: String?, contentPOST: String?): List<NameValuePair>? {
        if (listArguments == null && contentPOST == null) return null
        if (contentPOST == null || contentName == null) return listArguments
        if (listArguments == null) {
            val params = ArrayList<NameValuePair>()
            params.add(BasicNameValuePair(contentName, contentPOST))
            return params
        }
        listArguments.add(BasicNameValuePair(contentName, contentPOST))
        return listArguments
    }

    /**
     * Transform a String to Date Object
     * @param date If null create a new List, list of body HTTP Request data
     */
    @Throws(Exception::class)
    fun getDateFromString(date: String): Date {
        val format = SimpleDateFormat("yyyy-M-dd", Locale.FRANCE)
        return format.parse(date.substring(0, 10))
    }

    /**
     * Formatting body of HTTP Request
     * Unsupported by the API
     * @param params List of body content
     */
    @Throws(UnsupportedEncodingException::class)
    private fun getQuery(params: List<NameValuePair>): String {
        val result = StringBuilder()
        var first = true

        for (pair in params) {
            if (first)
                first = false
            else
                result.append("&")

            result.append(URLEncoder.encode(pair.name, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(pair.value, "UTF-8"))
        }

        return result.toString()
    }

    companion object {

        var baseURL: String? = null
    }
}
