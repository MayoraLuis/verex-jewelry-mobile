package com.verex.jewelry.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.verex.jewelry.data.VerexRepository
import com.verex.jewelry.model.JoyaFirestore
import com.verex.jewelry.model.Material
import com.verex.jewelry.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale
import com.verex.jewelry.model.Pedido

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onCerrarSesion: () -> Unit
) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    val pedidosFlow = remember { VerexRepository.obtenerPedidosEnTiempoReal() }
    val listaPedidos by pedidosFlow.collectAsState(initial = emptyList())

    val pedidosActivos = remember(listaPedidos) {
        listaPedidos.filter { it.estado.trim().uppercase() != "ENVIADO" }
    }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        containerColor = VerexGreen,
        topBar = {
            TopAppBar(
                title = { Text("Panel Administrativo", color = VerexWhite, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        VerexRepository.cerrarSesion()
                        onCerrarSesion()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir", tint = VerexWhite)
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
        ) {
            TabRow(
                selectedTabIndex = tabSeleccionada,
                containerColor = VerexGreenDark,
                contentColor = VerexWhite
            ) {
                Tab(
                    selected = tabSeleccionada == 0,
                    onClick = { tabSeleccionada = 0 },
                    text = { Text("Dashboard", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = tabSeleccionada == 1,
                    onClick = { tabSeleccionada = 1 },
                    text = { Text("Pedidos (${pedidosActivos.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = tabSeleccionada == 2,
                    onClick = { tabSeleccionada = 2 },
                    text = { Text("Nuevo Prod.", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }

            when (tabSeleccionada) {
                0 -> DashboardAdmin(listaPedidos = listaPedidos)
                1 -> {
                    if (pedidosActivos.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay pedidos pendientes por procesar.", color = VerexWhite)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(pedidosActivos, key = { index, pedido -> if (pedido.id.isNotBlank()) "${pedido.id}_$index" else "$index" }) { _, pedido ->
                                ItemPedidoAdmin(
                                    pedido = pedido,
                                    onActualizarEstado = { nuevoEstado ->
                                        coroutineScope.launch {
                                            val resultado = VerexRepository.actualizarEstadoPedido(pedido.id, nuevoEstado)
                                            if (resultado.isSuccess) {
                                                Toast.makeText(context, "Estado: $nuevoEstado", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    FormularioNuevoProducto(
                        onProductoAgregado = {
                            Toast.makeText(context, "¡Producto agregado al catálogo!", Toast.LENGTH_SHORT).show()
                            tabSeleccionada = 0
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardAdmin(listaPedidos: List<Pedido>) {
    val totalVentas = listaPedidos.sumOf { it.total }
    val pedidosEnProceso = listaPedidos.count { it.estado == "EN_FABRICACION" || it.estado == "RECIBIDO" }
    val pedidosCompletados = listaPedidos.count { it.estado == "ENVIADO" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Resumen de Operaciones", color = VerexWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaMetrica("Ventas Totales", "$${String.format(Locale.US, "%.2f", totalVentas)}", Color(0xFFFFD700), Modifier.weight(1f))
            TarjetaMetrica("Total Pedidos", "${listaPedidos.size}", VerexWhite, Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaMetrica("En Taller", "$pedidosEnProceso", Color(0xFF81D4FA), Modifier.weight(1f))
            TarjetaMetrica("Enviados / Listos", "$pedidosCompletados", Color(0xFFA5D6A7), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = VerexGreenDark)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Desglose Histórico de Pedidos", color = VerexWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))
                FilaDetalleEstado("Recibidos", listaPedidos.count { it.estado == "RECIBIDO" })
                FilaDetalleEstado("En Fabricación", listaPedidos.count { it.estado == "EN_FABRICACION" })
                FilaDetalleEstado("Empaquetados", listaPedidos.count { it.estado == "EMPAQUETADO" })
                FilaDetalleEstado("Enviados (Finalizados)", pedidosCompletados)
            }
        }
    }
}

@Composable
fun TarjetaMetrica(titulo: String, valor: String, colorValor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = VerexGreenDark)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
            Text(text = titulo, color = VerexWhite.copy(alpha = 0.8f), fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = valor, color = colorValor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FilaDetalleEstado(nombre: String, cantidad: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = nombre, color = VerexWhite.copy(alpha = 0.85f), fontSize = 14.sp)
        Text(text = "$cantidad", color = VerexWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun ItemPedidoAdmin(
    pedido: Pedido,
    onActualizarEstado: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val estadosPosibles = listOf("RECIBIDO", "EN_FABRICACION", "EMPAQUETADO", "ENVIADO")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VerexGreenDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cliente: ${pedido.clienteNombre}",
                    color = VerexWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "$${String.format(Locale.US, "%.2f", pedido.total)}",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Detalle: ${pedido.itemsResumen}", color = VerexWhite.copy(alpha = 0.85f), fontSize = 13.sp)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Estatus fabricación:", color = VerexWhite, fontSize = 13.sp)

                Box {
                    Button(
                        onClick = { expanded = true },
                        colors = ButtonDefaults.buttonColors(containerColor = VerexInputGray)
                    ) {
                        Text(pedido.estado, color = VerexTextDark, fontWeight = FontWeight.Bold)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(VerexInputGray)
                    ) {
                        estadosPosibles.forEach { est ->
                            DropdownMenuItem(
                                text = { Text(est, color = VerexTextDark, fontWeight = FontWeight.SemiBold) },
                                onClick = {
                                    expanded = false
                                    onActualizarEstado(est)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormularioNuevoProducto(
    onProductoAgregado: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var precioBase by remember { mutableStateOf("") }
    var pesoGramos by remember { mutableStateOf("") }
    var tipoJoya by remember { mutableStateOf("Anillo") }
    var materialSeleccionado by remember { mutableStateOf(Material.PLATA_925) }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registrar Nueva Joya", color = VerexWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(VerexInputGray)
                .border(1.5.dp, VerexWhite.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .clickable { photoPickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imagenUri != null) {
                AsyncImage(
                    model = imagenUri,
                    contentDescription = "Foto elegida",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Toca para cambiar", color = Color.White, fontSize = 11.sp)
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Seleccionar Imagen",
                        tint = VerexTextDark.copy(alpha = 0.6f),
                        modifier = Modifier.size(38.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Toca para subir foto de la joya", color = VerexTextDark, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }

        // Campo Nombre del Producto con borde redondeado y fondo blanco
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            placeholder = { Text("Nombre del Producto", color = Color.Gray) },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = VerexTextDark,
                unfocusedTextColor = VerexTextDark
            ),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )

        // Campo Precio Base con borde redondeado y fondo blanco
        OutlinedTextField(
            value = precioBase,
            onValueChange = { precioBase = it },
            placeholder = { Text("Precio Base de Elaboración ($)", color = Color.Gray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = VerexTextDark,
                unfocusedTextColor = VerexTextDark
            ),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )

        // Campo Peso Estimado con borde redondeado y fondo blanco
        OutlinedTextField(
            value = pesoGramos,
            onValueChange = { pesoGramos = it },
            placeholder = { Text("Peso Estimado (Gramos)", color = Color.Gray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = VerexTextDark,
                unfocusedTextColor = VerexTextDark
            ),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )

        Text("Tipo de Joya:", color = VerexWhite, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Anillo", "Cadena", "Pulsera", "Arito", "Reloj").forEach { tipo ->
                FilterChip(
                    selected = tipoJoya == tipo,
                    onClick = { tipoJoya = tipo },
                    label = { Text(tipo) }
                )
            }
        }

        Text("Material Base:", color = VerexWhite, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Material.entries.forEach { mat ->
                FilterChip(
                    selected = materialSeleccionado == mat,
                    onClick = { materialSeleccionado = mat },
                    label = { Text(mat.nombre) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Botón Guardar Joya en Catálogo con fondo blanco y letras oscuras
        Button(
            onClick = {
                val precio = precioBase.toDoubleOrNull() ?: 0.0
                val peso = pesoGramos.toDoubleOrNull() ?: 0.0
                val idGenerado = "PROD_${System.currentTimeMillis()}"

                if (nombre.isNotBlank() && precio > 0) {
                    coroutineScope.launch {
                        val rutaPersistente = if (imagenUri != null) {
                            VerexRepository.guardarImagenInterna(context, imagenUri!!, idGenerado)
                        } else {
                            ""
                        }

                        val joyaDto = JoyaFirestore(
                            id = idGenerado,
                            nombre = nombre,
                            tipo = tipoJoya,
                            material = materialSeleccionado.name,
                            pesoGramos = peso,
                            precioBase = precio,
                            extraProp = if (tipoJoya == "Anillo") 7 else 18,
                            imagenUri = rutaPersistente
                        )

                        val resultado = VerexRepository.guardarJoyaEnFirestore(joyaDto)
                        if (resultado.isSuccess) {
                            onProductoAgregado()
                        } else {
                            Toast.makeText(context, "Error al guardar en base de datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Por favor complete los campos obligatorios", Toast.LENGTH_SHORT).show()
                }
            },
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("Guardar Joya en Catálogo", color = VerexTextDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}