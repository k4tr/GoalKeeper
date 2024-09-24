package com.example.goalkeeper.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.example.goalkeeper.data.Difficulty
import com.example.goalkeeper.data.Goal
import com.example.goalkeeper.di.GoalRepository
import com.example.goalkeeper.ui.theme.DarkGreen
import com.example.goalkeeper.viewmodel.GoalViewModel
import androidx.compose.foundation.background

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(
    navController: NavController,
    goalViewModel: GoalViewModel,  // ViewModel для работы с данными
    onBackClick: () -> Unit,       // Функция для возврата назад
    onSaveClick: () -> Unit        // Функция для сохранения цели
) {
    var goalName by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY)}

    val rainbowColors: List<Color> = listOf(
        Color.Red,
        Color(0xFFFFA500), // Оранжевый
        Color.Yellow,
        Color.Green,
        Color.Blue,
        Color(0xFF4B0082), // Индиго
        Color(0xFFEE82EE)  // Фиолетовый
    )
    val brush = remember {
        Brush.linearGradient(
            colors = rainbowColors
        )
    }
    Scaffold(

    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF2F2F6)) // Тот же цвет фона, что и на главном экране
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,  modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { onBackClick() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                }
                ElevatedButton(onClick = {
                    if (goalName.isNotBlank()) {
                        val newGoal = Goal(name = goalName, difficulty = selectedDifficulty)
                        goalViewModel.addGoal(newGoal) // Используем метод addGoal
                        navController.popBackStack() // Возвращаемся на главный экран после сохранения
                    }
                }, shape = RoundedCornerShape(15.dp), // округлая кнопка
                    colors = ButtonDefaults.buttonColors(   // цвет текста
                        containerColor = Color(0xFF3D5220)),
                    modifier = Modifier.padding(10.dp)) {
                    Text("Сохранить")
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            // Поле для ввода имени цели
            TextField(
                value = goalName,
                textStyle = TextStyle(
                    brush = brush,
                    fontSize = 24.sp // Устанавливаем размер шрифта
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedTextColor = Color(0xff888888),
                    focusedContainerColor = Color.White,
                    focusedTextColor = Color(0xff222222),
                ),
                modifier = Modifier
                    .fillMaxWidth()

                    .background(Color.Transparent), // Прозрачный контейнер
                onValueChange = { goalName = it },
                label = { Text("Введите имя цели") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Выбор сложности цели
            Text(text = "Выберите сложность:", color = DarkGreen)
            DifficultyRadioButton(selectedDifficulty) { difficulty ->
                selectedDifficulty = difficulty
            }
        }
    }
}
@Composable
fun DifficultyRadioButton(
    selectedDifficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit
) {
    Column {
        Difficulty.values().forEach { difficulty ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = (selectedDifficulty == difficulty),
                    onClick = { onDifficultySelected(difficulty) }
                )
                Text(
                    text = difficulty.name,
                    color = DarkGreen,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

