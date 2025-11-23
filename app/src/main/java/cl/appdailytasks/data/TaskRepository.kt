package cl.appdailytasks.data

import android.util.Log
import cl.appdailytasks.model.Task
import cl.appdailytasks.model.TaskRequest
import cl.appdailytasks.model.TaskDto
import cl.appdailytasks.model.TaskDatabaseHelper
import cl.appdailytasks.network.BackendApiService
import cl.appdailytasks.network.RetrofitClient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TaskRepository(private val dbHelper: TaskDatabaseHelper) {
    private val apiService: BackendApiService = RetrofitClient.instance
    private val TAG = "TaskRepository"

    suspend fun getTasks(idGoogle: String): List<Task> {
        return try {
            Log.d(TAG, "Obteniendo tareas del usuario: $idGoogle")
            val taskDtos = apiService.getTasksByUsuario(idGoogle)
            Log.d(TAG, "Se obtuvieron ${taskDtos.size} tareas del backend")
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US) // Assuming date only based on log "2025-11-22"
            // If the API returns full ISO, use the previous format. Log showed "2025-11-22"
            
            taskDtos.map { dto ->
                val notificationTime = dto.dateTask?.let { 
                    try {
                        dateFormat.parse(it)?.time
                    } catch (e: Exception) {
                        null
                    }
                }
                Task(
                    id = dto.idTask,
                    title = dto.nombreTask,
                    description = dto.descripcionTask,
                    imageUri = dto.imgTask,
                    notificationTime = notificationTime,
                    idGoogle = dto.usuario?.idGoogle
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener tareas del backend: ${e.message}", e)
            // Fallback to local DB, filtering by idGoogle
            val localTasks = dbHelper.getAllTasks().filter { it.idGoogle == idGoogle }
            Log.d(TAG, "Usando ${localTasks.size} tareas de la base de datos local")
            localTasks
        }
    }

    suspend fun addTask(task: Task, idGoogle: String): Task {
        return try {
            Log.d(TAG, "Creando tarea para usuario $idGoogle: ${task.title}")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val dateTask = if (task.notificationTime != null) {
                dateFormat.format(Date(task.notificationTime))
            } else {
                dateFormat.format(Date())
            }

            val taskRequest = TaskRequest(
                nombreTask = task.title,
                descripcionTask = task.description,
                dateTask = dateTask ?: "", // Send empty string if null
                imgTask = task.imageUri ?: "" // Send empty string if null
            )

            val createdTaskDto = apiService.createTask(idGoogle, taskRequest)
            Log.d(TAG, "Tarea creada en backend con ID: ${createdTaskDto.idTask}")
            
            // Map DTO back to Task
            val responseDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val notificationTime = createdTaskDto.dateTask?.let {
                 try {
                     responseDateFormat.parse(it)?.time
                 } catch (e: Exception) {
                     null
                 }
            }

            val createdTask = Task(
                id = createdTaskDto.idTask,
                title = createdTaskDto.nombreTask,
                description = createdTaskDto.descripcionTask,
                imageUri = createdTaskDto.imgTask,
                notificationTime = notificationTime,
                idGoogle = createdTaskDto.usuario?.idGoogle
            )

            dbHelper.insertTask(createdTask) // Save to local DB
            createdTask
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear tarea en backend: ${e.message}", e)
            // If API fails, save locally
            val taskWithUser = task.copy(idGoogle = idGoogle)
            val id = dbHelper.insertTask(taskWithUser)
            Log.d(TAG, "Tarea guardada localmente con ID: $id")
            taskWithUser.copy(id = id)
        }
    }

    suspend fun updateTask(task: Task, idGoogle: String) {
        try {
            Log.d(TAG, "Actualizando tarea ${task.id} para usuario $idGoogle")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val dateTask = if (task.notificationTime != null) {
                dateFormat.format(Date(task.notificationTime))
            } else {
                dateFormat.format(Date())
            }

            val taskRequest = TaskRequest(
                nombreTask = task.title,
                descripcionTask = task.description,
                dateTask = dateTask ?: "",
                imgTask = task.imageUri ?: ""
            )

            apiService.updateTask(task.id, idGoogle, taskRequest)
            dbHelper.updateTask(task)
            Log.d(TAG, "Tarea actualizada exitosamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar tarea en backend: ${e.message}", e)
            dbHelper.updateTask(task)
            Log.d(TAG, "Tarea actualizada solo localmente")
        }
    }

    suspend fun deleteTask(task: Task, idGoogle: String) {
        try {
            Log.d(TAG, "Eliminando tarea ${task.id} para usuario $idGoogle")
            apiService.deleteTask(task.id, idGoogle)
            dbHelper.deleteTask(task.id)
            Log.d(TAG, "Tarea eliminada exitosamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar tarea en backend: ${e.message}", e)
            dbHelper.deleteTask(task.id)
            Log.d(TAG, "Tarea eliminada solo localmente")
        }
    }
}
