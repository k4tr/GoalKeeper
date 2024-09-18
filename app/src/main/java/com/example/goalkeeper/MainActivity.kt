package com.example.goalkeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.goalkeeper.data.AppDatabase
import com.example.goalkeeper.di.GoalRepository
import com.example.goalkeeper.di.GoalViewModelFactory
import com.example.goalkeeper.view.GoalsScreen
import com.example.goalkeeper.viewmodel.GoalViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = GoalRepository(AppDatabase.getDatabase(this).goalDao())
        val goalViewModel = ViewModelProvider(this, GoalViewModelFactory(repository))
            .get(GoalViewModel::class.java)

        setContent {
            GoalsScreen(goalViewModel = goalViewModel, onNavigateToAddGoal = { /* навигация */ })
        }
    }
}
