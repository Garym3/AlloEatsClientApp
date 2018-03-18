package fr.esgi.alloeatsclientapp.utils


class Global {
    companion object {
        const val emailPattern = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        const val passwordMinLength = 8
        const val passwordRequiredChars = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"

    }
}