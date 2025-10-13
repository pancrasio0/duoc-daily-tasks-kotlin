package cl.appdailytasks.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.appdailytasks.view.TaskNotificationReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import cl.appdailytasks.model.Task
import cl.appdailytasks.data.SampleTasks

class TaskViewModel : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        if (_tasks.value.isEmpty()) {
            _tasks.value = SampleTasks.sampleTasksList
        }
    }

    fun getTask(id: Int): Task? {
        return _tasks.value.find { it.id == id }
    }

    fun addTask(title: String, description: String, imageUri: Uri?, notificationTime: Long?, context: Context) {
        viewModelScope.launch {
            val newTask = Task(title = title, description = description, imageUri = imageUri, notificationTime = notificationTime)
            _tasks.update { it + newTask }
            notificationTime?.let { scheduleNotification(context, newTask) }
        }
    }

    fun updateTask(updatedTask: Task, context: Context) {
        viewModelScope.launch {
            val oldTask = getTask(updatedTask.id)
            _tasks.update { tasks ->
                tasks.map { if (it.id == updatedTask.id) updatedTask else it }
            }
            if (oldTask?.notificationTime != updatedTask.notificationTime) {
                oldTask?.notificationTime?.let { cancelNotification(context, oldTask) }
            }
            updatedTask.notificationTime?.let { scheduleNotification(context, updatedTask) }
        }
    }

    fun removeTask(task: Task, context: Context) {
        viewModelScope.launch {
            _tasks.update { tasks -> tasks.filterNot { it.id == task.id } }
            task.notificationTime?.let { cancelNotification(context, task) }
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