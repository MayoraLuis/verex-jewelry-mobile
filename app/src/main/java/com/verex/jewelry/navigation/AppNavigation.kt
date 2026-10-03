package com.verex.jewelry.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.verex.jewelry.data.VerexRepository
import com.verex.jewelry.model.RolUsuario
import com.verex.jewelry.ui.screens.AdminScreen
import com.verex.jewelry.ui.screens.CartScreen
import com.verex.jewelry.ui.screens.HomeScreen
import com.verex.jewelry.ui.screens.LoginScreen
import com.verex.jewelry.ui.screens.QuoterScreen
import com.verex.jewelry.ui.screens.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    val usuario = VerexRepository.usuarioActual
                    if (usuario?.rol == RolUsuario.ADMINISTRADOR) {
                        navController.navigate(Screen.Admin.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
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
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToQuoter = { joyaId ->
                    navController.navigate(Screen.Quoter.createRoute(joyaId))
                },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Quoter.route,
            arguments = listOf(
                navArgument("joyaId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val joyaId = backStackEntry.arguments?.getString("joyaId") ?: ""
            val joyaSeleccionada = VerexRepository.buscarJoyaPorId(joyaId)

            QuoterScreen(
                joya = joyaSeleccionada,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                onNavigateBack = { navController.popBackStack() },
                onOrderConfirmed = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Admin.route) {
            AdminScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}