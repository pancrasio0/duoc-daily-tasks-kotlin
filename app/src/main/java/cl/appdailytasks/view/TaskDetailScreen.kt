package cl.appdailytasks.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import cl.appdailytasks.viewmodel.TaskViewModel
import coil.compose.AsyncImage
import java.io.File
import java.util.Calendar

private fun copyUriContentToInternalStorage(context: Context, sourceUri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
        val imageFolder = File(context.filesDir, "task_images")
        if (!imageFolder.exists()) {
            imageFolder.mkdirs()
        }
        val file = File(imageFolder, "IMG_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { outputStream ->
            inputStream.use { it.copyTo(outputStream) }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskViewModel: TaskViewModel,
    taskId: Int?,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val task = taskId?.let { taskViewModel.getTask(it) }

    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var imageUri by remember { mutableStateOf(task?.imageUri) } // This is now a file path string or null
    var notificationTime by remember { mutableStateOf(task?.notificationTime) }

    var showImageSourceDialog by remember { mutableStateOf(false) }

    val isFormValid = title.length > 3 && (description.length > 3 || description.isEmpty())

    var tempCameraPhotoPath by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                imageUri = copyUriContentToInternalStorage(context, it)
            }
        }
    )

    fun createCameraImageFile(): File {
        val imageFolder = File(context.filesDir, "task_images")
        if (!imageFolder.exists()) {
            imageFolder.mkdirs()
        }
        return File.createTempFile("IMG_", ".jpg", imageFolder)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                imageUri = tempCameraPhotoPath
            }
        }
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                val photoFile = createCameraImageFile()
                tempCameraPhotoPath = photoFile.absolutePath
                val photoUri = FileProvider.getUriForFile(
                    context,
                    "cl.appdailytasks.provider",
                    photoFile
                )
                cameraLauncher.launch(photoUri)
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (task == null) "Nueva Tarea" else "Editar Tarea") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título (más de 3 letras)") },
                modifier = Modifier.fillMaxWidth(),
                isError = title.isNotEmpty() && title.length <= 3,
                supportingText = { if (title.isNotEmpty() && title.length <= 3) Text("El título es muy corto") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (más de 3 letras)") },
                modifier = Modifier.fillMaxWidth(),
                isError = description.isNotEmpty() && description.length <= 3,
                supportingText = { if (description.isNotEmpty() && description.length <= 3) Text("La descripción es muy corta") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            imageUri?.let { path ->
                AsyncImage(
                    model = File(path),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { imageUri = null }) {
                    Text("Quitar Imagen")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { showImageSourceDialog = true }) {
                Text("Seleccionar Imagen")
            }

            Spacer(modifier = Modifier.height(16.dp))
            DateTimePicker(context, notificationTime) { newTime ->
                notificationTime = newTime
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    if (isFormValid) {
                        val finalDescription = if(description.isEmpty()) " " else description
                        if (task == null) {
                            taskViewModel.addTask(title, finalDescription, imageUri, notificationTime)
                        } else {
                            val updatedTask = task.copy(
                                title = title,
                                description = finalDescription,
                                imageUri = imageUri,
                                notificationTime = notificationTime
                            )
                            taskViewModel.updateTask(updatedTask)
                        }
                        onNavigateBack()
                    }
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (task == null) "Crear Tarea" else "Guardar Cambios")
            }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Seleccionar fuente de imagen") },
            text = { Text("¿De dónde quieres obtener la imagen?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImageSourceDialog = false
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) -> {
                                val photoFile = createCameraImageFile()
                                tempCameraPhotoPath = photoFile.absolutePath
                                val photoUri = FileProvider.getUriForFile(
                                    context,
                                    "cl.appdailytasks.provider",
                                    photoFile
                                )
                                cameraLauncher.launch(photoUri)
                            }
                            else -> {
                                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    }
                ) { Text("Cámara") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImageSourceDialog = false
                        galleryLauncher.launch("image/*")
                    }
                ) { Text("Galería") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(context: Context, initialTime: Long?, onTimeSelected: (Long?) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance()
    initialTime?.let { calendar.timeInMillis = it }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialTime)
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        showTimePicker = true
                        datePickerState.selectedDateMillis?.let { calendar.timeInMillis = it }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Seleccionar Hora") },
            text = {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                        calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        calendar.set(Calendar.MINUTE, timePickerState.minute)
                        onTimeSelected(calendar.timeInMillis)
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = { showDatePicker = true }) {
            Text(if (initialTime == null) "Definir Notificación" else "Cambiar Notificación")
        }
        Spacer(modifier = Modifier.width(16.dp))
        if (initialTime != null) {
            Button(onClick = { onTimeSelected(null) }) {
                Text("Quitar")
            }
        }
    }
}
