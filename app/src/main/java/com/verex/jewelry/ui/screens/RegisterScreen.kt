package com.verex.jewelry.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
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
import com.verex.jewelry.AuthManager
import com.verex.jewelry.R
import com.verex.jewelry.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var estaCargando by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val authManager = remember { AuthManager() }

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
                text = "Bienvenidos",
                color = VerexWhite,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Campo Nombre Completo
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    mensajeError = null
                },
                placeholder = { Text("Nombre completo", color = Color.Gray) },
                singleLine = true,
                enabled = !estaCargando,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = VerexTextDark,
                    unfocusedTextColor = VerexTextDark,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Correo Electrónico
            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    mensajeError = null
                },
                placeholder = { Text("Correo Electrónico", color = Color.Gray) },
                singleLine = true,
                enabled = !estaCargando,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = VerexTextDark,
                    unfocusedTextColor = VerexTextDark,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = {
                    contrasena = it
                    mensajeError = null
                },
                placeholder = { Text("Contraseña (mínimo 6 caracteres)", color = Color.Gray) },
                singleLine = true,
                enabled = !estaCargando,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = VerexTextDark,
                    unfocusedTextColor = VerexTextDark,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )

            if (mensajeError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = mensajeError ?: "",
                    color = Color(0xFFFFD2D2),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Botón Ingresar
            OutlinedButton(
                onClick = {
                    if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank()) {
                        mensajeError = "Por favor, complete todos los campos."
                        return@OutlinedButton
                    }
                    if (contrasena.length < 6) {
                        mensajeError = "La contraseña debe tener al menos 6 caracteres."
                        return@OutlinedButton
                    }

                    estaCargando = true
                    mensajeError = null

                    coroutineScope.launch {
                        // Llamada corregida con el nombre exacto y orden de parámetros de tu AuthManager
                        val resultado = authManager.registrarConCorreo(nombre.trim(), correo.trim(), contrasena.trim())
                        estaCargando = false

                        if (resultado.isSuccess) {
                            val rol = resultado.getOrDefault("CLIENTE")
                            onRegisterSuccess(rol)
                        } else {
                            val e = resultado.exceptionOrNull()
                            mensajeError = e?.localizedMessage ?: "Error al registrarse"
                        }
                    }
                },
                enabled = !estaCargando,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(44.dp),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = VerexWhite)
            ) {
                if (estaCargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = VerexWhite,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Ingresar",
                        color = VerexWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enlace para volver al Login
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes una cuenta? ",
                    color = VerexWhite,
                    fontSize = 13.sp
                )
                Text(
                    text = "Inicia sesión",
                    color = VerexWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable(enabled = !estaCargando) {
                        onNavigateBack()
                    }
                )
            }
        }
    }
}