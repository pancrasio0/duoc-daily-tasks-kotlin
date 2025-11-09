package cl.appdailytasks.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cl.appdailytasks.R

@Composable
fun LoginScreen(onOfflineMode: () -> Unit, onGoogleSignIn: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(165, 244, 255)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_playstore),
                contentDescription = "App Logo",
                modifier = Modifier.size(256.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onOfflineMode) {
                Text("Modo Offline")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onGoogleSignIn) {
                Text("Conectar con Google")
            }
        }
    }
}
