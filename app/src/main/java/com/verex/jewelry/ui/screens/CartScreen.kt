package com.verex.jewelry.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.verex.jewelry.data.VerexRepository
import com.verex.jewelry.model.Joya
import com.verex.jewelry.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import com.verex.jewelry.model.Pedido

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    val itemsCarrito = remember { mutableStateListOf<Joya>().apply { addAll(VerexRepository.carrito) } }
    val total = itemsCarrito.sumOf { it.calcularPrecioFinal() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var procesandoPedido by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = VerexGreen,
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras", color = VerexWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = VerexWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VerexGreenDark)
            )
        },
        bottomBar = {
            if (itemsCarrito.isNotEmpty()) {
                Surface(
                    color = VerexGreenDark,
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total a Pagar:", color = VerexWhite, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", total)}",
                                color = Color(0xFFFFD700),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (procesandoPedido) return@Button
                                procesandoPedido = true

                                coroutineScope.launch {
                                    val resumen = itemsCarrito.joinToString(", ") { "${it.nombre} (${it.materialBase.nombre})" }
                                    val idGenerado = "PED_${System.currentTimeMillis()}"
                                    val usuarioIdActual = VerexRepository.obtenerUsuarioActualId()

                                    // Usamos 'userId' tal como está definido en el modelo Pedido.kt
                                    val nuevoPedido = Pedido(
                                        id = idGenerado,
                                        userId = usuarioIdActual,
                                        clienteNombre = if (usuarioIdActual.isNotBlank()) "Cliente App" else "Cliente Mostrador",
                                        itemsResumen = resumen,
                                        total = total,
                                        estado = "RECIBIDO",
                                        fecha = System.currentTimeMillis()
                                    )

                                    val resultado = VerexRepository.guardarPedidoEnFirestore(nuevoPedido)
                                    procesandoPedido = false

                                    if (resultado.isSuccess) {
                                        itemsCarrito.clear()
                                        Toast.makeText(context, "¡Pedido registrado con éxito!", Toast.LENGTH_SHORT).show()
                                        onNavigateToOrders()
                                    } else {
                                        Toast.makeText(context, "Error al procesar el pedido", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            enabled = !procesandoPedido,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = VerexInputGray)
                        ) {
                            if (procesandoPedido) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = VerexTextDark)
                            } else {
                                Text("Confirmar y Enviar a Taller", color = VerexTextDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (itemsCarrito.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Carrito Vacío",
                        tint = VerexWhite.copy(alpha = 0.5f),
                        modifier = Modifier.size(70.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Tu carrito está vacío", color = VerexWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(itemsCarrito) { joya ->
                    ItemCarrito(
                        joya = joya,
                        onEliminar = {
                            itemsCarrito.remove(joya)
                            VerexRepository.carrito.remove(joya)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemCarrito(
    joya: Joya,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VerexGreenDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                val fallbackRes = if (joya.imagenRes != 0) joya.imagenRes else com.verex.jewelry.R.drawable.logo
                if (joya.imagenUri.isNotBlank()) {
                    val model: Any = remember(joya.imagenUri) {
                        if (joya.imagenUri.startsWith("content://") || joya.imagenUri.startsWith("http")) {
                            joya.imagenUri
                        } else {
                            val f = File(joya.imagenUri)
                            if (f.exists()) f else joya.imagenUri
                        }
                    }
                    AsyncImage(
                        model = model,
                        contentDescription = joya.nombre,
                        modifier = Modifier.fillMaxSize().padding(3.dp),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = fallbackRes),
                        placeholder = painterResource(id = fallbackRes)
                    )
                } else {
                    Image(
                        painter = painterResource(id = fallbackRes),
                        contentDescription = joya.nombre,
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = joya.nombre, color = VerexWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${joya.materialBase.nombre} • ${joya.pesoGramos}g",
                    color = VerexWhite.copy(alpha = 0.75f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${String.format(Locale.US, "%.2f", joya.calcularPrecioFinal())}",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFFF6B6B))
            }
        }
    }
}