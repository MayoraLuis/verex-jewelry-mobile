package com.verex.jewelry.model

data class Pedido(
    val idPedido: String,
    val cliente: Usuario,
    val joya: Joya,
    val cantidad: Int = 1,
    val total: Double = 0.0,
    val grabadoPersonalizado: String = "",
    var estado: EstadoPedido = EstadoPedido.RECIBIDO
) {
    fun calcularTotalPedido(): Double {
        val subtotal = joya.calcularPrecioFinal() * cantidad
        val costoGrabado = if (grabadoPersonalizado.isNotBlank()) 10.0 else 0.0
        return subtotal + costoGrabado
    }
}