package com.verex.jewelry.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verex.jewelry.R
import com.verex.jewelry.data.VerexRepository
import com.verex.jewelry.model.Anillo
import com.verex.jewelry.model.Cadena
import com.verex.jewelry.model.Joya
import com.verex.jewelry.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToQuoter: (String) -> Unit, // Recibe el ID de la joya seleccionada
    onNavigateToCart: () -> Unit,
    onLogout: () -> Unit
) {
    val catalogo = VerexRepository.obtenerCatalogo()
    var busqueda by remember { mutableStateOf("") }
    val categorias = listOf("Todos", "Cadenas", "Anillos", "Pulseras")
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }

    // Filtrado reactivo en tiempo real por categoría y texto de búsqueda
    val catalogoFiltrado = remember(categoriaSeleccionada, busqueda, catalogo) {
        catalogo.filter { joya ->
            // Filtro por tipo de pieza
            val cumpleCategoria = when (categoriaSeleccionada) {
                "Anillos" -> joya is Anillo
                "Cadenas" -> joya is Cadena && (joya.nombre.contains("Cadena", ignoreCase = true) || joya.nombre.contains("Set", ignoreCase = true))
                "Pulseras" -> joya is Cadena && joya.nombre.contains("Pulsera", ignoreCase = true)
                else -> true // "Todos"
            }

            // Filtro por texto en el buscador
            val cumpleBusqueda = if (busqueda.isBlank()) {
                true
            } else {
                joya.nombre.contains(busqueda.trim(), ignoreCase = true) ||
                        joya.materialBase.nombre.contains(busqueda.trim(), ignoreCase = true)
            }

            cumpleCategoria && cumpleBusqueda
        }
    }

    Scaffold(
        containerColor = VerexGreen,
        bottomBar = {
            NavigationBar(
                containerColor = VerexGreenDark,
                contentColor = VerexWhite,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .navigationBarsPadding()
                    .height(68.dp)
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio", tint = VerexWhite) },
                    label = { Text("Inicio", color = VerexWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = VerexGreen
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        // Al hacer clic en "Cotizar" desde la barra inferior, abre el primer producto por defecto
                        val primerId = catalogo.firstOrNull()?.id ?: "A001"
                        onNavigateToQuoter(primerId)
                    },
                    icon = { Icon(Icons.Default.LocalOffer, contentDescription = "Cotizar", tint = VerexWhite) },
                    label = { Text("Cotizar", color = VerexWhite, fontSize = 11.sp) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCart,
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = VerexWhite) },
                    label = { Text("Carrito", color = VerexWhite, fontSize = 11.sp) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        VerexRepository.cerrarSesion()
                        onLogout()
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir", tint = VerexWhite) },
                    label = { Text("Salir", color = VerexWhite, fontSize = 11.sp) }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Joyería Verex",
                modifier = Modifier
                    .fillMaxWidth(0.70f)
                    .height(90.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Barra de búsqueda interactiva
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(CircleShape)
                    .background(VerexInputGray)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (busqueda.isEmpty()) {
                    Text(
                        text = "Buscar...",
                        style = TextStyle(
                            color = VerexTextHint,
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic
                        )
                    )
                }
                BasicTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    singleLine = true,
                    cursorBrush = SolidColor(VerexTextDark),
                    textStyle = TextStyle(
                        color = VerexTextDark,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Botones de filtro interactivos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                categorias.forEach { cat ->
                    val estaSeleccionado = categoriaSeleccionada == cat
                    Button(
                        onClick = { categoriaSeleccionada = cat },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (estaSeleccionado) VerexInputGray else VerexGreenDark
                        ),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .then(
                                if (!estaSeleccionado) Modifier.border(1.dp, VerexWhite.copy(alpha = 0.5f), CircleShape)
                                else Modifier
                            )
                    ) {
                        Text(
                            text = cat,
                            color = if (estaSeleccionado) VerexTextDark else VerexWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cuadrícula que se redibuja según el filtro
            if (catalogoFiltrado.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "No se encontraron joyas en esta categoría.",
                        color = VerexWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(catalogoFiltrado) { joya ->
                        // Pasa el ID único de la joya actual hacia el cotizador
                        JoyaGridCard(
                            joya = joya,
                            onVerDetalle = { onNavigateToQuoter(joya.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JoyaGridCard(joya: Joya, onVerDetalle: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .border(1.5.dp, VerexWhite, RoundedCornerShape(26.dp))
            .background(Color.Transparent)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = joya.imagenRes),
                contentDescription = joya.nombre,
                modifier = Modifier
                    .size(115.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = joya.nombre,
                color = VerexWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                text = "Precio: $${"%.2f".format(joya.calcularPrecioFinal())}",
                color = VerexWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onVerDetalle,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = VerexGreenDark),
                modifier = Modifier
                    .height(32.dp)
                    .border(1.dp, VerexWhite, CircleShape),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp)
            ) {
                Text("Ver Detalle", color = VerexWhite, fontSize = 11.sp)
            }
        }
    }
}