package com.example.goalkeeper.view

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.ui.unit.dp
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.sp

import androidx.navigation.NavController

import com.example.goalkeeper.module.AppBottomBar
import com.example.goalkeeper.module.BottomNavTab
import com.example.goalkeeper.module.CustomCheckbox
import com.example.goalkeeper.ui.theme.DarkGreen
import com.example.goalkeeper.ui.theme.Maroon
import com.example.goalkeeper.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.example.goalkeeper.R
import com.example.goalkeeper.data.model.Goal
import com.example.goalkeeper.module.ActivityCalendar
import com.example.goalkeeper.module.CustomToast
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
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
    val scrollState = rememberScrollState() // Создаем состояние прокрутки
    val generatedGoals by goalViewModel.generatedGoals.collectAsState(initial = emptyList())
    val showToast by goalViewModel.showToast.collectAsState()
    val toastMessage by goalViewModel.toastMessage.collectAsState()
    val context = LocalContext.current // Получаем доступ к контексту для Toast
    val activeDays by goalViewModel.activeDays.collectAsState() // теперь это поток данных
    val activeDaysList: List<LocalDate> = activeDays.mapNotNull { millis ->
        try {
            Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        } catch (e: DateTimeException) {
            null
        }
    }

    Scaffold(
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F2F6)), // Устанавливаем фон экрана
            contentAlignment = Alignment.TopStart
        ) {
            // Прокручиваем весь экран
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()) // Прокрутка всего экрана
                    .padding(22.dp)
                    .padding(paddingValues)
            ) {
                // Отображение текущей даты
                Text(
                    text = getCurrentDate(),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Row {
                    // Название приложения
                    Text(
                        text = "GoalKeeper",
                        color = DarkGreen,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Кнопки "Параметры" и "Генерация"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    GoalButtonGeneration(
                        text = "Генерация",
                        iconRes = R.drawable.icon_add,
                        onClick = {
                            goalViewModel.generateGoals()
                        }
                    )
                    GoalButtonParameters(
                        text = "Параметры",
                        iconRes = R.drawable.component_1,
                        onClick = { navController.navigate("settingsScreen") }
                    )
                }

                // Проверяем и отображаем кастомный Toast
                if (showToast) {
                    CustomToast(context = context, message = toastMessage) {
                        goalViewModel.resetToastFlag() // Сбрасываем флаг после показа тоста
                    }
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
                            width = 1.dp,
                            color = Maroon,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    if (generatedGoals.isEmpty()) {
                        Text(text = "Пока что целей нет! Скорее добавьте их :D", color = Color.Gray)
                    } else {
                        Column { // Используем обычный Column, так как LazyColumn уже не нужен
                            generatedGoals.forEach { goal ->
                                val isChecked = goal.isCompleted
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
                Text(
                    text = "Активность за месяц:",
                    color = DarkGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 16.dp)
                )
                ActivityCalendar(activeDays = activeDaysList)
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
fun GoalButtonParameters(text: String, iconRes: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.small.copy(all = CornerSize(12.dp)),
        modifier = Modifier
            .fillMaxWidth() // Сделаем кнопки одинаковыми по ширине
            .height(58.dp)
            .border(
                width = 1.dp,
                color = Maroon,
                shape = MaterialTheme.shapes.small.copy(all = CornerSize(12.dp))
            )
    ) {
        Image(painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = DarkGreen,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1, // Текст в одну строку
            overflow = TextOverflow.Ellipsis // Если текст не помещается, добавим многоточие
        )
    }
}
// Компонент кнопки с иконкой
@Composable
fun GoalButtonGeneration(text: String, iconRes: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.small.copy(all = CornerSize(12.dp)),
        modifier = Modifier
            .width(170.dp) // Сделаем кнопки одинаковыми по ширине
            .height(58.dp)
            .border(
                width = 1.dp,
                color = Maroon,
                shape = MaterialTheme.shapes.small.copy(all = CornerSize(12.dp))
            )
    ) {
        Image(painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = DarkGreen,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1, // Текст в одну строку
            overflow = TextOverflow.Ellipsis // Если текст не помещается, добавим многоточие
        )
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
            .padding(8.dp) // Внутренние отступы внутри элемента
    ) {
        // Чекбокс для отметки выполнения цели
        CustomCheckbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Колонка с именем цели, разделителем и сложностью
        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            // Текст цели
            Text(
                text = goal.name,
                style = if (isChecked) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // Разделитель
            Divider(
                color = Maroon,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            // Текст сложности
            Text(
                text = "Сложность: ${goal.difficulty.name}",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }

}
