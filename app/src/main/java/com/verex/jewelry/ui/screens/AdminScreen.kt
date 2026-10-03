package com.verex.jewelry.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verex.jewelry.R
import com.verex.jewelry.data.VerexRepository
import com.verex.jewelry.model.*
import com.verex.jewelry.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    var listaPedidos by remember { mutableStateOf(VerexRepository.obtenerPedidosAdmin()) }
    var mostrarDialogoAgregar by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = VerexGreen,
        topBar = {
            TopAppBar(
                title = { Text("Panel de Control (Admin)", color = VerexWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = VerexWhite)
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión", tint = VerexWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VerexGreenDark)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de Métricas / Ventas
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, VerexWhite),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ventas Mensuales ($)",
                            color = VerexWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        ) {
                            val valores = listOf(0.45f, 0.65f, 0.30f, 0.60f, 0.90f)
                            val barraAncho = size.width / (valores.size * 2)
                            val lineaBaseY = size.height - 10f

                            // Línea de base
                            drawLine(
                                color = Color.White.copy(alpha = 0.5f),
                                start = Offset(0f, lineaBaseY),
                                end = Offset(size.width, lineaBaseY),
                                strokeWidth = 2f
                            )

                            valores.forEachIndexed { index, porcentaje ->
                                val x = index * (barraAncho * 2) + barraAncho / 2
                                val alturaBarra = (lineaBaseY - 10f) * porcentaje
                                val topY = lineaBaseY - alturaBarra

                                drawRoundRect(
                                    color = if (index == valores.lastIndex) Color.White else Color.White.copy(alpha = 0.7f),
                                    topLeft = Offset(x, topY),
                                    size = Size(barraAncho, alturaBarra),
                                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                )
                            }
                        }
                    }
                }
            }

            // Botón Agregar Joya al Inventario
            item {
                OutlinedButton(
                    onClick = { mostrarDialogoAgregar = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, VerexWhite),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VerexWhite)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = VerexWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Agregar Nuevo Producto al Inventario",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Título de la sección
            item {
                Text(
                    text = "Gestión de Pedidos Activos",
                    color = VerexWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Lista reactiva de pedidos
            if (listaPedidos.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = VerexGreenDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay pedidos activos actualmente.", color = VerexWhite)
                        }
                    }
                }
            } else {
                items(listaPedidos) { pedido: Pedido ->
                    var menuExpandido by remember { mutableStateOf(false) }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = VerexGreenDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = pedido.idPedido,
                                    color = VerexWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "$${String.format("%.2f", pedido.total)}",
                                    color = VerexWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Cliente: ${pedido.cliente.nombre}",
                                color = VerexWhite.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )

                            Text(
                                text = "Producto: ${pedido.joya.nombre} (x${pedido.cantidad})",
                                color = VerexWhite.copy(alpha = 0.85f),
                                fontSize = 13.sp
                            )

                            Text(
                                text = "Material: ${pedido.joya.materialBase.nombre}",
                                color = VerexWhite.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Selector del estado del pedido
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Estado actual:",
                                    color = VerexWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Box {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = VerexInputGray,
                                        modifier = Modifier.clickable { menuExpandido = true }
                                    ) {
                                        Text(
                                            text = pedido.estado.name,
                                            color = VerexTextDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = menuExpandido,
                                        onDismissRequest = { menuExpandido = false }
                                    ) {
                                        EstadoPedido.values().forEach { estado ->
                                            DropdownMenuItem(
                                                text = { Text(estado.name) },
                                                onClick = {
                                                    VerexRepository.actualizarEstadoPedido(pedido.idPedido, estado)
                                                    listaPedidos = VerexRepository.obtenerPedidosAdmin()
                                                    menuExpandido = false
                                                }
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
    }

    // Diálogo para Registrar Nueva Joya
    if (mostrarDialogoAgregar) {
        var nombreProducto by remember { mutableStateOf("") }
        var pesoTexto by remember { mutableStateOf("") }
        var precioTexto by remember { mutableStateOf("") }
        var materialElegido by remember { mutableStateOf(Material.PLATA_925) }

        AlertDialog(
            onDismissRequest = { mostrarDialogoAgregar = false },
            containerColor = VerexGreenDark,
            title = {
                Text("Nuevo Producto", color = VerexWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = nombreProducto,
                        onValueChange = { nombreProducto = it },
                        label = { Text("Nombre de la Joya") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VerexTextDark,
                            unfocusedTextColor = VerexTextDark,
                            focusedContainerColor = VerexInputGray,
                            unfocusedContainerColor = VerexInputGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pesoTexto,
                        onValueChange = { pesoTexto = it },
                        label = { Text("Peso en Gramos (ej: 4.5)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VerexTextDark,
                            unfocusedTextColor = VerexTextDark,
                            focusedContainerColor = VerexInputGray,
                            unfocusedContainerColor = VerexInputGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = precioTexto,
                        onValueChange = { precioTexto = it },
                        label = { Text("Precio Base Elaboración (ej: 30.0)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VerexTextDark,
                            unfocusedTextColor = VerexTextDark,
                            focusedContainerColor = VerexInputGray,
                            unfocusedContainerColor = VerexInputGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val peso = pesoTexto.toDoubleOrNull() ?: 3.0
                        val precio = precioTexto.toDoubleOrNull() ?: 25.0
                        val nuevaJoya = Anillo(
                            id = "JOY-" + (100..999).random(),
                            nombre = if (nombreProducto.isNotBlank()) nombreProducto else "Nueva Joya Verex",
                            materialBase = materialElegido,
                            pesoGramos = peso,
                            precioBaseElaboracion = precio,
                            talla = 7,
                            imagenRes = R.drawable.anillo_plata_1
                        )
                        VerexRepository.agregarJoya(nuevaJoya)
                        mostrarDialogoAgregar = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VerexWhite)
                ) {
                    Text("Guardar", color = VerexGreenDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoAgregar = false }) {
                    Text("Cancelar", color = VerexWhite)
                }
            }
        )
    }
}