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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.verex.jewelry.R
import com.verex.jewelry.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var estaCargando by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }

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
                    .fillMaxWidth(0.70f)
                    .height(95.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Crear Cuenta",
                color = VerexWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Campo Nombre Completo
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    mensajeError = null
                },
                label = { Text("Nombre Completo") },
                singleLine = true,
                enabled = !estaCargando,
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

            Spacer(modifier = Modifier.height(10.dp))

            // Campo Correo Electrónico
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

            Spacer(modifier = Modifier.height(10.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = {
                    contrasena = it
                    mensajeError = null
                },
                label = { Text("Contraseña (mínimo 6 caracteres)") },
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

            Spacer(modifier = Modifier.height(20.dp))

            // Botón de Registro
            Button(
                onClick = {
                    if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank()) {
                        mensajeError = "Por favor, complete todos los campos."
                        return@Button
                    }
                    if (contrasena.length < 6) {
                        mensajeError = "La contraseña debe tener al menos 6 caracteres."
                        return@Button
                    }

                    estaCargando = true
                    mensajeError = null

                    coroutineScope.launch {
                        try {
                            // 1. Crear usuario en Firebase Authentication
                            val authResult = auth.createUserWithEmailAndPassword(
                                correo.trim(),
                                contrasena.trim()
                            ).await()

                            val uid = authResult.user?.uid ?: throw Exception("Error al crear usuario")

                            val correoClean = correo.trim().lowercase()
                            val esAdmin = correoClean == "admin@verex.com" || correoClean.contains("admin")
                            val rol = if (esAdmin) "admin" else "CLIENTE"

                            // 2. Persistir perfil del usuario en Cloud Firestore
                            val datosUsuario = hashMapOf(
                                "id" to uid,
                                "nombre" to nombre.trim(),
                                "correo" to correo.trim(),
                                "rol" to rol
                            )
                            firestore.collection("usuarios").document(uid).set(datosUsuario).await()

                            estaCargando = false
                            onRegisterSuccess()
                        } catch (e: Exception) {
                            estaCargando = false
                            mensajeError = when {
                                e.message?.contains("email address is already in use", ignoreCase = true) == true ->
                                    "Este correo ya se encuentra registrado."
                                e.message?.contains("badly formatted", ignoreCase = true) == true ->
                                    "El formato del correo es inválido."
                                else ->
                                    e.localizedMessage ?: "Ocurrió un error al registrarse."
                            }
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
                        text = "Registrarme",
                        color = VerexWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    color = VerexWhite,
                    fontSize = 14.sp
                )
                Text(
                    text = "Inicia sesión",
                    color = VerexWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(enabled = !estaCargando) {
                        onNavigateToLogin()
                    }
                )
            }
        }
    }
}