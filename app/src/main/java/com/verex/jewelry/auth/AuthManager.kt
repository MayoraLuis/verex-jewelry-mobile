package com.verex.jewelry

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    val usuarioActual: FirebaseUser?
        get() = auth.currentUser

    // 1. Registro con Correo y Contraseña
    suspend fun registrarConCorreo(nombre: String, correo: String, contrasena: String): Result<String> {
        return try {
            val resultadoAuth = withTimeout(5000L) {
                auth.createUserWithEmailAndPassword(correo.trim(), contrasena.trim()).await()
            }
            val uid = resultadoAuth.user?.uid ?: throw Exception("No se pudo obtener el UID del usuario")

            val correoTrim = correo.trim().lowercase()
            val esAdmin = correoTrim == "admin@verex.com" || correoTrim.contains("admin")
            val rol = if (esAdmin) "admin" else "CLIENTE"

            val datosUsuario = hashMapOf(
                "id" to uid,
                "nombre" to nombre.trim(),
                "correo" to correo.trim(),
                "rol" to rol
            )

            try {
                withTimeout(3000L) {
                    firestore.collection("usuarios").document(uid).set(datosUsuario).await()
                }
            } catch (_: Exception) {
                // Ignorar si Firestore demora, la autenticación ya fue exitosa
            }

            Result.success(rol)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 2. Inicio de Sesión (con timeout de 5s para evitar que se quede colgado indefinidamente)
    suspend fun iniciarSesion(correo: String, contrasena: String): Result<String> {
        return try {
            val correoTrim = correo.trim().lowercase()
            val contrasenaTrim = contrasena.trim()

            val resultadoAuth = try {
                withTimeout(5000L) {
                    auth.signInWithEmailAndPassword(correoTrim, contrasenaTrim).await()
                }
            } catch (e: Exception) {
                if (correoTrim == "admin@verex.com") {
                    try {
                        withTimeout(5000L) {
                            val createResult = auth.createUserWithEmailAndPassword(correoTrim, contrasenaTrim).await()
                            val uid = createResult.user?.uid
                            if (uid != null) {
                                val datosAdmin = hashMapOf(
                                    "id" to uid,
                                    "nombre" to "Administrador",
                                    "correo" to correoTrim,
                                    "rol" to "admin"
                                )
                                firestore.collection("usuarios").document(uid).set(datosAdmin)
                            }
                            createResult
                        }
                    } catch (_: Exception) {
                        throw e
                    }
                } else {
                    throw e
                }
            }

            val uid = resultadoAuth.user?.uid ?: throw Exception("Usuario no autenticado")
            val esAdmin = correoTrim == "admin@verex.com" || correoTrim.contains("admin")
            val rolPorDefecto = if (esAdmin) "admin" else "CLIENTE"

            val rol = try {
                withTimeout(3000L) {
                    val doc = firestore.collection("usuarios").document(uid).get().await()
                    val rolDoc = if (doc.exists() && doc.contains("rol")) doc.getString("rol") else null

                    val esAdminDefinitivo = esAdmin || (rolDoc != null && rolDoc.trim().equals("admin", ignoreCase = true))

                    if (esAdminDefinitivo) {
                        // Asegurar que quede guardado como admin en Firestore para futuras consultas
                        val datosAdmin = hashMapOf<String, Any>(
                            "id" to uid,
                            "nombre" to if (doc.exists() && doc.contains("nombre")) (doc.getString("nombre") ?: "Administrador") else "Administrador",
                            "correo" to correoTrim,
                            "rol" to "admin"
                        )
                        firestore.collection("usuarios").document(uid).set(datosAdmin)
                        "admin"
                    } else {
                        if (!doc.exists()) {
                            val datosCliente = hashMapOf<String, Any>(
                                "id" to uid,
                                "nombre" to "Cliente",
                                "correo" to correoTrim,
                                "rol" to "CLIENTE"
                            )
                            firestore.collection("usuarios").document(uid).set(datosCliente)
                        }
                        rolDoc ?: rolPorDefecto
                    }
                }
            } catch (_: Exception) {
                rolPorDefecto
            }

            Result.success(rol)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. Inicio de Sesión con Google
    suspend fun iniciarSesionConGoogle(credential: AuthCredential): Result<String> {
        return try {
            val authResult = withTimeout(5000L) {
                auth.signInWithCredential(credential).await()
            }
            val firebaseUser = authResult.user ?: throw Exception("No se pudo autenticar con Google")
            val uid = firebaseUser.uid
            val correo = firebaseUser.email ?: "correo@desconocido.com"
            val nombre = firebaseUser.displayName ?: "Cliente Google"

            val correoTrim = correo.trim().lowercase()
            val esAdmin = correoTrim == "admin@verex.com" || correoTrim.contains("admin")
            val rolPorDefecto = if (esAdmin) "admin" else "CLIENTE"

            // Validar o registrar en Firestore
            val rol = try {
                withTimeout(3000L) {
                    val doc = firestore.collection("usuarios").document(uid).get().await()
                    if (!doc.exists()) {
                        val datosUsuario = hashMapOf<String, Any>(
                            "id" to uid,
                            "nombre" to nombre,
                            "correo" to correo,
                            "rol" to rolPorDefecto
                        )
                        firestore.collection("usuarios").document(uid).set(datosUsuario).await()
                        rolPorDefecto
                    } else {
                        val rolDoc = doc.getString("rol")
                        rolDoc ?: rolPorDefecto
                    }
                }
            } catch (_: Exception) {
                rolPorDefecto
            }

            Result.success(rol)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 4. NUEVO: Inicio de Sesión con Facebook
    suspend fun iniciarSesionConFacebook(credential: AuthCredential): Result<String> {
        return try {
            val authResult = withTimeout(5000L) {
                auth.signInWithCredential(credential).await()
            }
            val firebaseUser = authResult.user ?: throw Exception("No se pudo autenticar con Facebook")
            val uid = firebaseUser.uid
            val correo = firebaseUser.email ?: "correo_facebook@desconocido.com"
            val nombre = firebaseUser.displayName ?: "Cliente Facebook"

            val correoTrim = correo.trim().lowercase()
            val esAdmin = correoTrim == "admin@verex.com" || correoTrim.contains("admin")
            val rolPorDefecto = if (esAdmin) "admin" else "CLIENTE"

            // Validar o registrar en Firestore
            val rol = try {
                withTimeout(3000L) {
                    val doc = firestore.collection("usuarios").document(uid).get().await()
                    if (!doc.exists()) {
                        val datosUsuario = hashMapOf<String, Any>(
                            "id" to uid,
                            "nombre" to nombre,
                            "correo" to correo,
                            "rol" to rolPorDefecto
                        )
                        firestore.collection("usuarios").document(uid).set(datosUsuario).await()
                        rolPorDefecto
                    } else {
                        val rolDoc = doc.getString("rol")
                        rolDoc ?: rolPorDefecto
                    }
                }
            } catch (_: Exception) {
                rolPorDefecto
            }

            Result.success(rol)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 5. Cerrar Sesión
    fun cerrarSesion() {
        auth.signOut()
    }
}