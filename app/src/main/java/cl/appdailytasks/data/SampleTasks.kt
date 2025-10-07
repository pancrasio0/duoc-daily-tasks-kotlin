package cl.appdailytasks.data

import cl.appdailytasks.model.Task
import java.util.Date

object SampleTasks {
    val sampleTasksList: List<Task> = listOf(
        Task(
            id = 1,
            name = "Comprar lista de la compra",
            description = "Leche, pan, huevos y fruta para la semana.",
            date = Date(),
            imageUrl = "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
        ),
        Task(
            id = 2,
            name = "Estudiar Jetpack Compose",
            description = "Revisar la documentación oficial y hacer un ejemplo.",
            date = Date(),
            imageUrl = "https://developer.android.com/static/images/jetpack/compose-icon.svg"
        ),
        Task(
            id = 3,
            name = "Hacer ejercicio",
            description = "30 minutos de cardio en la trotadora.",
            date = Date(),
            imageUrl = "https://images.pexels.com/photos/4753997/pexels-photo-4753997.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
        ),
        Task(
            id = 4,
            name = "Hacer ejercicio 2",
            description = null,
            date = Date(),
            imageUrl = "https://images.pexels.com/photos/4753997/pexels-photo-4753997.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
        )

    )
}