package cl.appdailytasks.model

import java.util.Date

data class Task(
    val id: Int,
    val name: String,
    val description: String,
    val date: Date? = null, // La fecha puede ser opcional
    val imageUrl: String? = null // La imagen puede ser opcional
)
