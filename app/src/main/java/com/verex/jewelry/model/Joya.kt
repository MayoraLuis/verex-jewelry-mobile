package com.verex.jewelry.model

abstract class Joya(
    val id: String,
    val nombre: String,
    var materialBase: Material,
    var pesoGramos: Double,
    var precioBaseElaboracion: Double,
    val imagenRes: Int // Identificador del recurso R.drawable
) {
    abstract fun calcularPrecioFinal(): Double
}

class Anillo(
    id: String,
    nombre: String,
    materialBase: Material,
    pesoGramos: Double,
    precioBaseElaboracion: Double,
    val talla: Int,
    imagenRes: Int
) : Joya(id, nombre, materialBase, pesoGramos, precioBaseElaboracion, imagenRes) {
    override fun calcularPrecioFinal(): Double {
        val costoMaterial = pesoGramos * materialBase.multiplicadorGramo
        val costoTallaExtra = if (talla > 7) (talla - 7) * 5.0 else 0.0
        return precioBaseElaboracion + costoMaterial + costoTallaExtra
    }
}

class Cadena(
    id: String,
    nombre: String,
    materialBase: Material,
    pesoGramos: Double,
    precioBaseElaboracion: Double,
    val longitudCm: Int,
    imagenRes: Int
) : Joya(id, nombre, materialBase, pesoGramos, precioBaseElaboracion, imagenRes) {
    override fun calcularPrecioFinal(): Double {
        val costoMaterial = pesoGramos * materialBase.multiplicadorGramo
        val costoLongitudExtra = if (longitudCm > 45) (longitudCm - 45) * 1.5 else 0.0
        return precioBaseElaboracion + costoMaterial + costoLongitudExtra
    }
}