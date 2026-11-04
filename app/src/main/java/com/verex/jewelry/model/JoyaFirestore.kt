package com.verex.jewelry.model

// DTO plano para serialización y deserialización en Firebase Firestore
data class JoyaFirestore(
    val id: String = "",
    val nombre: String = "",
    val tipo: String = "Anillo", // Anillo, Cadena, Pulsera, Arito, Reloj
    val material: String = "PLATA_925",
    val pesoGramos: Double = 0.0,
    val precioBase: Double = 0.0,
    val imagenUri: String = "",
    val extraProp: Int = 7 // Talla o Longitud o atributo específico
) {
    fun toJoya(): Joya {
        val matEnum = try { Material.valueOf(material) } catch (e: Exception) { Material.PLATA_925 }
        val defaultImage = com.verex.jewelry.R.drawable.logo
        return when (tipo) {
            "Cadena" -> Cadena(id, nombre, matEnum, pesoGramos, precioBase, extraProp, defaultImage, imagenUri)
            "Pulsera" -> Pulsera(id, nombre, matEnum, pesoGramos, precioBase, extraProp, defaultImage, imagenUri)
            "Arito" -> Arito(id, nombre, matEnum, pesoGramos, precioBase, "Mariposa", defaultImage, imagenUri)
            "Reloj" -> Reloj(id, nombre, matEnum, pesoGramos, precioBase, "Eslabones", defaultImage, imagenUri)
            else -> Anillo(id, nombre, matEnum, pesoGramos, precioBase, extraProp, defaultImage, imagenUri)
        }
    }
}