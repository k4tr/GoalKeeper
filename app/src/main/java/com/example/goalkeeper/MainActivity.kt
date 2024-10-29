package com.example.goalkeeper

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.goalkeeper.data.AppDatabase
import com.example.goalkeeper.repository.GoalRepository
import com.example.goalkeeper.repository.GoalViewModelFactory
import com.example.goalkeeper.repository.TimeRepository
import com.example.goalkeeper.repository.TimeViewModelFactory
import com.example.goalkeeper.module.AppBottomBar
import com.example.goalkeeper.module.BottomNavTab
import com.example.goalkeeper.ui.theme.GoalKeeperTheme
import com.example.goalkeeper.view.GoalsScreen
import com.example.goalkeeper.view.AddGoalScreen
import com.example.goalkeeper.view.SearchScreen
import com.example.goalkeeper.view.SettingsScreen
import com.example.goalkeeper.viewmodel.GoalViewModel
import com.example.goalkeeper.viewmodel.TimeViewModel


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val timeDao = AppDatabase.getDatabase(this).timeDao()
        val repositoryTime = TimeRepository(timeDao)
        val goalDao = AppDatabase.getDatabase(this).goalDao()
        val repository = GoalRepository(goalDao)

        val goalViewModel = ViewModelProvider(
            this,
            GoalViewModelFactory(repository, goalDao, timeDao, repositoryTime)
        ).get(GoalViewModel::class.java)
        val timeViewModel = ViewModelProvider(this, TimeViewModelFactory(repositoryTime))
            .get(TimeViewModel::class.java)

        //UI
        setContent {
            val navController = rememberNavController()

            // Состояние для отслеживания выбранной вкладки
            val (selectedTab, setSelectedTab) = remember { mutableStateOf(BottomNavTab.Home) }

            GoalKeeperTheme {
                Scaffold(
                    bottomBar = {
                        // Передаем правильные параметры в AppBottomBar
                        AppBottomBar(
                            selectedTab = selectedTab,
                            onTabSelected = { tab ->
                                setSelectedTab(tab)
                                when (tab) {
                                    BottomNavTab.Home -> navController.navigate("goalsScreen")
                                    BottomNavTab.Search -> navController.navigate("searchScreen")
                                    BottomNavTab.Add -> navController.navigate("addGoalScreen")
                                }
                            }
                        )
                    }
                ) {
                    NavHost(navController = navController, startDestination = "goalsScreen") {
                        composable("goalsScreen") {
                            GoalsScreen(
                                goalViewModel = goalViewModel,
                                onNavigateToAddGoal = {
                                    navController.navigate("settingScreen")
                                },
                                navController = navController,
                                selectedTab = selectedTab, // Передаем состояние вкладки
                                onTabSelected = setSelectedTab // Передаем функцию для изменения состояния вкладки
                            )
                        }
                        composable("searchScreen") {
                            SearchScreen(
                                navController = navController,
                                goalViewModel = goalViewModel,
                                selectedTab = selectedTab, // Передаем состояние вкладки
                                onTabSelected = setSelectedTab, // Передаем функцию для изменения состояния вкладки
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable("addGoalScreen") {
                            AddGoalScreen(
                                navController = navController,
                                goalViewModel = goalViewModel,
                                selectedTab = selectedTab, // Передаем состояние вкладки
                                onTabSelected = setSelectedTab, // Передаем функцию для изменения состояния вкладки
                                onBackClick = { navController.popBackStack() },
                                onSaveClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("settingsScreen") {
                            SettingsScreen(
                                navController = navController,
                                selectedTab = selectedTab,
                                timeViewModel = timeViewModel,

                                onTabSelected = setSelectedTab,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}


