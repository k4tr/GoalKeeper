package com.example.goalkeeper.module

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppBottomBar(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit
) {

    BottomAppBar(
        containerColor = Color(0xFFF9F9F9) // Цвет BottomAppBar
    ) {
        // Вкладка "Дом"
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Главная",
                    tint = if (selectedTab == BottomNavTab.Home) Color(0xFF778FD2) else Color(0xFF3D5220) // Меняем цвет иконки
                )
            },
            label = {
                Text(
                    text = "Главная",
                    color = if (selectedTab == BottomNavTab.Home) Color(0xFF778FD2) else Color(0xFF3D5220) // Меняем цвет текста
                )
            },
            selected = selectedTab == BottomNavTab.Home,
            onClick = { onTabSelected(BottomNavTab.Home) }

        )

        // Вкладка "Поиск"
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Поиск",
                    tint = if (selectedTab == BottomNavTab.Search) Color(0xFF778FD2) else Color(0xFF3D5220) // Меняем цвет иконки
                )
            },
            label = {
                Text(
                    text = "Поиск",
                    color = if (selectedTab == BottomNavTab.Search) Color(0xFF778FD2) else Color(0xFF3D5220) // Меняем цвет текста
                )
            },
            selected = selectedTab == BottomNavTab.Search,
            onClick = { onTabSelected(BottomNavTab.Search) }
        )

        // Вкладка "Галочка"
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Галочка",
                    tint = if (selectedTab == BottomNavTab.Check) Color(0xFF778FD2) else Color(0xFF3D5220) // Меняем цвет иконки
                )
            },
            label = {
                Text(
                    text = "Галочка",
                    color = if (selectedTab == BottomNavTab.Check) Color(0xFF778FD2) else Color(0xFF3D5220) // Меняем цвет текста
                )
            },
            selected = selectedTab == BottomNavTab.Check,
            onClick = { onTabSelected(BottomNavTab.Check) }
        )
    }
}

// Определяем вкладки для нижней панели
enum class BottomNavTab {
    Home, Search, Check
}