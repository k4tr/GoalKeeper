package com.example.goalkeeper.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.goalkeeper.R
import com.example.goalkeeper.data.Goal
import com.example.goalkeeper.module.AppBottomBar
import com.example.goalkeeper.module.BottomNavTab
import com.example.goalkeeper.module.CustomCheckbox
import com.example.goalkeeper.ui.theme.DarkGreen
import com.example.goalkeeper.ui.theme.Maroon
import com.example.goalkeeper.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    navController: NavController,
    goalViewModel: GoalViewModel,
    onNavigateToAddGoal: () -> Unit,
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit
) {
    val allGoals by goalViewModel.allGoals.collectAsState()
    val state by goalViewModel.state.collectAsState()
    val generatedGoals by goalViewModel.generatedGoals.collectAsState()
    val scrollState = rememberScrollState()
    Scaffold(
        bottomBar = {
            AppBottomBar(selectedTab = selectedTab, onTabSelected = {
                onTabSelected(it)
                when (it) {
                    BottomNavTab.Home -> navController.navigate("goalsScreen")
                    BottomNavTab.Search -> navController.navigate("searchScreen")
                    BottomNavTab.Check -> { /* Логика для третьей вкладки */ }
                }
            })
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF2F2F6)), // Устанавливаем фон экрана
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {
                // Отображение текущей даты
                Text(
                    text = getCurrentDate(),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                // Название приложения
                Text(
                    text = "GoalKeeper",
                    color = DarkGreen,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Кнопки "Ввести цель" и "Генерация"
                Row (modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ){
                    GoalButton(
                        text = "Ввести цель",
                        iconRes = R.drawable.icon_add,
                        onClick = { navController.navigate("addGoalScreen") }
                    )

                    GoalButtonWithCustomIcon(
                        text = "Генерация",
                        iconRes = R.drawable.component_1, // Используем пользовательскую иконку
                        onClick = { goalViewModel.generateGoals() }
                    )
                }
                Text(
                    text = "Цели на сегодня:",
                    color = DarkGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp) // Отступы между элементами
                        .background(Color.White, shape = RoundedCornerShape(12.dp)) // Белая подложка с закругленными углами
                        .border(
                            width = 1.dp, // Ширина обводки
                            color = Maroon, // Цвет обводки
                            shape = RoundedCornerShape(12.dp) // Форма обводки
                        )
                        .padding(16.dp) // Внутренние отступы внутри элемента
                ){
                    if (generatedGoals.isEmpty()) {
                        Text(text = "Пока что целей нет! Скорее добавьте их :D", color = Color.Gray)
                    } else {
                        LazyColumn {
                            items(generatedGoals) { goal ->
                                val isChecked = goal.isCompleted  // Предположим, что в модели Goal есть поле isCompleted
                                GoalItemWithCheckbox(
                                    goal = goal,
                                    isChecked = isChecked,
                                    onCheckedChange = { checked ->
                                        goalViewModel.onGoalCheckedChange(goal, checked)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Получение текущей даты в нужном формате
fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return sdf.format(Date())
}
@Composable
fun GoalButtonWithCustomIcon(text: String, iconRes: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.small.copy(all = CornerSize(12.dp)),
        modifier = Modifier
            .width(200.dp)
            .height(58.dp)
            .border(
                width = 1.dp, // Ширина обводки
                color = Maroon, // Цвет обводки
                shape = MaterialTheme.shapes.small.copy(all = CornerSize(12.dp)) // Форма обводки
            )
    ) {
        Image(painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = DarkGreen, fontSize = 16.sp, fontWeight = FontWeight.Normal)
    }
}
// Компонент кнопки с иконкой
@Composable
fun GoalButton(text: String, iconRes: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.small.copy(all = CornerSize(12.dp)),
        modifier = Modifier
            .width(184.dp)
            .height(58.dp)
            .border(
                width = 1.dp, // Ширина обводки
                color = Maroon, // Цвет обводки
                shape = MaterialTheme.shapes.small.copy(all = CornerSize(12.dp)) // Форма обводки
            )
    ) {
        Image(painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text, color = DarkGreen, fontSize = 15.sp, fontWeight = FontWeight.Normal)
    }
}
//элементы списка (цели на день)
@Composable
fun GoalItemWithCheckbox(
    goal: Goal,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Чекбокс для отметки выполнения цели
        CustomCheckbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Текст цели, который зачёркивается, если цель выполнена
        Text(
            text = goal.name,
            style = if (isChecked) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
