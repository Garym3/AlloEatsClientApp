package fr.esgi.alloeatsclientapp.utils

import fr.esgi.alloeatsclientapp.models.User


class Global {
    companion object {
        const val emailPattern = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        const val passwordMinLength = 8
        const val passwordRequiredChars = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"

        val currentConnectedUser = User("test@gmail.com", "password1", "Bob", "Marley", "0000000000", "City", "Address", "00000", "France")
    }
}