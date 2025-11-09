package cl.appdailytasks.viewmodel

import android.content.Intent
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

    fun getSignInIntent(): Intent {
        return repository.getSignInIntent()
    }

    fun signInWithIntent(intent: Intent) {
        viewModelScope.launch {
            val (account, errorMessage) = repository.signInWithIntent(intent)
            _googleUser.value = account
            _error.value = errorMessage
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
