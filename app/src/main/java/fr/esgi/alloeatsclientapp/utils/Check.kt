package fr.esgi.alloeatsclientapp.utils

import java.util.regex.Pattern

/**
 * Created by Olivier on 17/03/2018.
 */

class Check {
    companion object {
        fun emailStructure(email : String): Boolean {
            return Pattern.compile(Global.emailPattern).matcher(email).matches()
        }

        fun passwordStructure(password : String): Boolean {
            return passwordLength(password) && passwordRequiredChars(password)

        }

        private fun passwordLength(password : String): Boolean {
            return password.length >= Global.passwordMinLength
        }

        private fun passwordRequiredChars(password : String): Boolean {
            val pattern = Regex(Global.passwordRequiredChars)
            return password.matches(pattern)
        }
    }
}
