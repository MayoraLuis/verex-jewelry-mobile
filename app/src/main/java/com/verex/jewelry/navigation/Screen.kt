package com.verex.jewelry.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Cart : Screen("cart")
    object ClientOrders : Screen("client_orders")
    object Admin : Screen("admin")

    object Quoter : Screen("quoter/{joyaId}") {
        fun createRoute(joyaId: String) = "quoter/$joyaId"
    }
}