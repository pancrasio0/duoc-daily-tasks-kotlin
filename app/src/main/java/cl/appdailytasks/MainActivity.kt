package cl.appdailytasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.appdailytasks.ui.theme.AppdailytasksTheme
import cl.appdailytasks.view.AddTaskScreen
import cl.appdailytasks.view.TaskScreen
import cl.appdailytasks.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppdailytasksTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val taskViewModel: TaskViewModel = viewModel()

    NavHost(navController = navController, startDestination = "taskScreen") {
        composable("taskScreen") {
            TaskScreen(
                taskViewModel = taskViewModel,
                onAddTask = { navController.navigate("addTask") }
            )
        }
        composable("addTask") {
            AddTaskScreen(
                taskViewModel = taskViewModel,
                onTaskAdded = { navController.popBackStack() }
            )
        }
    }
}
