package cl.appdailytasks.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.appdailytasks.model.Task

// Clase Helper para gestionar la base de datos
class TaskDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 1

        // Tabla y columnas
        private const val TABLE_TASKS = "tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_IMAGE_URI = "image_uri"
        private const val COLUMN_NOTIFICATION_TIME = "notification_time"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_IMAGE_URI TEXT,
                $COLUMN_NOTIFICATION_TIME INTEGER
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    // Insertar una tarea
    fun insertTask(task: Task): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, task.id)
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_IMAGE_URI, task.imageUri)
            put(COLUMN_NOTIFICATION_TIME, task.notificationTime)
        }
        return db.insert(TABLE_TASKS, null, values)
    }

    // Obtener todas las tareas
    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TASKS,
            null,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val title = getString(getColumnIndexOrThrow(COLUMN_TITLE))
                val description = getString(getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val imageUriString = getString(getColumnIndexOrThrow(COLUMN_IMAGE_URI))
                val notificationTime = if (isNull(getColumnIndexOrThrow(COLUMN_NOTIFICATION_TIME))) {
                    null
                } else {
                    getLong(getColumnIndexOrThrow(COLUMN_NOTIFICATION_TIME))
                }

                tasks.add(
                    Task(
                        id = id,
                        title = title,
                        description = description,
                        imageUri = imageUriString,
                        notificationTime = notificationTime
                    )
                )
            }
        }
        cursor.close()
        return tasks
    }

    // Obtener una tarea por ID
    fun getTaskById(id: Int): Task? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TASKS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var task: Task? = null
        if (cursor.moveToFirst()) {
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            val imageUriString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))
            val notificationTime = if (cursor.isNull(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_TIME))) {
                null
            } else {
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_TIME))
            }

            task = Task(
                id = id,
                title = title,
                description = description,
                imageUri = imageUriString,
                notificationTime = notificationTime
            )
        }
        cursor.close()
        return task
    }

    // Actualizar una tarea
    fun updateTask(task: Task): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DESCRIPTION, task.description)
            put(COLUMN_IMAGE_URI, task.imageUri)
            put(COLUMN_NOTIFICATION_TIME, task.notificationTime)
        }
        return db.update(
            TABLE_TASKS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(task.id.toString())
        )
    }

    // Eliminar una tarea
    fun deleteTask(id: Int): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_TASKS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    // Eliminar todas las tareas
    fun deleteAllTasks(): Int {
        val db = writableDatabase
        return db.delete(TABLE_TASKS, null, null)
    }
}
