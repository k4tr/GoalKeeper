package com.example.goalkeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.goalkeeper.data.AppDatabase
import com.example.goalkeeper.di.GoalRepository
import com.example.goalkeeper.di.GoalViewModelFactory
import com.example.goalkeeper.ui.theme.GoalKeeperTheme
import com.example.goalkeeper.view.GoalsScreen
import com.example.goalkeeper.view.AddGoalScreen
import com.example.goalkeeper.viewmodel.GoalViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Получаем экземпляр базы данных и DAO
        val goalDao = AppDatabase.getDatabase(this).goalDao()
        // Создаем экземпляр репозитория
        val repository = GoalRepository(goalDao)
        // Передаем и репозиторий, и goalDao в фабрику ViewModel
        val goalViewModel = ViewModelProvider(this, GoalViewModelFactory(repository, goalDao))
            .get(GoalViewModel::class.java)

        // Устанавливаем начальную точку навигации
        setContent {
            val navController = rememberNavController()

            GoalKeeperTheme {
                NavHost(navController = navController, startDestination = "goalsScreen") {
                    composable("goalsScreen") {
                        GoalsScreen(
                            goalViewModel = goalViewModel,
                            onNavigateToAddGoal = {
                                navController.navigate("addGoalScreen")
                            },
                            navController = navController // Передаем navController
                        )
                    }
                    composable("addGoalScreen") {
                        AddGoalScreen(
                            navController = navController,
                            goalViewModel = goalViewModel,
                            onBackClick = { navController.popBackStack() },
                            onSaveClick = {
                                // Добавьте логику для сохранения цели, если она ещё не реализована
                                // Например:
                                // goalViewModel.addGoal(newGoal)
                                navController.popBackStack() // Возвращаемся на главный экран после сохранения
                            }
                        )
                    }
                }
            }
        }
    }
}
