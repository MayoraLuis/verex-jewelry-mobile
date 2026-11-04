package com.verex.jewelry.model


// Modelo de datos para perfiles de usuario y roles
data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val esAdmin: Boolean = false
)