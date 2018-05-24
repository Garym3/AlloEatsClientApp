package fr.esgi.alloeatsclientapp.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.inaka.killertask.KillerTask
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.api.users.UserAuth
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.app_bar_main.*

class CreateAccountActivity : AppCompatActivity() {

    private var userAuth : UserAuth? = UserAuth()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        toolbar.setBackgroundColor(Color.parseColor("#ffb200"))

        // Hides keyboard on focus only
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        val inputFields = listOf<EditText>(
                emailInput, passwordInput, phoneNumberInput, countryInput, cityInput,
                addressInput, zipCodeInput, firstNameInput, lastNameInput
        )

        registerButton.setOnClickListener({
            for (inputField: EditText? in inputFields) {
                if (inputField == null || inputField.length() <= 0) {
                    Toast.makeText(this, "All fields must be filled.",
                            Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            KillerTask(
                {
                    userAuth?.createAccount(this@CreateAccountActivity,
                            toEditTextStrings(inputFields))
                },
                {
                    Toast.makeText(this, "Your account has been created!",
                            Toast.LENGTH_SHORT).show()
                    Log.i("CreateAccountActivity", "Account created.")
                }).go()
        })

        abortButton.setOnClickListener({
            finish()
        })
    }

    private fun toEditTextStrings(inputFields: List<EditText>?): List<String>{
        val inputFieldsTexts: MutableList<String>? = null

        for (inputField: EditText in inputFields!!){
            inputFieldsTexts?.add(inputField.text.toString())
        }

        return inputFieldsTexts?.toList()!!
    }
}
