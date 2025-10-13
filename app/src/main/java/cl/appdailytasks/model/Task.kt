package cl.appdailytasks.model

import android.net.Uri
import java.util.Date

data class Task(
    val id: Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
    val title: String,
    val description: String,
    val imageUri: Uri? = null,
    val notificationTime: Long? = null
)