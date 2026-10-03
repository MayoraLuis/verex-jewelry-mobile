package com.verex.jewelry.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verex.jewelry.R
import com.verex.jewelry.data.VerexRepository
import com.verex.jewelry.ui.theme.*

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }

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
            // Logotipo institucional
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

            // Mensaje de Error si las credenciales fallan
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

            // Botón de Iniciar Sesión
            Button(
                onClick = {
                    if (correo.isBlank() || contrasena.isBlank()) {
                        mensajeError = "Por favor, complete todos los campos."
                    } else {
                        // Verificación directa sobre Usuario?
                        val usuario = VerexRepository.iniciarSesion(correo, contrasena)
                        if (usuario != null) {
                            onLoginSuccess()
                        } else {
                            mensajeError = "Credenciales incorrectas. Verifique correo y clave."
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = VerexGreenDark)
            ) {
                Text(
                    text = "Entrar",
                    color = VerexWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

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
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}