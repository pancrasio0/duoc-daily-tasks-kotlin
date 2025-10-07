package cl.appdailytasks.viewmodel

import androidx.lifecycle.ViewModel
import cl.appdailytasks.data.SampleTasks
import cl.appdailytasks.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())

    val tasks: StateFlow<List<Task>> = _tasks

    init {
        loadTasks()
    }

    private fun loadTasks() {
        _tasks.value = SampleTasks.sampleTasksList
    }

    fun addTask(title: String, description: String, dateStr: String, imageUrl: String) {
        val date = try {
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(dateStr)
        } catch (e: Exception) {
            null
        }

        val newTask = Task(
            id = (_tasks.value.maxOfOrNull { it.id } ?: 0) + 1,
            name = title,
            description = description,
            date = date,
            imageUrl = imageUrl.takeIf { it.isNotBlank() }
        )
        _tasks.value = _tasks.value + newTask
    }
}
