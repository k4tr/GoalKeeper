package com.example.goalkeeper.view

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.goalkeeper.module.AppBottomBar
import com.example.goalkeeper.module.BottomNavTab
import com.example.goalkeeper.module.CircularTimeDistribution
import com.example.goalkeeper.ui.theme.DarkGreen
import com.example.goalkeeper.ui.theme.Maroon

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    selectedTab: BottomNavTab,
    onBackClick: () -> Unit,
    onTabSelected: (BottomNavTab) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Параметры генерации") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF2F2F6),  // Устанавливаем цвет фона TopAppBar
                titleContentColor = DarkGreen,        // Цвет текста (например, как в других экранах)
                navigationIconContentColor = DarkGreen)
            )
            Divider(
                color = Maroon,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 64.dp)
            )
        },
        bottomBar = {
            AppBottomBar(selectedTab = selectedTab, onTabSelected = {
                onTabSelected(it)
                when (it) {
                    BottomNavTab.Home -> navController.navigate("goalsScreen")
                    BottomNavTab.Search -> navController.navigate("searchScreen")
                    BottomNavTab.Add -> navController.navigate("addGoalScreen")
                }
            })
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F2F6)), // Устанавливаем фон экрана
            contentAlignment = Alignment.TopStart
        ) {
            CircularTimeDistribution()
        }
    }
}

// Preview для демонстрации экрана настроек
@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    val navController = rememberNavController()
    SettingsScreen(
        navController = navController,
        selectedTab = BottomNavTab.Home,
        onBackClick = {},
        onTabSelected = {}
    )
}