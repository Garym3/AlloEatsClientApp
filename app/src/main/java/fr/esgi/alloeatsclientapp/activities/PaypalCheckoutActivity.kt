package fr.esgi.alloeatsclientapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.Toast

import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import fr.esgi.alloeatsclientapp.R
import org.json.JSONException

import org.json.JSONObject
import java.math.BigDecimal

public class PaypalCheckoutActivity : AppCompatActivity() {

    private val requestCodePayment = 1

    private var paymentButton: Button? = null
    private var paypalId: String? = null
    private var paypalState: String? = null
    private var paypalAmount: String? = null
    private var paypalCurrencyCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        paymentButton = findViewById(R.id.paymentButton)
        paymentButton!!.setOnClickListener({onBuyPressed()})
    }

    public fun onBuyPressed() {
        val thingToBuy: PayPalPayment = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE)
        val intent = Intent(this@PaypalCheckoutActivity, PaymentActivity::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, "") //TODO
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy)
        startActivityForResult(intent, requestCodePayment)
    }

    private fun getThingToBuy(paymentIntent: String) : PayPalPayment {
        return PayPalPayment(BigDecimal("1"), "USD", "sample item  ", paymentIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == requestCodePayment) {
            if (resultCode == Activity.RESULT_OK) {
                val confirm: PaymentConfirmation =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                try {
                    Log.e("Show", confirm.toJSONObject().toString(4))
                    Log.e("Show", confirm.payment.toJSONObject().toString(4))

                    val json: JSONObject = confirm.toJSONObject()
                    val response: JSONObject = json.getJSONObject ("response")
                    paypalId = response.getString("id")
                    paypalState = response.getString("state")
                    val payment: JSONObject = confirm.payment.toJSONObject()
                    paypalAmount = payment.getString("amount")
                    paypalCurrencyCode = payment.getString("currency_code")

                    Toast.makeText(applicationContext, "PaymentConfirmation info received"
                            + " from PayPal", Toast.LENGTH_LONG).show()
                } catch (e: JSONException) {
                    Toast.makeText(applicationContext, "An extremely unlikely failure" +
                    " occurred:" + e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

