package com.verex.jewelry.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verex.jewelry.FirestoreManager
import com.verex.jewelry.model.Pedido
import com.verex.jewelry.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onNavigateBack: () -> Unit
) {
    val firestoreManager = remember { FirestoreManager() }
    val coroutineScope = rememberCoroutineScope()

    var listaPedidos by remember { mutableStateOf<List<Pedido>>(emptyList()) }
    var estaCargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    // Cargar datos reales desde Firestore al abrir la pantalla
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val resultado = firestoreManager.obtenerPedidosUsuario()
            estaCargando = false
            if (resultado.isSuccess) {
                listaPedidos = resultado.getOrDefault(emptyList())
            } else {
                mensajeError = "Error al cargar el historial."
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Pedidos", color = VerexWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = VerexWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VerexGreenDark)
            )
        },
        containerColor = VerexGreen
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (estaCargando) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = VerexWhite
                )
            } else if (mensajeError != null) {
                Text(
                    text = mensajeError ?: "",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (listaPedidos.isEmpty()) {
                Text(
                    text = "No tienes solicitudes registradas aún.",
                    color = VerexWhite,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listaPedidos) { pedido ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = VerexWhite)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val idOrden = if (pedido.id.length > 6) pedido.id.takeLast(6) else pedido.id
                                    Text(
                                        text = "Orden #$idOrden",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = VerexTextDark
                                    )
                                    Text(
                                        text = "$${String.format(Locale.US, "%.2f", pedido.total)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFFB8860B)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = if (pedido.itemsResumen.isNotBlank()) pedido.itemsResumen else "Joyas personalizadas",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Estado: ${pedido.estado}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VerexGreenDark
                                    )
                                    val fechaFormateada = try {
                                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(pedido.fecha))
                                    } catch (e: Exception) {
                                        "Reciente"
                                    }
                                    Text(
                                        text = fechaFormateada,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}