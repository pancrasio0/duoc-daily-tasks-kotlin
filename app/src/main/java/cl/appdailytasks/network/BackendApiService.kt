package cl.appdailytasks.network

import cl.appdailytasks.model.Task
import cl.appdailytasks.model.Usuario
import cl.appdailytasks.model.TaskRequest
import cl.appdailytasks.model.TaskDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BackendApiService {

    // Tasks
    @GET("tasks")
    suspend fun getTasks(): List<TaskDto>

    @GET("tasks/{id}")
    suspend fun getTask(@Path("id") id: Long): TaskDto

    @GET("tasks/usuario/{idGoogle}")
    suspend fun getTasksByUsuario(@Path("idGoogle") idGoogle: String): List<TaskDto>

    @POST("tasks/usuario/{idGoogle}")
    suspend fun createTask(@Path("idGoogle") idGoogle: String, @Body task: TaskRequest): TaskDto

    @PUT("tasks/{id}/usuario/{idGoogle}")
    suspend fun updateTask(@Path("id") id: Long, @Path("idGoogle") idGoogle: String, @Body task: TaskRequest): TaskDto

    @DELETE("tasks/{id}/usuario/{idGoogle}")
    suspend fun deleteTask(@Path("id") id: Long, @Path("idGoogle") idGoogle: String): Response<Void>

    // Usuarios
    @GET("usuarios")
    suspend fun getUsuarios(): List<Usuario>

    @GET("usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Long): Usuario

    @GET("usuarios/google/{idGoogle}")
    suspend fun getUsuarioByGoogleId(@Path("idGoogle") idGoogle: String): Usuario

    @POST("usuarios")
    suspend fun createUsuario(@Body usuario: Usuario): Usuario

    @PUT("usuarios/{id}")
    suspend fun updateUsuario(@Path("id") id: Long, @Body usuario: Usuario): Usuario

    @DELETE("usuarios/{id}")
    suspend fun deleteUsuario(@Path("id") id: Long): Response<Void>
}
