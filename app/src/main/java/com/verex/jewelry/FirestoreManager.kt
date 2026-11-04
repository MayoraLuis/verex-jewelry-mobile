package com.verex.jewelry

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.verex.jewelry.model.Pedido

class FirestoreManager {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Guardar un nuevo pedido/cotización en Firestore
    suspend fun guardarPedido(producto: String, descripcion: String, precio: Double): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
            val nuevoPedidoRef = db.collection("pedidos").document()

            val pedido = Pedido(
                id = nuevoPedidoRef.id,
                userId = userId,
                producto = producto,
                descripcion = descripcion,
                precio = precio
            )

            nuevoPedidoRef.set(pedido).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener el historial de pedidos únicamente del usuario actual
    suspend fun obtenerPedidosUsuario(): Result<List<Pedido>> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
            val querySnapshot = db.collection("pedidos")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val listaPedidos = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Pedido::class.java)
            }

            Result.success(listaPedidos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}