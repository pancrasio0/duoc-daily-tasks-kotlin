package cl.appdailytasks.model

import java.util.Date
// Clase principal de la aplicación, define la estructura de la tarea.
data class Task(
    val id: Long = 0, // Genera un ID único, cambiado a Long para API
    val title: String,
    val description: String,
    val imageUri: String? = null, // Ruta de la imagen
    val notificationTime: Long? = null, // Tiempo en milisegundos para la notificación
    val idGoogle: String? = null // ID de Google del usuario propietario
)
