package cl.appdailytasks.view

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import cl.appdailytasks.MainActivity
import cl.appdailytasks.R
// Funcion para crear notificaciones de las tareas
class TaskNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("TASK_ID", 0)
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "¡Tarea Pendiente!"
        val taskDescription = intent.getStringExtra("TASK_DESC") ?: "Es hora de empezar."

        val imageUriString = intent.getStringExtra("TASK_IMAGE_URI")
        var taskImageBitmap: Bitmap? = null

        // ÚNICAMENTE intentamos cargar una imagen si la tarea la tiene.
        // Deprecado
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            taskImageBitmap = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        val activityIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "task_channel")

            .setSmallIcon(R.drawable.ic_stat_image)
            .setContentTitle(taskTitle)
            .setContentText(taskDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)



        if (taskImageBitmap != null) {
            builder.setLargeIcon(taskImageBitmap)
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(taskImageBitmap)
                    .bigLargeIcon(null as Bitmap?)
            )
        }



        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(taskId, builder.build())
        }
    }
}

