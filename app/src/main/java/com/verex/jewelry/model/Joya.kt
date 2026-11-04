package com.verex.jewelry.model

enum class Material(val nombre: String, val multiplicadorGramo: Double) {
    PLATA_925("Plata .925", 2.5),
    ORO_10K("Oro 10K", 38.0),
    ORO_14K("Oro 14K", 52.0)
}

abstract class Joya(
    val id: String,
    val nombre: String,
    var materialBase: Material,
    var pesoGramos: Double,
    var precioBaseElaboracion: Double,
    val imagenRes: Int,
    val imagenUri: String = ""
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
    imagenRes: Int,
    imagenUri: String = ""
) : Joya(id, nombre, materialBase, pesoGramos, precioBaseElaboracion, imagenRes, imagenUri) {
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
    imagenRes: Int,
    imagenUri: String = ""
) : Joya(id, nombre, materialBase, pesoGramos, precioBaseElaboracion, imagenRes, imagenUri) {
    override fun calcularPrecioFinal(): Double {
        val costoMaterial = pesoGramos * materialBase.multiplicadorGramo
        val costoLongitudExtra = if (longitudCm > 45) (longitudCm - 45) * 1.5 else 0.0
        return precioBaseElaboracion + costoMaterial + costoLongitudExtra
    }
}

class Pulsera(
    id: String,
    nombre: String,
    materialBase: Material,
    pesoGramos: Double,
    precioBaseElaboracion: Double,
    val longitudCm: Int = 18,
    imagenRes: Int,
    imagenUri: String = ""
) : Joya(id, nombre, materialBase, pesoGramos, precioBaseElaboracion, imagenRes, imagenUri) {
    override fun calcularPrecioFinal(): Double {
        val costoMaterial = pesoGramos * materialBase.multiplicadorGramo
        val costoLongitudExtra = if (longitudCm > 18) (longitudCm - 18) * 2.0 else 0.0
        return precioBaseElaboracion + costoMaterial + costoLongitudExtra
    }
}

class Arito(
    id: String,
    nombre: String,
    materialBase: Material,
    pesoGramos: Double,
    precioBaseElaboracion: Double,
    val tipoCierre: String = "Mariposa",
    imagenRes: Int,
    imagenUri: String = ""
) : Joya(id, nombre, materialBase, pesoGramos, precioBaseElaboracion, imagenRes, imagenUri) {
    override fun calcularPrecioFinal(): Double {
        val costoMaterial = pesoGramos * materialBase.multiplicadorGramo
        return precioBaseElaboracion + costoMaterial
    }
}

class Reloj(
    id: String,
    nombre: String,
    materialBase: Material,
    pesoGramos: Double,
    precioBaseElaboracion: Double,
    val tipoMalla: String = "Eslabones",
    imagenRes: Int,
    imagenUri: String = ""
) : Joya(id, nombre, materialBase, pesoGramos, precioBaseElaboracion, imagenRes, imagenUri) {
    override fun calcularPrecioFinal(): Double {
        val costoMaterial = pesoGramos * materialBase.multiplicadorGramo
        return precioBaseElaboracion + costoMaterial + 50.0
    }
}