package com.verex.jewelry.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.verex.jewelry.data.VerexRepository
import com.verex.jewelry.ui.theme.*
import java.util.Locale
import com.verex.jewelry.model.Pedido
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientOrdersScreen(
    onNavigateBack: () -> Unit
) {
    val usuarioId = remember { VerexRepository.obtenerUsuarioActualId() }
    val pedidosFlow = remember(usuarioId) { VerexRepository.obtenerPedidosDelCliente(usuarioId) }
    val listaPedidos: List<Pedido> by pedidosFlow.collectAsState(initial = emptyList())

    Scaffold(
        containerColor = VerexGreen,
        topBar = {
            TopAppBar(
                title = { Text("Mis Pedidos", color = VerexWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = VerexWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VerexGreenDark)
            )
        }
    ) { paddingValues ->
        if (listaPedidos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No has realizado ningún pedido aún.", color = VerexWhite, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(
                    items = listaPedidos,
                    key = { index, pedido -> if (pedido.id.isNotBlank()) "${pedido.id}_$index" else "$index" }
                ) { _, pedido: Pedido ->
                    ItemPedidoCliente(pedido = pedido)
                }
            }
        }
    }
}

@Composable
fun ItemPedidoCliente(pedido: Pedido) {
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
                    text = "Orden #${pedido.id.takeLast(6).uppercase()}",
                    color = VerexWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "$${String.format(Locale.US, "%.2f", pedido.total)}",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = pedido.itemsResumen, color = VerexWhite.copy(alpha = 0.85f), fontSize = 13.sp)

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de progreso y estado
            val progreso = when (pedido.estado.uppercase()) {
                "RECIBIDO" -> 0.25f
                "EN_FABRICACION" -> 0.60f
                "EMPAQUETADO" -> 0.85f
                "ENVIADO" -> 1.0f
                else -> 0.10f
            }

            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFFFFD700),
                trackColor = Color.White.copy(alpha = 0.2f),
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Estado: ${pedido.estado.replace("_", " ")}",
                color = VerexWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}