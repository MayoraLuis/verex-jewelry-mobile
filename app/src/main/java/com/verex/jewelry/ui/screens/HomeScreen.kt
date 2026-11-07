package com.verex.jewelry.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import com.verex.jewelry.R
import com.verex.jewelry.data.VerexRepository
import com.verex.jewelry.model.Anillo
import com.verex.jewelry.model.Arito
import com.verex.jewelry.model.Cadena
import com.verex.jewelry.model.Joya
import com.verex.jewelry.model.Pulsera
import com.verex.jewelry.model.Reloj
import com.verex.jewelry.ui.theme.VerexGreen
import com.verex.jewelry.ui.theme.VerexGreenDark
import com.verex.jewelry.ui.theme.VerexInputGray
import com.verex.jewelry.ui.theme.VerexTextDark
import com.verex.jewelry.ui.theme.VerexWhite

@Composable
fun HomeScreen(
    onNavigateToQuoter: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit
) {
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }
    var textoBusqueda by remember { mutableStateOf("") }
    val categorias = listOf("Todos", "Cadenas", "Anillos", "Pulseras", "Aritos", "Relojes")

    val catalogoFlow = remember { VerexRepository.obtenerCatalogoEnTiempoReal() }
    val catalogo: List<Joya> by catalogoFlow.collectAsState(initial = VerexRepository.catalogoJoyas)

    val joyasFiltradas: List<Joya> = catalogo.filter { joya: Joya ->
        val coincideCategoria = when (categoriaSeleccionada) {
            "Cadenas" -> joya is Cadena
            "Anillos" -> joya is Anillo
            "Pulseras" -> joya is Pulsera
            "Aritos" -> joya is Arito
            "Relojes" -> joya is Reloj
            else -> true
        }
        val coincideBusqueda = joya.nombre.contains(textoBusqueda, ignoreCase = true)
        coincideCategoria && coincideBusqueda
    }

    Scaffold(
        containerColor = VerexGreen,
        bottomBar = {
            NavigationBar(
                containerColor = VerexGreenDark,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VerexGreenDark,
                        selectedTextColor = VerexWhite,
                        indicatorColor = VerexWhite,
                        unselectedIconColor = VerexWhite.copy(alpha = 0.7f),
                        unselectedTextColor = VerexWhite.copy(alpha = 0.7f)
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        val primeraJoya = catalogo.firstOrNull()?.id ?: ""
                        onNavigateToQuoter(primeraJoya)
                    },
                    icon = { Icon(Icons.Default.LocalOffer, contentDescription = "Cotizar") },
                    label = { Text("Cotizar", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = VerexWhite.copy(alpha = 0.7f),
                        unselectedTextColor = VerexWhite.copy(alpha = 0.7f)
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCart,
                    icon = {
                        BadgedBox(
                            badge = {
                                if (VerexRepository.carrito.isNotEmpty()) {
                                    Badge(containerColor = Color(0xFFFFD700)) {
                                        Text("${VerexRepository.carrito.size}", color = VerexTextDark, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                        }
                    },
                    label = { Text("Carrito", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = VerexWhite.copy(alpha = 0.7f),
                        unselectedTextColor = VerexWhite.copy(alpha = 0.7f)
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToOrders,
                    icon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = "Mis Pedidos") },
                    label = { Text("Pedidos", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = VerexWhite.copy(alpha = 0.7f),
                        unselectedTextColor = VerexWhite.copy(alpha = 0.7f)
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onLogout,
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir") },
                    label = { Text("Salir", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = VerexWhite.copy(alpha = 0.7f),
                        unselectedTextColor = VerexWhite.copy(alpha = 0.7f)
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Verex",
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(60.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                placeholder = { Text("Buscar...", color = VerexTextDark.copy(alpha = 0.6f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = VerexTextDark) },
                singleLine = true,
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = VerexInputGray,
                    unfocusedContainerColor = VerexInputGray,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = VerexTextDark,
                    unfocusedTextColor = VerexTextDark
                ),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorias) { categoria ->
                    val esSeleccionada = categoriaSeleccionada == categoria
                    Button(
                        onClick = { categoriaSeleccionada = categoria },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (esSeleccionada) VerexInputGray else Color.Transparent,
                            contentColor = if (esSeleccionada) VerexTextDark else VerexWhite
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(
                            text = categoria,
                            fontSize = 12.sp,
                            fontWeight = if (esSeleccionada) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (joyasFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron joyas en esta categoría.",
                        color = VerexWhite,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(
                        items = joyasFiltradas,
                        key = { index, joya -> if (joya.id.isNotBlank()) "${joya.id}_$index" else "$index" }
                    ) { _, joya: Joya ->
                        ItemJoyaCatalogo(
                            joya = joya,
                            onClick = { onNavigateToQuoter(joya.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemJoyaCatalogo(
    joya: Joya,
    onClick: () -> Unit
) {
    // Tarjeta con fondo claro (VerexInputGray o Blanco) y borde fino blanco
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VerexInputGray),
        border = BorderStroke(1.dp, Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                val fallbackRes = if (joya.imagenRes != 0) joya.imagenRes else com.verex.jewelry.R.drawable.logo
                if (joya.imagenUri.isNotBlank()) {
                    AsyncImage(
                        model = joya.imagenUri,
                        contentDescription = joya.nombre,
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = fallbackRes),
                        placeholder = painterResource(id = fallbackRes)
                    )
                } else {
                    Image(
                        painter = painterResource(id = fallbackRes),
                        contentDescription = joya.nombre,
                        modifier = Modifier.fillMaxSize().padding(6.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Textos en color oscuro para contrastar perfectamente con el fondo claro de la tarjeta
            Text(
                text = joya.nombre,
                color = VerexTextDark,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Desde: $${String.format("%.2f", joya.precioBaseElaboracion)}",
                color = VerexGreenDark,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onClick,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = VerexGreen),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Ver Detalle",
                    color = VerexWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}