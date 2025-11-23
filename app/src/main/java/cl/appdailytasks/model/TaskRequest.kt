package cl.appdailytasks.model

import com.google.gson.annotations.SerializedName

data class TaskRequest(
    @SerializedName("nombreTask") val nombreTask: String,
    @SerializedName("descripcionTask") val descripcionTask: String,
    @SerializedName("dateTask") val dateTask: String?,
    @SerializedName("imgTask") val imgTask: String?
)
