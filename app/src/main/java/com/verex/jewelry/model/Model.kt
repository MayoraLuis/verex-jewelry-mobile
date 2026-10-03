package com.verex.jewelry.model

enum class Material(val nombre: String, val multiplicadorGramo: Double) {
    PLATA_925("Plata .925", 2.5),
    ORO_10K("Oro 10K", 45.0),
    ORO_14K("Oro 14K", 65.0)
}

enum class EstadoPedido {
    RECIBIDO,
    EN_FABRICACION,
    EMPAQUETADO,
    ENVIADO
}

enum class RolUsuario {
    CLIENTE,
    ADMINISTRADOR
}

data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rol: RolUsuario
)