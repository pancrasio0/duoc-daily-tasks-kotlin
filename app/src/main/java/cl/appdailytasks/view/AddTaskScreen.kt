package cl.appdailytasks.view


import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import cl.appdailytasks.viewmodel.TaskViewModel
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    taskViewModel: TaskViewModel,
    onTaskAdded: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateTime by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    val isTitleValid = title.length > 3
    val isDescriptionValid = description.isEmpty() || description.length > 3




    fun isValidDateTime(dateTimeStr: String): Boolean {
        if (dateTimeStr.isEmpty()) return true

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return try {
            val inputDateTime = LocalDateTime.parse(dateTimeStr, formatter)
            !inputDateTime.isBefore(LocalDateTime.now())
        } catch (e: DateTimeParseException) {
            false
        }
    }

    val isDateValid = isValidDateTime(dateTime)
    val isButtonEnabled = isTitleValid && isDescriptionValid && isDateValid

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)


    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val now = Calendar.getInstance()
            now.add(Calendar.MINUTE, 1)
            val currentHour = now.get(Calendar.HOUR_OF_DAY)
            val currentMinute = now.get(Calendar.MINUTE)

            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                val selectedDateTime = LocalDateTime.of(
                    selectedYear,
                    selectedMonth + 1,
                    selectedDay,
                    selectedHour,
                    selectedMinute
                )
                val formattedDateTime = selectedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                dateTime = formattedDateTime
                // Validar que la fecha y hora seleccionada no sea anterior a la actual.
                dateError = if (selectedDateTime.isBefore(LocalDateTime.now())) {
                    "La fecha no puede ser del pasado"
                } else {
                    null
                }
            }

            TimePickerDialog(
                context,
                timeSetListener,
                currentHour,
                currentMinute,
                true
            ).show()

        },
        year, month, day
    )

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            // Cuando el usuario elige una imagen de la galería,
            // la asignamos directamente a nuestro estado principal.
            imageUri = uri
            imageUrl = uri?.toString() ?: ""
        }
    )


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { isSuccess ->
            if (isSuccess) {
                imageUri = tempImageUri
                imageUrl = tempImageUri.toString()
            }
        }
    )



    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                createImageUri(context)?.let { uri ->
                    tempImageUri = uri
                    cameraLauncher.launch(uri)
                }
            } else {
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Nueva Tarea") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = if (it.length <= 3) "El título debe tener más de 3 letras." else null
                },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    descriptionError = if (it.isNotEmpty() && it.length <= 3) "La descripción debe tener más de 3 letras." else null
                },
                label = { Text("Descripción (Opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                isError = descriptionError != null,
                supportingText = { descriptionError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            OutlinedTextField(
                value = dateTime,
                onValueChange = {},
                label = { Text("Fecha y Hora (Opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clickable { datePickerDialog.show() },
                enabled = false,
                isError = dateError != null,
                supportingText = { dateError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUri),
                    contentDescription = "Vista previa de la imagen",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround, // Espacio entre botones
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tu botón original para la cámara, ahora dentro de un Row
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text(if (imageUri == null) "Tomar Foto" else "Tomar Otra")
                }

                // El nuevo botón para la galería
                Button(onClick = { galleryLauncher.launch("image/*") }) {
                    Text("Galería")
                }
            }

            // Botón condicional para quitar la imagen
            if (imageUri != null) {
                Button(
                    onClick = {
                        imageUri = null
                        imageUrl = "" // Limpia ambos estados
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Quitar Imagen")
                }
            }



            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {

                    val finalDescription = if (descriptionError == null) description else ""


                    val finalImageUri: Uri? = imageUri


                    val notificationTimestamp: Long? = if (dateTime.isNotEmpty() && dateError == null) {
                        try {

                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            val localDateTime = LocalDateTime.parse(dateTime, formatter)


                            localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                        } catch (e: DateTimeParseException) {

                            null
                        }
                    } else {

                        null
                    }


                    taskViewModel.addTask(
                        title = title,
                        description = finalDescription,
                        imageUri = finalImageUri,notificationTime = notificationTimestamp, // Ahora la línea es correcta
                        context = context
                    )




                    onTaskAdded()
                },
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth() // Que ocupe todo el ancho para que sea más fácil de pulsar.
                    .padding(top = 16.dp)
            ) {
                Text("Guardar Tarea")
            }
        }
    }
}
private fun createImageUri(context: Context): Uri? {
    val imageFolder = File(context.externalCacheDir, "camera_images")
    if (!imageFolder.exists()) {
        imageFolder.mkdirs()
    }
    val file = File.createTempFile(
        "JPEG_${System.currentTimeMillis()}_",
        ".jpg",
        imageFolder
    )
    return FileProvider.getUriForFile(
        context,
        "cl.appdailytasks.provider",
        file
    )
}


