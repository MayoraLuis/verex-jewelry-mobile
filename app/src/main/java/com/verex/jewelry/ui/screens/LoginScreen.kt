package com.verex.jewelry.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.verex.jewelry.AuthManager
import com.verex.jewelry.R
import com.verex.jewelry.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var estaCargando by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val authManager = remember { AuthManager() }
    val context = LocalContext.current

    Scaffold(
        containerColor = VerexGreen
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Verex",
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(110.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Iniciar Sesión",
                color = VerexWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campo de Correo Electrónico
            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    mensajeError = null
                },
                label = { Text("Correo Electrónico") },
                singleLine = true,
                enabled = !estaCargando,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerexWhite,
                    unfocusedBorderColor = VerexWhite.copy(alpha = 0.6f),
                    focusedLabelColor = VerexWhite,
                    unfocusedLabelColor = VerexWhite.copy(alpha = 0.8f),
                    focusedTextColor = VerexTextDark,
                    unfocusedTextColor = VerexTextDark,
                    focusedContainerColor = VerexInputGray,
                    unfocusedContainerColor = VerexInputGray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de Contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = {
                    contrasena = it
                    mensajeError = null
                },
                label = { Text("Contraseña") },
                singleLine = true,
                enabled = !estaCargando,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerexWhite,
                    unfocusedBorderColor = VerexWhite.copy(alpha = 0.6f),
                    focusedLabelColor = VerexWhite,
                    unfocusedLabelColor = VerexWhite.copy(alpha = 0.8f),
                    focusedTextColor = VerexTextDark,
                    unfocusedTextColor = VerexTextDark,
                    focusedContainerColor = VerexInputGray,
                    unfocusedContainerColor = VerexInputGray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (mensajeError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = mensajeError ?: "",
                    color = Color(0xFFFFD2D2),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Botón de Iniciar Sesión con Correo/Contraseña
            Button(
                onClick = {
                    if (correo.isBlank() || contrasena.isBlank()) {
                        mensajeError = "Por favor, complete todos los campos."
                        return@Button
                    }

                    estaCargando = true
                    mensajeError = null

                    coroutineScope.launch {
                        val resultado = authManager.iniciarSesion(correo.trim(), contrasena.trim())
                        estaCargando = false

                        if (resultado.isSuccess) {
                            val rol = resultado.getOrDefault("CLIENTE")
                            onLoginSuccess(rol)
                        } else {
                            val e = resultado.exceptionOrNull()
                            mensajeError = "${e?.javaClass?.simpleName}: ${e?.localizedMessage ?: "Error desconocido"}"
                        }
                    }
                },
                enabled = !estaCargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = VerexGreenDark)
            ) {
                if (estaCargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = VerexWhite,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = "Entrar",
                        color = VerexWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón de Iniciar Sesión con Google
            OutlinedButton(
                onClick = {
                    estaCargando = true
                    mensajeError = null
                    coroutineScope.launch {
                        try {
                            val credentialManager = CredentialManager.create(context)

                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId("603398907729-m93359t1qi2ef4lp6s4b66coaedccf07.apps.googleusercontent.com")
                                .build()

                            val getCredentialRequest = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            val result = credentialManager.getCredential(context, getCredentialRequest)
                            val credential = result.credential

                            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                                val resultado = authManager.iniciarSesionConGoogle(firebaseCredential)
                                estaCargando = false

                                if (resultado.isSuccess) {
                                    val rol = resultado.getOrDefault("CLIENTE")
                                    onLoginSuccess(rol)
                                } else {
                                    val e = resultado.exceptionOrNull()
                                    mensajeError = "${e?.javaClass?.simpleName}: ${e?.localizedMessage ?: "Error desconocido"}"
                                }
                            } else {
                                estaCargando = false
                                mensajeError = "Tipo de credencial no soportado."
                            }
                        } catch (e: Exception) {
                            estaCargando = false
                            mensajeError = "Error: ${e.javaClass.simpleName} - ${e.localizedMessage}"
                        }
                    }
                },
                enabled = !estaCargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = CircleShape,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = VerexWhite)
            ) {
                Text(
                    text = "Iniciar Sesión con Google",
                    color = VerexWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Enlace para ir al Registro
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = VerexWhite,
                    fontSize = 14.sp
                )
                Text(
                    text = "Regístrate aquí",
                    color = VerexWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(enabled = !estaCargando) {
                        onNavigateToRegister()
                    }
                )
            }
        }
    }
}