package cl.appdailytasks.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.appdailytasks.model.Task
import cl.appdailytasks.model.TaskDatabaseHelper
import cl.appdailytasks.view.TaskNotificationReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = TaskDatabaseHelper(application)
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _tasks.value = dbHelper.getAllTasks()
        }
    }

    fun getTask(id: Int): Task? {
        return _tasks.value.find { it.id == id }
    }

    fun addTask(title: String, description: String, imageUri: Uri?, notificationTime: Long?) {
        viewModelScope.launch {
            val newTask = Task(title = title, description = description, imageUri = imageUri, notificationTime = notificationTime)
            val newId = dbHelper.insertTask(newTask)
            loadTasks() // Recargar tareas para obtener la lista actualizada con el nuevo ID
            if (notificationTime != null) {
                // Necesitamos encontrar la tarea que acabamos de insertar para obtener su ID de la base de datos
                // Una forma simple pero no la más eficiente es buscarla por sus atributos
                // Una mejor implementación de dbHelper.insertTask devolvería el objeto completo o su ID de forma más directa
                val insertedTask = dbHelper.getAllTasks().find { it.title == title && it.description == description && it.notificationTime == notificationTime }
                insertedTask?.let {
                    scheduleNotification(getApplication(), it)
                }
            }
        }
    }

    fun updateTask(updatedTask: Task) {
        viewModelScope.launch {
            val oldTask = getTask(updatedTask.id)
            dbHelper.updateTask(updatedTask)
            loadTasks()

            if (oldTask?.notificationTime != updatedTask.notificationTime) {
                oldTask?.notificationTime?.let { cancelNotification(getApplication(), oldTask) }
            }
            updatedTask.notificationTime?.let { scheduleNotification(getApplication(), updatedTask) }
        }
    }

    fun removeTask(task: Task) {
        viewModelScope.launch {
            dbHelper.deleteTask(task.id)
            loadTasks()
            task.notificationTime?.let { cancelNotification(getApplication(), task) }
        }
    }

    private fun cancelNotification(context: Context, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TaskNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, task.id, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun scheduleNotification(context: Context, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(context, "Permiso para alarmas exactas no concedido.", Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(context, TaskNotificationReceiver::class.java).apply {
            putExtra("TASK_ID", task.id)
            putExtra("TASK_TITLE", task.title)
            putExtra("TASK_DESC", task.description)
            task.imageUri?.let { putExtra("TASK_IMAGE_URI", it.toString()) }
        }
        val pendingIntent = PendingIntent.getBroadcast(context, task.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        task.notificationTime?.let {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, it, pendingIntent)
        }
    }
}
