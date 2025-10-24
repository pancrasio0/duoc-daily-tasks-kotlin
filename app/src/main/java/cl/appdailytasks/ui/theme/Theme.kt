package cl.appdailytasks.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
// Colores de modo oscuro
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)
// Colores de modo claro
private val LightColorScheme = lightColorScheme(
    primary = MediumPurple,
    secondary = DarkGray,
    background = LightLavender,
    surface = SoftLilac,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = AlmostBlack,
    onSurface = AlmostBlack,
)

@Composable
fun AppdailytasksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // Habilita el color dinamico segun tema del celular
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Se asigna a colorscheme segun el tema del telefono
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    // Asigna los colores
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}