package cl.appdailytasks.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import kotlinx.coroutines.tasks.await
import cl.appdailytasks.R

class GoogleAuthUiClient(
    private val context: Context
) {

    private val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    private val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    suspend fun signInWithIntent(intent: Intent): Pair<GoogleSignInAccount?, String?> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account = task.await()
            Pair(account, null)
        } catch (e: ApiException) {
            e.printStackTrace()
            val errorMessage = when (e.statusCode) {
                CommonStatusCodes.DEVELOPER_ERROR -> "Error del desarrollador. Asegúrate de que el ID de cliente web y la firma SHA-1 están bien configurados en Google Cloud."
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> "Inicio de sesión cancelado."
                else -> "Error de inicio de sesión de Google: ${e.statusCode}"
            }
            Pair(null, errorMessage)
        }
    }

    suspend fun signOut() {
        googleSignInClient.signOut().await()
    }
}
