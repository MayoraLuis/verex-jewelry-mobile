package com.verex.jewelry.data

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.verex.jewelry.R
import com.verex.jewelry.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.File

object VerexRepository {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val catalogoJoyas: MutableList<Joya> = mutableListOf(
        Anillo(
            id = "A1",
            nombre = "Anillo Solitario Circonia",
            materialBase = Material.PLATA_925,
            pesoGramos = 3.5,
            precioBaseElaboracion = 45.0,
            talla = 7,
            imagenRes = R.drawable.anillo_plata_1
        ),
        Cadena(
            id = "C1",
            nombre = "Cadena Figaro Eslabón Fino",
            materialBase = Material.PLATA_925,
            pesoGramos = 8.0,
            precioBaseElaboracion = 60.0,
            longitudCm = 50,
            imagenRes = R.drawable.cadena_plata
        ),
        Pulsera(
            id = "P1",
            nombre = "Pulsera Tejido",
            materialBase = Material.PLATA_925,
            pesoGramos = 6.0,
            precioBaseElaboracion = 85.0,
            longitudCm = 18,
            imagenRes = R.drawable.cadena_plata
        ),
        Arito(
            id = "AR1",
            nombre = "Set de Perlas",
            materialBase = Material.PLATA_925,
            pesoGramos = 2.0,
            precioBaseElaboracion = 30.0,
            tipoCierre = "Presión",
            imagenRes = R.drawable.set_perlas
        ),
        Reloj(
            id = "R1",
            nombre = "Reloj Cuarzo Silver",
            materialBase = Material.PLATA_925,
            pesoGramos = 25.0,
            precioBaseElaboracion = 150.0,
            tipoMalla = "Acero/Plata",
            imagenRes = R.drawable.logo
        )
    )

    val carrito: MutableList<Joya> = mutableListOf()

    fun agregarAlCarrito(joya: Joya) {
        carrito.add(joya)
    }

    fun limpiarCarrito() {
        carrito.clear()
    }

    fun cerrarSesion() {
        auth.signOut()
    }

    fun obtenerUsuarioActualId(): String {
        return auth.currentUser?.uid ?: ""
    }

    private val catalogoCompletoCache: MutableList<Joya> = mutableListOf()

    fun buscarJoyaPorId(id: String): Joya? {
        return catalogoCompletoCache.firstOrNull { it.id == id }
            ?: catalogoJoyas.firstOrNull { it.id == id }
            ?: catalogoJoyas.firstOrNull()
    }

    fun guardarImagenInterna(context: Context, uriOriginal: Uri, idProducto: String): String {
        return try {
            val archivoDestino = File(context.filesDir, "${idProducto}.jpg")
            context.contentResolver.openInputStream(uriOriginal)?.use { input ->
                archivoDestino.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            archivoDestino.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    suspend fun guardarJoyaEnFirestore(joyaDto: JoyaFirestore): Result<Unit> {
        return try {
            firestore.collection("catalogo")
                .document(joyaDto.id)
                .set(joyaDto)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun obtenerCatalogoEnTiempoReal(): Flow<List<Joya>> = callbackFlow {
        val listener = firestore.collection("catalogo")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(catalogoJoyas)
                    return@addSnapshotListener
                }

                val joyasRemotas = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(JoyaFirestore::class.java)?.toJoya()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }

                val combinadas = if (joyasRemotas.isEmpty()) {
                    catalogoJoyas
                } else {
                    val idsRemotos = joyasRemotas.map { it.id }.toSet()
                    joyasRemotas + catalogoJoyas.filter { it.id !in idsRemotos }
                }

                catalogoCompletoCache.clear()
                catalogoCompletoCache.addAll(combinadas)
                trySend(combinadas)
            }
        awaitClose { listener.remove() }
    }

    suspend fun guardarPedidoEnFirestore(pedido: Pedido): Result<Unit> {
        return try {
            firestore.collection("pedidos")
                .document(pedido.id)
                .set(pedido)
                .await()
            limpiarCarrito()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun obtenerPedidosEnTiempoReal(): Flow<List<Pedido>> = callbackFlow {
        val listener = firestore.collection("pedidos")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val lista = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Pedido::class.java)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }.sortedByDescending { it.fecha }
                trySend(lista)
            }
        awaitClose { listener.remove() }
    }

    fun obtenerPedidosDelCliente(usuarioId: String): Flow<List<Pedido>> = callbackFlow {
        val listener = firestore.collection("pedidos")
            .whereEqualTo("usuarioId", usuarioId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val lista = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Pedido::class.java)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }.sortedByDescending { it.fecha }
                trySend(lista)
            }
        awaitClose { listener.remove() }
    }

    suspend fun actualizarEstadoPedido(pedidoId: String, nuevoEstado: String): Result<Unit> {
        return try {
            firestore.collection("pedidos")
                .document(pedidoId)
                .update("estado", nuevoEstado)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}