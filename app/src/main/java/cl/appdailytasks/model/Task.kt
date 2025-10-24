package cl.appdailytasks.model

import java.util.Date
// Clase principal de la aplicación, define la estructura de la tarea.
data class Task(
    val id: Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(), // Genera un ID único
    val title: String,
    val description: String,
    val imageUri: String? = null, // Ruta de la imagen
    val notificationTime: Long? = null // Tiempo en milisegundos para la notificación
)
