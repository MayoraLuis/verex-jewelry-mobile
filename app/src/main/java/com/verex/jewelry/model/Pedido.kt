package com.verex.jewelry.model

data class Pedido(
    val id: String = "",
    val userId: String = "",
    val clienteNombre: String = "",
    val producto: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val total: Double = 0.0,
    val estado: String = "Pendiente",
    val itemsResumen: String = "",
    val fecha: Long = 0L
) {
    // Constructor vacío requerido por Firebase Firestore para deserialización
    constructor() : this("", "", "", "", "", 0.0, 0.0, "Pendiente", "", 0L)
}