package com.verex.jewelry.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verex.jewelry.R
import com.verex.jewelry.data.VerexRepository
import com.verex.jewelry.model.Joya
import com.verex.jewelry.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onOrderConfirmed: () -> Unit
) {
    val context = LocalContext.current
    var itemsCarrito by remember { mutableStateOf(VerexRepository.obtenerCarrito()) }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    val selectorImagenLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
    }

    val bitmapSeleccionado = remember(imagenUri) {
        imagenUri?.let { uri ->
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
                    android.graphics.ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    val costoEnvio = 4.00

    val subtotal = itemsCarrito.fold(0.0) { acc, par ->
        val joya: Joya = par.first
        val cantidad: Int = par.second
        acc + (joya.calcularPrecioFinal() * cantidad)
    }

    val totalPagar = if (itemsCarrito.isNotEmpty()) subtotal + costoEnvio else 0.0

    Scaffold(
        containerColor = VerexGreen,
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito de Compras", color = VerexWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = VerexWhite)
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
                Text("El carrito está vacío", color = VerexWhite, fontSize = 18.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(itemsCarrito) { par: Pair<Joya, Int> ->
                    val joya = par.first
                    val cantidad = par.second

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = VerexGreenDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = joya.imagenRes),
                                contentDescription = joya.nombre,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = joya.nombre,
                                    color = VerexWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "${joya.materialBase.nombre} | ${joya.pesoGramos}g",
                                    color = VerexWhite.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "$${String.format("%.2f", joya.calcularPrecioFinal() * cantidad)}",
                                    color = VerexWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(VerexInputGray, CircleShape)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = " - ",
                                    color = VerexTextDark,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier
                                        .clickable {
                                            VerexRepository.actualizarCantidad(joya.id, cantidad - 1)
                                            itemsCarrito = VerexRepository.obtenerCarrito()
                                        }
                                        .padding(horizontal = 4.dp)
                                )
                                Text(
                                    text = "$cantidad",
                                    color = VerexTextDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                                Text(
                                    text = " + ",
                                    color = VerexTextDark,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier
                                        .clickable {
                                            VerexRepository.actualizarCantidad(joya.id, cantidad + 1)
                                            itemsCarrito = VerexRepository.obtenerCarrito()
                                        }
                                        .padding(horizontal = 4.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Adjuntar foto de referencia (opcional):",
                        color = VerexWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = VerexInputGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectorImagenLauncher.launch("image/*")
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (bitmapSeleccionado != null) {
                                Image(
                                    bitmap = bitmapSeleccionado.asImageBitmap(),
                                    contentDescription = "Foto adjuntada desde archivo",
                                    modifier = Modifier
                                        .size(130.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        painter = painterResource(id = android.R.drawable.ic_menu_camera),
                                        contentDescription = "Cámara",
                                        modifier = Modifier.size(36.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Presiona aquí para subir una imagen",
                                        color = VerexTextDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "de la galería o archivos",
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, VerexWhite),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal:", color = VerexWhite, fontSize = 15.sp)
                                Text("$${String.format("%.2f", subtotal)}", color = VerexWhite, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Envío:", color = VerexWhite, fontSize = 15.sp)
                                Text("$${String.format("%.2f", costoEnvio)}", color = VerexWhite, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(
                                color = VerexWhite.copy(alpha = 0.5f),
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total a Pagar:", color = VerexWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                Text("$${String.format("%.2f", totalPagar)}", color = VerexWhite, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                // Botón Confirmar Pedido conectado limpiamente a VerexRepository
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            VerexRepository.registrarPedidosDesdeCarrito(costoEnvio = costoEnvio)
                            VerexRepository.limpiarCarrito()
                            onOrderConfirmed()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = VerexGreenDark)
                    ) {
                        Text("Confirmar Pedido", color = VerexWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}