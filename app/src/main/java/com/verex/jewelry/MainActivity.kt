package com.verex.jewelry

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.FirebaseApp
import com.verex.jewelry.navigation.AppNavigation
import com.verex.jewelry.ui.theme.VerexGreen

class MainActivity : ComponentActivity() {

    // Declarar el callback manager de Facebook accesible para la app
    lateinit var callbackManager: CallbackManager
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val info = packageManager.getPackageInfo(packageName, android.content.pm.PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = java.security.MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val androidKeyHash = android.util.Base64.encodeToString(md.digest(), android.util.Base64.DEFAULT)
                android.util.Log.d("KeyHashTest", "MI_KEY_HASH: $androidKeyHash")
            }
        } catch (e: Exception) { }

        // Inicializar el SDK de Facebook y los eventos de la aplicación
        try {
            FacebookSdk.sdkInitialize(applicationContext)
            AppEventsLogger.activateApp(application)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        callbackManager = CallbackManager.Factory.create()

        enableEdgeToEdge()
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = VerexGreen
            ) {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pasar el resultado del intent de Facebook al callback manager
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}