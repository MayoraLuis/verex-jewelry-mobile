package com.verex.jewelry.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.verex.jewelry.R
import com.verex.jewelry.data.VerexRepository
import com.verex.jewelry.model.RolUsuario
import com.verex.jewelry.model.Usuario
import com.verex.jewelry.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = VerexGreen,
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta", color = VerexWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = VerexWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VerexGreenDark)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Verex",
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(95.dp),
                contentScale = ContentScale.Fit
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

            Spacer(modifier = Modifier.height(10.dp))

            // Campo Confirmar Contraseña
            OutlinedTextField(
                value = confirmarContrasena,
                onValueChange = {
                    confirmarContrasena = it
                    mensajeError = null
                },
                label = { Text("Confirmar Contraseña") },
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

            // Mensajes de Validación
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

            // Botón de Registrarse
            Button(
                onClick = {
                    when {
                        nombre.isBlank() || correo.isBlank() || contrasena.isBlank() || confirmarContrasena.isBlank() -> {
                            mensajeError = "Por favor, complete todos los campos."
                        }
                        !correo.contains("@") || !correo.contains(".") -> {
                            mensajeError = "Ingrese un correo electrónico válido."
                        }
                        contrasena.length < 6 -> {
                            mensajeError = "La contraseña debe tener al menos 6 caracteres."
                        }
                        contrasena != confirmarContrasena -> {
                            mensajeError = "Las contraseñas no coinciden."
                        }
                        else -> {
                            val nuevoUsuario = Usuario(
                                id = "U" + (100..999).random(),
                                nombre = nombre.trim(),
                                correo = correo.trim(),
                                contrasena = contrasena.trim(),
                                rol = RolUsuario.CLIENTE
                            )

                            // Registra en el repositorio para que persista en la lista de usuarios
                            val registradoExitosamente = VerexRepository.registrarUsuario(nuevoUsuario)

                            if (registradoExitosamente) {
                                onRegisterSuccess()
                            } else {
                                mensajeError = "Este correo electrónico ya está registrado."
                            }
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
                    text = "Registrarse",
                    color = VerexWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enlace para volver al Login
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "¿Ya tienes cuenta? ", color = VerexWhite, fontSize = 14.sp)
                Text(
                    text = "Inicia sesión",
                    color = VerexWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}