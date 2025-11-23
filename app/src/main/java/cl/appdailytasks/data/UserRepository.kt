package cl.appdailytasks.data

import android.util.Log
import cl.appdailytasks.model.Usuario
import cl.appdailytasks.network.BackendApiService
import cl.appdailytasks.network.RetrofitClient

class UserRepository {
    private val apiService: BackendApiService = RetrofitClient.instance
    private val TAG = "UserRepository"

    suspend fun getUsuarioByGoogleId(idGoogle: String): Usuario? {
        return try {
            Log.d(TAG, "Buscando usuario con Google ID: $idGoogle")
            val usuario = apiService.getUsuarioByGoogleId(idGoogle)
            Log.d(TAG, "Usuario encontrado: ${usuario.nombre}")
            usuario
        } catch (e: Exception) {
            Log.e(TAG, "Error al buscar usuario: ${e.message}", e)
            null
        }
    }

    suspend fun createUsuario(usuario: Usuario): Usuario? {
        return try {
            Log.d(TAG, "Creando usuario: ${usuario.nombre} (${usuario.email})")
            val createdUser = apiService.createUsuario(usuario)
            Log.d(TAG, "Usuario creado exitosamente con ID: ${createdUser.id}")
            createdUser
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear usuario: ${e.message}", e)
            null
        }
    }
}
