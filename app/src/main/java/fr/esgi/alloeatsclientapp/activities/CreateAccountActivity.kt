package fr.esgi.alloeatsclientapp.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.inaka.killertask.KillerTask
import fr.esgi.alloeatsclientapp.R
import fr.esgi.alloeatsclientapp.api.users.UserAuth

class CreateAccountActivity : AppCompatActivity() {

    private var userAuth : UserAuth? = UserAuth()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        // Hides keyboard on focus only
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        val inputFields = listOf<EditText>(
                findViewById(R.id.emailInput), findViewById(R.id.passwordInput),
                findViewById(R.id.phoneNumberInput),
                findViewById(R.id.countryInput), findViewById(R.id.cityInput),
                findViewById(R.id.addressInput), findViewById(R.id.zipCodeInput),
                findViewById(R.id.firstNameInput), findViewById(R.id.lastNameInput)
        )

        val abortButton: Button? = findViewById(R.id.abortButton)
        val registerButton: Button? = findViewById(R.id.registerButton)

        registerButton?.setOnClickListener({
            for (inputField: EditText? in inputFields){
                if(inputField == null || inputField.length() <= 0){
                    Toast.makeText(this, "All fields must be filled.",
                            Toast.LENGTH_SHORT).show()
                } else {
                    KillerTask(
                        {
                            userAuth?.createAccount(this.applicationContext,
                                    toEditTextStrings(inputFields))
                        },
                        {
                            Toast.makeText(this, "Your account has been created!",
                                    Toast.LENGTH_SHORT).show()
                            Log.i("CreateAccountActivity", "Account created.")
                        }).go()
                }
            }
        })

        abortButton?.setOnClickListener({

        })
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    private fun toEditTextStrings(inputFields: List<EditText>?): List<String>{
        val inputFieldsTexts: MutableList<String>? = null

        for (inputField: EditText in inputFields!!){
            inputFieldsTexts?.add(inputField.text.toString())
        }

        return inputFieldsTexts?.toList()!!
    }
}
