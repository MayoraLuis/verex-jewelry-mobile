package com.verex.jewelry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.verex.jewelry.navigation.AppNavigation
import com.verex.jewelry.ui.theme.VerexJewelryAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VerexJewelryAppTheme {
                AppNavigation()
            }
        }
    }
}