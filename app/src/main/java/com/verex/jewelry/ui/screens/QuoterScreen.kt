package com.verex.jewelry.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.verex.jewelry.data.VerexRepository
import com.verex.jewelry.model.*
import com.verex.jewelry.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoterScreen(
    joya: Joya,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val context = LocalContext.current

    var materialSeleccionado by remember { mutableStateOf(joya.materialBase) }
    var pesoGramos by remember { mutableDoubleStateOf(joya.pesoGramos) }
    var tallaAnillo by remember { mutableIntStateOf((joya as? Anillo)?.talla ?: 7) }
    var longitudCm by remember {
        mutableIntStateOf(
            when (joya) {
                is Cadena -> joya.longitudCm
                is Pulsera -> joya.longitudCm
                else -> 45
            }
        )
    }

    // Cálculo dinámico del precio
    val precioCalculado = remember(materialSeleccionado, pesoGramos, tallaAnillo, longitudCm) {
        val costoMaterial = pesoGramos * materialSeleccionado.multiplicadorGramo
        val costoExtra = when (joya) {
            is Anillo -> if (tallaAnillo > 7) (tallaAnillo - 7) * 5.0 else 0.0
            is Cadena -> if (longitudCm > 45) (longitudCm - 45) * 1.5 else 0.0
            is Pulsera -> if (longitudCm > 18) (longitudCm - 18) * 2.0 else 0.0
            is Reloj -> 50.0
            else -> 0.0
        }
        joya.precioBaseElaboracion + costoMaterial + costoExtra
    }

    Scaffold(
        containerColor = VerexGreen,
        topBar = {
            TopAppBar(
                title = { Text("Cotizador Personalizado", color = VerexWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = VerexWhite)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = VerexWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VerexGreenDark)
            )
        },
        bottomBar = {
            // Usamos un Surface fijo en la parte inferior que respeta las barras de navegación del sistema
            Surface(
                color = VerexGreenDark,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding() // <- Esto evita que choque con los botones del celular
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Total en una sola línea
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total: ",
                            color = VerexWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${String.format(Locale.getDefault(), "%.2f", precioCalculado)}",
                            color = Color(0xFFFFD700),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Botón Añadir al Carrito
                    Button(
                        onClick = {
                            val joyaPersonalizada = when (joya) {
                                is Anillo -> Anillo(
                                    id = "CUSTOM_${System.currentTimeMillis()}",
                                    nombre = "${joya.nombre} (Personalizado)",
                                    materialBase = materialSeleccionado,
                                    pesoGramos = pesoGramos,
                                    precioBaseElaboracion = joya.precioBaseElaboracion,
                                    talla = tallaAnillo,
                                    imagenRes = joya.imagenRes
                                )
                                is Cadena -> Cadena(
                                    id = "CUSTOM_${System.currentTimeMillis()}",
                                    nombre = "${joya.nombre} (Personalizado)",
                                    materialBase = materialSeleccionado,
                                    pesoGramos = pesoGramos,
                                    precioBaseElaboracion = joya.precioBaseElaboracion,
                                    longitudCm = longitudCm,
                                    imagenRes = joya.imagenRes
                                )
                                is Pulsera -> Pulsera(
                                    id = "CUSTOM_${System.currentTimeMillis()}",
                                    nombre = "${joya.nombre} (Personalizado)",
                                    materialBase = materialSeleccionado,
                                    pesoGramos = pesoGramos,
                                    precioBaseElaboracion = joya.precioBaseElaboracion,
                                    longitudCm = longitudCm,
                                    imagenRes = joya.imagenRes
                                )
                                is Arito -> Arito(
                                    id = "CUSTOM_${System.currentTimeMillis()}",
                                    nombre = "${joya.nombre} (Personalizado)",
                                    materialBase = materialSeleccionado,
                                    pesoGramos = pesoGramos,
                                    precioBaseElaboracion = joya.precioBaseElaboracion,
                                    tipoCierre = joya.tipoCierre,
                                    imagenRes = joya.imagenRes
                                )
                                else -> Reloj(
                                    id = "CUSTOM_${System.currentTimeMillis()}",
                                    nombre = "${joya.nombre} (Personalizado)",
                                    materialBase = materialSeleccionado,
                                    pesoGramos = pesoGramos,
                                    precioBaseElaboracion = joya.precioBaseElaboracion,
                                    tipoMalla = (joya as? Reloj)?.tipoMalla ?: "Estándar",
                                    imagenRes = joya.imagenRes
                                )
                            }
                            VerexRepository.agregarAlCarrito(joyaPersonalizada)
                            Toast.makeText(context, "¡Agregado al carrito!", Toast.LENGTH_SHORT).show()
                            onNavigateToCart()
                        },
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = "Añadir al Carrito",
                            color = VerexTextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen de la joya
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                if (joya.imagenUri.isNotBlank()) {
                    AsyncImage(
                        model = joya.imagenUri,
                        contentDescription = joya.nombre,
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = if (joya.imagenRes != 0) joya.imagenRes else com.verex.jewelry.R.drawable.logo),
                        placeholder = painterResource(id = if (joya.imagenRes != 0) joya.imagenRes else com.verex.jewelry.R.drawable.logo)
                    )
                } else {
                    Image(
                        painter = painterResource(id = if (joya.imagenRes != 0) joya.imagenRes else com.verex.jewelry.R.drawable.logo),
                        contentDescription = joya.nombre,
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Título y etiqueta de material alineados a la izquierda
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = joya.nombre,
                    color = VerexWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Selecciona el Material",
                    color = VerexWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Selector de Material
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Material.entries.forEach { material ->
                    FilterChip(
                        selected = materialSeleccionado == material,
                        onClick = { materialSeleccionado = material },
                        label = { Text(material.nombre) }
                    )
                }
            }

            // Opciones específicas según el tipo de joya
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                when (joya) {
                    is Anillo -> {
                        Text(
                            text = "Talla: $tallaAnillo",
                            color = VerexWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontStyle = FontStyle.Italic
                        )
                        Slider(
                            value = tallaAnillo.toFloat(),
                            onValueChange = { tallaAnillo = it.toInt() },
                            valueRange = 4f..12f,
                            steps = 7,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFFD700),
                                activeTrackColor = Color(0xFFFFD700)
                            )
                        )
                    }
                    is Cadena, is Pulsera -> {
                        val min = if (joya is Pulsera) 14f else 40f
                        val max = if (joya is Pulsera) 24f else 70f
                        Text(
                            text = "Longitud: $longitudCm cm",
                            color = VerexWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontStyle = FontStyle.Italic
                        )
                        Slider(
                            value = longitudCm.toFloat(),
                            onValueChange = { longitudCm = it.toInt() },
                            valueRange = min..max,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFFD700),
                                activeTrackColor = Color(0xFFFFD700)
                            )
                        )
                    }
                    else -> Unit
                }
            }

            // Espacio inferior extra para asegurar que el contenido scrolleable no quede oculto detrás de la barra fija
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}