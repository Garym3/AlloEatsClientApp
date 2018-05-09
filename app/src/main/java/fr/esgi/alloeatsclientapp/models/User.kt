package fr.esgi.alloeatsclientapp.models


data class User(val username: String, val mailAddress: String?, val firstName: String?,
                val lastName: String?, val phoneNumber: String, val city: String,
                val address: String, val zipCode: String, val country: String)
