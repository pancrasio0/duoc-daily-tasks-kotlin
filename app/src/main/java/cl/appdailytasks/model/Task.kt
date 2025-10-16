package cl.appdailytasks.model

import java.util.Date

data class Task(
    val id: Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
    val title: String,
    val description: String,
    val imageUri: String? = null,
    val notificationTime: Long? = null
)
