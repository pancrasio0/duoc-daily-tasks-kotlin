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
            name = "Realizar avances en la documentación",description = "Revisar la documentación oficial y Realizar avances.",
            date = Date(),
            imageUrl = "https://images.pexels.com/photos/5926382/pexels-photo-5926382.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
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
            name = "Correr en el simulador",
            description = "Hacer practicas, Qualy y Carrera como tal. ",
            date = Date(),
            imageUrl = "https://images.pexels.com/photos/1545743/pexels-photo-1545743.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"




        )



    )
}