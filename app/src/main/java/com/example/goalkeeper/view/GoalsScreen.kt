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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goalkeeper.R
import com.example.goalkeeper.ui.theme.DarkGreen
import com.example.goalkeeper.ui.theme.GoalKeeperTheme
import com.example.goalkeeper.ui.theme.Maroon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen() {
    val scrollState = rememberScrollState()
    Scaffold(
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    GoalButton(
                        text = "Ввести цель",
                        icon = R.drawable.icon_add,
                        onClick = { /* TODO: Handle "Ввести цель" action */ }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    GoalButtonWithCustomIcon(
                        text = "Генерация",
                        iconRes = R.drawable.component_1, // Используем пользовательскую иконку
                        onClick = { /* TODO: Handle "Генерация" action */ }
                    )
                }


                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }
}
// Получение текущей даты в нужном формате
fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return sdf.format(Date())
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GoalKeeperTheme() {
        GoalsScreen()
    }
}
@Composable
fun GoalButtonWithCustomIcon(text: String, iconRes: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.small.copy(all = CornerSize(12.dp)),
        modifier = Modifier
            .width(168.dp)
            .height(48.dp)
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
fun GoalButton(text: String, icon: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.small.copy(all = CornerSize(12.dp)), // Закругленные углы
        modifier = Modifier
            .width(168.dp)
            .height(48.dp)
            .border(
                width = 1.dp, // Ширина обводки
                color = Maroon, // Цвет обводки
                shape = MaterialTheme.shapes.small.copy(all = CornerSize(12.dp)) // Форма обводки
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, // Центрирование элементов по вертикали
            modifier = Modifier.width(168.dp) // Заполнение всей доступной ширины кнопки
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = DarkGreen,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                maxLines = 1,
                modifier = Modifier.weight(0.8f) // Текст занимает 80% ширины кнопки
            )
        }
    }
}