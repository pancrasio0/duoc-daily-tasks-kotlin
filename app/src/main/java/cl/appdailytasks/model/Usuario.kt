package cl.appdailytasks.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    val id: Long = 0,
    val idGoogle: String,
    @SerializedName("nombreCompleto")
    val nombre: String,
    @SerializedName("correo")
    val email: String,
    @SerializedName("imgUsuario")
    val imgUsuario: String? = null
)
