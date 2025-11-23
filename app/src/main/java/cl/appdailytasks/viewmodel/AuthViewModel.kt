package cl.appdailytasks.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.appdailytasks.auth.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _googleUser = MutableStateFlow<GoogleSignInAccount?>(null)
    val googleUser = _googleUser.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _signOutEvent = MutableSharedFlow<Unit>()
    val signOutEvent = _signOutEvent.asSharedFlow()
    
    private val TAG = "AuthViewModel"

    fun getSignInIntent(): Intent {
        return repository.getSignInIntent()
    }

    private val userRepository = cl.appdailytasks.data.UserRepository()

    fun signInWithIntent(intent: Intent) {
        viewModelScope.launch {
            val (account, errorMessage) = repository.signInWithIntent(intent)
            _googleUser.value = account
            _error.value = errorMessage

            account?.let {
                val idGoogle = it.id
                val email = it.email
                val name = it.displayName
                Log.d(TAG, "Usuario Google autenticado: $name ($email)")
                
                if (idGoogle != null && email != null && name != null) {
                    Log.d(TAG, "Verificando si usuario existe en backend...")
                    val existingUser = userRepository.getUsuarioByGoogleId(idGoogle)
                    
                    if (existingUser == null) {
                        Log.d(TAG, "Usuario no existe, creando nuevo usuario...")
                        val photoUrl = it.photoUrl?.toString()
                        val newUser = cl.appdailytasks.model.Usuario(
                            idGoogle = idGoogle, 
                            email = email, 
                            nombre = name,
                            imgUsuario = photoUrl
                        )
                        val createdUser = userRepository.createUsuario(newUser)
                        
                        if (createdUser != null) {
                            Log.d(TAG, "Usuario creado exitosamente con ID: ${createdUser.id}")
                        } else {
                            Log.e(TAG, "ERROR: No se pudo crear el usuario en el backend")
                            _error.value = "No se pudo registrar el usuario en el servidor"
                        }
                    } else {
                        Log.d(TAG, "Usuario ya existe con ID: ${existingUser.id}")
                    }
                } else {
                    Log.e(TAG, "ERROR: Datos de Google incompletos")
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _googleUser.value = null
            _signOutEvent.emit(Unit)
        }
    }

    fun clearError() {
        _error.value = null
    }
}
