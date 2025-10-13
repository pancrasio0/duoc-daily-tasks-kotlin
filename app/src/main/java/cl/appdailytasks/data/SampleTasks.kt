package cl.appdailytasks.data

import androidx.core.net.toUri
import cl.appdailytasks.model.Task
import java.util.Date

object SampleTasks {
    val sampleTasksList: List<Task> = listOf(
        Task(
            id = 1,
            title = "Comprar lista de la compra",
            description = "Leche, pan, huevos y fruta para la semana.",
            imageUri = "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500".toUri()
        ),
        Task(
            id = 2,
            title = "Realizar avances en la documentación",
            description = "Revisar la documentación oficial y Realizar avances.",
            imageUri = "https://images.pexels.com/photos/5926382/pexels-photo-5926382.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1".toUri()
        ),
        Task(
            id = 3,
            title = "Hacer ejercicio",
            description = "30 minutos de cardio en la trotadora.",
            imageUri = "https://images.pexels.com/photos/4753997/pexels-photo-4753997.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500".toUri()
        ),
        Task(
            id = 4,
            title = "Correr en el simulador",
            description = "Hacer practicas, Qualy y Carrera como tal.",
            imageUri = "https://images.pexels.com/photos/1545743/pexels-photo-1545743.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1".toUri()
        ),
        Task(
            id = 5,
            title = "Correr en el simulador",
            description = "Hacer practicas, Qualy y Carrera como tal.",
            imageUri = "https://images.pexels.com/photos/1545743/pexels-photo-1545743.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1".toUri()
        )

    )

}