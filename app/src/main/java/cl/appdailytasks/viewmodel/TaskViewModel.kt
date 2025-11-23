package cl.appdailytasks.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import cl.appdailytasks.data.TaskRepository

// Viewmodel que maneja las funciones para las tareas
class TaskViewModel(application: Application) : AndroidViewModel(application) {
    // CRUD Básico de la aplicación usando taskdatabasehelper importado como dbhelper
    private val dbHelper = TaskDatabaseHelper(application)
    private val repository = TaskRepository(dbHelper)
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    
    private var currentUserId: String? = null

    init {
        // Don't load tasks initially, wait for user ID
    }
    
    fun setUserId(id: String?) {
        currentUserId = id
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            currentUserId?.let { userId ->
                _tasks.value = repository.getTasks(userId)
            } ?: run {
                _tasks.value = emptyList() // Or local tasks if we want offline support without login
            }
        }
    }

    fun getTask(id: Long): Task? {
        return _tasks.value.find { it.id == id }
    }

    fun addTask(title: String, description: String, imageUri: String?, notificationTime: Long?) {
        viewModelScope.launch {
            currentUserId?.let { userId ->
                val newTask = Task(title = title, description = description, imageUri = imageUri, notificationTime = notificationTime, idGoogle = userId)
                val createdTask = repository.addTask(newTask, userId)
                loadTasks()
                if (notificationTime != null) {
                    scheduleNotification(getApplication(), createdTask)
                }
            }
        }
    }

    fun updateTask(updatedTask: Task) {
        viewModelScope.launch {
            currentUserId?.let { userId ->
                val oldTask = getTask(updatedTask.id)
                repository.updateTask(updatedTask, userId)
                loadTasks()

                if (oldTask?.notificationTime != updatedTask.notificationTime) {
                    oldTask?.notificationTime?.let { cancelNotification(getApplication(), oldTask) }
                }
                updatedTask.notificationTime?.let { scheduleNotification(getApplication(), updatedTask) }
            }
        }
    }

    fun removeTask(task: Task) {
        viewModelScope.launch {
            currentUserId?.let { userId ->
                repository.deleteTask(task, userId)
                loadTasks()
                task.notificationTime?.let { cancelNotification(getApplication(), task) }
            }
        }
    }

    private fun cancelNotification(context: Context, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TaskNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, task.id.toInt(), intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
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
            task.imageUri?.let { putExtra("TASK_IMAGE_URI", it) }
        }
        val pendingIntent = PendingIntent.getBroadcast(context, task.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        task.notificationTime?.let {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, it, pendingIntent)
        }
    }
}
