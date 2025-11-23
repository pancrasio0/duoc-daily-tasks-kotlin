package cl.appdailytasks.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class AuthRepository(private val context: Context) {

    private val googleAuthUiClient = GoogleAuthUiClient(context)

    fun getSignInIntent(): Intent {
        return googleAuthUiClient.getSignInIntent()
    }

    suspend fun signInWithIntent(intent: Intent): Pair<GoogleSignInAccount?, String?> {
        return googleAuthUiClient.signInWithIntent(intent)
    }

    suspend fun signOut() {
        googleAuthUiClient.signOut()
    }
}
