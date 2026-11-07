package com.verex.jewelry.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Remove
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
import com.verex.jewelry.model.Pedido
import com.verex.jewelry.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Mantenemos la lista reactiva de items del carrito
    val itemsCarrito = remember { mutableStateListOf<Joya>().apply { addAll(VerexRepository.carrito) } }
    val cantidades = remember { mutableStateMapOf<String, Int>().apply {
        itemsCarrito.forEachIndexed { index, joya -> put("${joya.id}_$index", 1) }
    } }

    // Estado para la foto de referencia adjunta
    var fotoReferenciaUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            fotoReferenciaUri = uri
            Toast.makeText(context, "¡Foto de referencia adjuntada!", Toast.LENGTH_SHORT).show()
        }
    }

    // Cálculo dinámico del subtotal
    val subtotal = itemsCarrito.indices.sumOf { index ->
        val joya = itemsCarrito[index]
        val cantidad = cantidades["${joya.id}_$index"] ?: 1
        joya.calcularPrecioFinal() * cantidad
    }
    val envio = if (itemsCarrito.isNotEmpty()) 4.00 else 0.0
    val total = subtotal + envio

    var procesandoPedido by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = VerexGreen,
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito de Compras", color = VerexWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = VerexWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VerexGreenDark)
            )
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsCarrito.forEachIndexed { index, joya ->
                    val key = "${joya.id}_$index"
                    val cantidad = cantidades[key] ?: 1

                    ItemCarritoMockupConCantidad(
                        joya = joya,
                        cantidad = cantidad,
                        onAumentar = { cantidades[key] = cantidad + 1 },
                        onDisminuir = {
                            if (cantidad > 1) {
                                cantidades[key] = cantidad - 1
                            } else {
                                itemsCarrito.removeAt(index)
                                VerexRepository.carrito.remove(joya)
                            }
                        },
                        onEliminar = {
                            itemsCarrito.removeAt(index)
                            VerexRepository.carrito.remove(joya)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Adjuntar foto de referencia (opcional):",
                    color = VerexWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = VerexInputGray),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (fotoReferenciaUri != null) {
                            AsyncImage(
                                model = fotoReferenciaUri,
                                contentDescription = "Foto adjunta",
                                modifier = Modifier.fillMaxSize().padding(4.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Subir imagen",
                                    tint = VerexTextDark,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Presiona aquí para subir una imagen",
                                    color = VerexTextDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "de la galería o cámara",
                                    color = VerexTextDark.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = VerexGreenDark),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal:", color = VerexWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", subtotal)}",
                                color = VerexWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Envío:", color = VerexWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", envio)}",
                                color = VerexWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        HorizontalDivider(color = VerexWhite.copy(alpha = 0.5f), thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total a Pagar:", color = VerexWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", total)}",
                                color = Color(0xFFFFD700),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (procesandoPedido) return@Button
                        procesandoPedido = true

                        coroutineScope.launch {
                            val resumen = itemsCarrito.indices.joinToString(", ") { i ->
                                val j = itemsCarrito[i]
                                val cant = cantidades["${j.id}_$i"] ?: 1
                                "${j.nombre} (x$cant, ${j.materialBase.nombre})"
                            }
                            val idGenerado = "PED_${System.currentTimeMillis()}"
                            val usuarioIdActual = VerexRepository.obtenerUsuarioActualId()

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
                                VerexRepository.carrito.clear()
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
                        .height(48.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    if (procesandoPedido) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = VerexTextDark)
                    } else {
                        Text(
                            text = "Confirmar Pedido",
                            color = VerexTextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ItemCarritoMockupConCantidad(
    joya: Joya,
    cantidad: Int,
    onAumentar: () -> Unit,
    onDisminuir: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VerexInputGray),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.5f)),
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

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = joya.nombre,
                        color = VerexTextDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${joya.materialBase.nombre} | Talla 7",
                        color = VerexTextDark.copy(alpha = 0.75f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$${String.format(Locale.US, "%.2f", joya.calcularPrecioFinal() * cantidad)}",
                        color = VerexGreenDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFD32F2F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(1.dp, VerexTextDark.copy(alpha = 0.2f)),
                    modifier = Modifier.height(32.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        IconButton(
                            onClick = onDisminuir,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Menos", tint = VerexTextDark, modifier = Modifier.size(14.dp))
                        }

                        Text(
                            text = "$cantidad",
                            color = VerexTextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(
                            onClick = onAumentar,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Más", tint = VerexTextDark, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}