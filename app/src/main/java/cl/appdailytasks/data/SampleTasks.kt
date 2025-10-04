package cl.appdailytasks.data

import cl.appdailytasks.model.Task
import java.util.Date

// Objeto que contiene datos de ejemplo para desarrollo y pruebas.
object SampleTasks {
    val sampleTasksList = listOf(
        Task(
            id = 1,
            name = "Comprar lista de la compra",
            description = "Leche, pan, huevos y fruta para la semana.",
            date = Date()
        ),
        Task(
            id = 2,
            name = "Estudiar",
            description = "Revisar la documentación oficial.",
            date = Date(),
            imageUrl = "https://developer.android.com/static/images/jetpack/compose-icon.svg"
        ),
        Task(
            id = 3,
            name = "Hacer ejercicio",
            description = "30 minutos de cardio en la trotadora.",
            date = Date()
        )
    )
}
