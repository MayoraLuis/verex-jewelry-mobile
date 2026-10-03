package com.verex.jewelry.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verex.jewelry.R
import com.verex.jewelry.data.VerexRepository
import com.verex.jewelry.model.Anillo
import com.verex.jewelry.model.Joya
import com.verex.jewelry.model.Material
import com.verex.jewelry.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoterScreen(
    joya: Joya? = null,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    // Si viene nula (por ejemplo desde la barra inferior), toma la primera del catálogo
    val joyaActual = joya ?: VerexRepository.obtenerCatalogo().firstOrNull()

    // Estados configurables
    var materialSeleccionado by remember { mutableStateOf(joyaActual?.materialBase ?: Material.PLATA_925) }
    var tallaSeleccionada by remember { mutableIntStateOf(if (joyaActual is Anillo) joyaActual.talla else 7) }
    var grabadoTexto by remember { mutableStateOf("") }
    var incluyeGrabado by remember { mutableStateOf(false) }

    // Cálculo reactivo
    val precioFinal = remember(joyaActual, materialSeleccionado, tallaSeleccionada, incluyeGrabado) {
        if (joyaActual == null) 0.0
        else {
            val costoBase = joyaActual.precioBaseElaboracion
            val costoMetal = joyaActual.pesoGramos * materialSeleccionado.multiplicadorGramo
            val extraTalla = if (joyaActual is Anillo && tallaSeleccionada > 7) (tallaSeleccionada - 7) * 4.5 else 0.0
            val extraGrabado = if (incluyeGrabado) 10.0 else 0.0
            costoBase + costoMetal + extraTalla + extraGrabado
        }
    }

    Scaffold(
        containerColor = VerexGreen,
        topBar = {
            TopAppBar(
                title = { Text("Cotizador de Joya", color = VerexWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = VerexWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VerexGreenDark)
            )
        }
    ) { paddingValues ->
        if (joyaActual == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No se encontró la joya seleccionada.", color = VerexWhite)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Imagen de la joya seleccionada
                Image(
                    painter = painterResource(id = joyaActual.imagenRes),
                    contentDescription = joyaActual.nombre,
                    modifier = Modifier
                        .size(170.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(2.dp, VerexWhite, RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Nombre y especificaciones
                Text(
                    text = joyaActual.nombre,
                    color = VerexWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Peso estimado: ${joyaActual.pesoGramos}g",
                    color = VerexWhite.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de Material
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VerexGreenDark)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Selecciona el Material:", color = VerexWhite, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Material.values().forEach { mat ->
                                val selected = materialSeleccionado == mat
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (selected) VerexInputGray else Color.Transparent)
                                        .border(1.dp, VerexWhite, CircleShape)
                                        .clickable { materialSeleccionado = mat }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = mat.nombre,
                                        color = if (selected) VerexTextDark else VerexWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Selector de Talla (si es anillo)
                if (joyaActual is Anillo) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = VerexGreenDark)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Talla del Anillo:", color = VerexWhite, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                (5..10).forEach { t ->
                                    val isSelected = tallaSeleccionada == t
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) VerexInputGray else Color.Transparent)
                                            .border(1.dp, VerexWhite, CircleShape)
                                            .clickable { tallaSeleccionada = t },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$t",
                                            color = if (isSelected) VerexTextDark else VerexWhite,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Opción de Grabado
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VerexGreenDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Grabado personalizado", color = VerexWhite, fontWeight = FontWeight.SemiBold)
                            Text("Costo adicional: +$10.00", color = VerexWhite.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                        Switch(
                            checked = incluyeGrabado,
                            onCheckedChange = { incluyeGrabado = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = VerexGreenDark,
                                checkedTrackColor = VerexWhite
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Resumen del Precio Cotizado
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VerexInputGray)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("PRECIO TOTAL COTIZADO", color = VerexTextDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "$${String.format("%.2f", precioFinal)}",
                            color = VerexGreenDark,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón Añadir al Carrito
                Button(
                    onClick = {
                        // 1. Guardamos la joya seleccionada en el carrito del repositorio
                        joyaActual?.let { joya ->
                            VerexRepository.agregarAlCarrito(joya, cantidad = 1)
                        }
                        // 2. Navegamos a la pantalla del carrito
                        onNavigateToCart()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = VerexGreenDark)
                ) {
                    Text("Añadir al Carrito", color = VerexWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}