package com.verex.jewelry.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.verex.jewelry.data.VerexRepository
import com.verex.jewelry.ui.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { rol: String ->
                    val destino = if (rol.trim().equals("admin", ignoreCase = true)) {
                        Screen.Admin.route
                    } else {
                        Screen.Home.route
                    }
                    navController.navigate(destino) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToQuoter = { joyaId: String ->
                    navController.navigate(Screen.Quoter.createRoute(joyaId))
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                },
                onNavigateToOrders = {
                    navController.navigate(Screen.ClientOrders.route)
                },
                onLogout = {
                    VerexRepository.cerrarSesion()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("historial") {
            HistorialScreen(
                onNavigateBack = {
                    // Regresa a la pantalla anterior (ej. Home o Carrito)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Quoter.route,
            arguments = listOf(navArgument("joyaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val joyaId = backStackEntry.arguments?.getString("joyaId") ?: ""
            val joyaSeleccionada = VerexRepository.buscarJoyaPorId(joyaId) ?: VerexRepository.catalogoJoyas.first()

            QuoterScreen(
                joya = joyaSeleccionada,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOrders = {
                    navController.navigate(Screen.ClientOrders.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(Screen.ClientOrders.route) {
            HistorialScreen (
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Admin.route) {
            AdminScreen(
                onCerrarSesion = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}