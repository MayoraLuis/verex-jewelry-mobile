package com.verex.jewelry.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Cart : Screen("cart")
    object Admin : Screen("admin")

    // Ruta base con parámetro para la definición en NavHost y función creadora
    object Quoter : Screen("quoter/{joyaId}") {
        fun createRoute(joyaId: String) = "quoter/$joyaId"
    }
}