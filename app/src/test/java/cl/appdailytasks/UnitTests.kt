package cl.appdailytasks

import cl.appdailytasks.model.TaskDto
import cl.appdailytasks.model.Usuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UnitTests {

    @Test
    fun testUsuarioCreation() {
        val usuario = Usuario(
            id = 1,
            idGoogle = "google123",
            nombre = "Juan Perez",
            email = "juan@example.com",
            imgUsuario = "url_to_image"
        )

        assertEquals(1L, usuario.id)
        assertEquals("google123", usuario.idGoogle)
        assertEquals("Juan Perez", usuario.nombre)
        assertEquals("juan@example.com", usuario.email)
        assertEquals("url_to_image", usuario.imgUsuario)
    }

    @Test
    fun testUsuarioDefaultValues() {
        val usuario = Usuario(
            idGoogle = "google456",
            nombre = "Maria Lopez",
            email = "maria@example.com"
        )

        assertEquals(0L, usuario.id)
        assertNull(usuario.imgUsuario)
    }

    @Test
    fun testTaskDtoCreation() {
        val usuario = Usuario(idGoogle = "g1", nombre = "U1", email = "e1")
        val task = TaskDto(
            idTask = 100,
            nombreTask = "Comprar leche",
            descripcionTask = "Ir al supermercado",
            dateTask = "2023-10-27",
            imgTask = null,
            usuario = usuario
        )

        assertEquals(100L, task.idTask)
        assertEquals("Comprar leche", task.nombreTask)
        assertEquals("Ir al supermercado", task.descripcionTask)
        assertEquals(usuario, task.usuario)
    }

    @Test
    fun testUsuarioEquality() {
        val usuario1 = Usuario(
            id = 1,
            idGoogle = "g1",
            nombre = "Test",
            email = "test@test.com"
        )
        val usuario2 = Usuario(
            id = 1,
            idGoogle = "g1",
            nombre = "Test",
            email = "test@test.com"
        )
        val usuario3 = Usuario(
            id = 2,
            idGoogle = "g2",
            nombre = "Test2",
            email = "test2@test.com"
        )

        assertEquals(usuario1, usuario2)
        assertNotEquals(usuario1, usuario3)
    }

    @Test
    fun testTaskDtoEquality() {
        val task1 = TaskDto(
            idTask = 1,
            nombreTask = "Task 1",
            descripcionTask = "Desc 1",
            dateTask = "2023-01-01",
            imgTask = "img1",
            usuario = null
        )
        val task2 = TaskDto(
            idTask = 1,
            nombreTask = "Task 1",
            descripcionTask = "Desc 1",
            dateTask = "2023-01-01",
            imgTask = "img1",
            usuario = null
        )

        assertEquals(task1, task2)
    }
}
