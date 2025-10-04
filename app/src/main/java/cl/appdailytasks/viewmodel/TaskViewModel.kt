package cl.appdailytasks.viewmodel

import androidx.lifecycle.ViewModel
import cl.appdailytasks.data.SampleTasks
import cl.appdailytasks.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TaskViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())

    val tasks: StateFlow<List<Task>> = _tasks

    init {
        loadTasks()
    }

    private fun loadTasks() {
        _tasks.value = SampleTasks.sampleTasksList
    }
}
