package com.verex.jewelry.data

import com.verex.jewelry.R
import com.verex.jewelry.model.*

object VerexRepository {

    // Catálogo en memoria
    private val catalogo: MutableList<Joya> = mutableListOf(
        Anillo(
            id = "A001",
            nombre = "Anillo Plata 925",
            materialBase = Material.PLATA_925,
            pesoGramos = 3.5,
            precioBaseElaboracion = 25.0,
            talla = 7,
            imagenRes = R.drawable.anillo_plata_1
        ),
        Anillo(
            id = "A002",
            nombre = "Anillo Solitario Plata",
            materialBase = Material.PLATA_925,
            pesoGramos = 4.0,
            precioBaseElaboracion = 45.0,
            talla = 6,
            imagenRes = R.drawable.anillo_plata_2
        ),
        Cadena(
            id = "C001",
            nombre = "Set de Perlas y Cadena",
            materialBase = Material.PLATA_925,
            pesoGramos = 8.5,
            precioBaseElaboracion = 35.0,
            longitudCm = 45,
            imagenRes = R.drawable.set_perlas
        ),
        Cadena(
            id = "P001",
            nombre = "Pulsera Plata 925",
            materialBase = Material.PLATA_925,
            pesoGramos = 5.0,
            precioBaseElaboracion = 28.0,
            longitudCm = 18,
            imagenRes = R.drawable.pulsera_plata
        )
    )

    // Carrito de compras: Lista de pares (Joya, Cantidad)
    private val carrito: MutableList<Pair<Joya, Int>> = mutableListOf()

    // Usuarios del sistema
    private val usuarios: MutableList<Usuario> = mutableListOf(
        Usuario(
            id = "U001",
            nombre = "Cliente Verex",
            correo = "cliente@verex.com",
            contrasena = "123456",
            rol = RolUsuario.CLIENTE
        ),
        Usuario(
            id = "U002",
            nombre = "Administrador Taller",
            correo = "admin@verex.com",
            contrasena = "admin123",
            rol = RolUsuario.ADMINISTRADOR
        ),
        Usuario(
            id = "U003",
            nombre = "Luis Mayora",
            correo = "luis.mayorac@gmail.com",
            contrasena = "123456",
            rol = RolUsuario.CLIENTE
        )
    )

    // Lista de pedidos inicializada con un pedido de demostración
    private val pedidos: MutableList<Pedido> = mutableListOf(
        Pedido(
            idPedido = "PED-1024",
            cliente = usuarios[2], // Luis Mayora
            joya = catalogo[0],
            cantidad = 1,
            total = 37.75,
            estado = EstadoPedido.RECIBIDO
        )
    )

    var usuarioActual: Usuario? = null

    // --- CATÁLOGO ---
    fun obtenerCatalogo(): List<Joya> = catalogo.toList()

    fun buscarJoyaPorId(id: String): Joya? {
        return catalogo.find { it.id.equals(id.trim(), ignoreCase = true) }
    }

    fun agregarJoya(joya: Joya): Boolean {
        return try {
            catalogo.add(joya)
            true
        } catch (e: Exception) {
            false
        }
    }

    // --- CARRITO ---
    fun obtenerCarrito(): List<Pair<Joya, Int>> = carrito.toList()

    fun agregarAlCarrito(joya: Joya, cantidad: Int = 1) {
        val index = carrito.indexOfFirst { it.first.id.equals(joya.id, ignoreCase = true) }
        if (index != -1) {
            val actual = carrito[index]
            carrito[index] = Pair(actual.first, actual.second + cantidad)
        } else {
            carrito.add(Pair(joya, cantidad))
        }
    }

    fun actualizarCantidad(id: String, nuevaCantidad: Int) {
        val index = carrito.indexOfFirst { it.first.id.equals(id.trim(), ignoreCase = true) }
        if (index != -1) {
            if (nuevaCantidad > 0) {
                carrito[index] = Pair(carrito[index].first, nuevaCantidad)
            } else {
                carrito.removeAt(index)
            }
        }
    }

    fun limpiarCarrito() {
        carrito.clear()
    }

    // --- PEDIDOS & ADMIN ---
    fun obtenerPedidosAdmin(): List<Pedido> = pedidos.toList()

    fun agregarPedido(pedido: Pedido) {
        pedidos.add(pedido)
    }

    fun registrarPedidosDesdeCarrito(costoEnvio: Double = 4.0) {
        val cliente = usuarioActual ?: usuarios.first()
        carrito.forEach { par ->
            val joya = par.first
            val cantidad = par.second
            val totalItem = (joya.calcularPrecioFinal() * cantidad) + costoEnvio
            val nuevoPedido = Pedido(
                idPedido = "PED-" + (1000..9999).random(),
                cliente = cliente,
                joya = joya,
                cantidad = cantidad,
                total = totalItem,
                estado = EstadoPedido.RECIBIDO
            )
            pedidos.add(nuevoPedido)
        }
    }

    fun actualizarEstadoPedido(idPedido: String, nuevoEstado: EstadoPedido): Boolean {
        val pedido = pedidos.find { it.idPedido.equals(idPedido.trim(), ignoreCase = true) }
        return if (pedido != null) {
            pedido.estado = nuevoEstado
            true
        } else {
            false
        }
    }

    // --- AUTENTICACIÓN ---
    fun registrarUsuario(usuario: Usuario): Boolean {
        val yaExiste = usuarios.any { it.correo.equals(usuario.correo.trim(), ignoreCase = true) }
        if (yaExiste) return false
        usuarios.add(usuario)
        usuarioActual = usuario
        return true
    }

    fun iniciarSesion(correo: String, contrasena: String): Usuario? {
        val user = usuarios.find {
            it.correo.equals(correo.trim(), ignoreCase = true) && it.contrasena == contrasena.trim()
        }
        usuarioActual = user
        return user
    }

    fun cerrarSesion() {
        usuarioActual = null
    }
}