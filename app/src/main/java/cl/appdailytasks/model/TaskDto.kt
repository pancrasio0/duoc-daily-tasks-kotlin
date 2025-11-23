package cl.appdailytasks.model

import com.google.gson.annotations.SerializedName

data class TaskDto(
    @SerializedName("idTask") val idTask: Long,
    @SerializedName("nombreTask") val nombreTask: String,
    @SerializedName("descripcionTask") val descripcionTask: String,
    @SerializedName("dateTask") val dateTask: String?,
    @SerializedName("imgTask") val imgTask: String?,
    @SerializedName("usuario") val usuario: Usuario?
)
