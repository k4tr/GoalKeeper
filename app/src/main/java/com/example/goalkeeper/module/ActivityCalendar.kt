package com.example.goalkeeper.module

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActivityCalendar(
    activeDays: List<LocalDate> = listOf(), // Список активных дней
    modifier: Modifier = Modifier
) {
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    val daysInMonth = selectedMonth.lengthOfMonth() // Корректное количество дней для текущего месяца
    val firstDayOfMonth = selectedMonth.atDay(1)
    val daysOffset = firstDayOfMonth.dayOfWeek.value % 7

    Column(
        modifier = modifier
            .height(300.dp)
            .padding(16.dp)) {
        // Шапка с названием месяца и навигацией
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { selectedMonth = selectedMonth.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }
            Text(
                text = selectedMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        + " " + selectedMonth.year,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
            IconButton(onClick = { selectedMonth = selectedMonth.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }

        // Заголовки дней недели
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { day ->
                Text(text = day, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Сетка дней месяца
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(4.dp)
        ) {
            // Пустые ячейки для смещения начала месяца
            items(daysOffset) {
                Spacer(modifier = Modifier.size(40.dp))
            }

            // Ячейки для каждого дня месяца
            items(daysInMonth) { day ->
                val currentDate = selectedMonth.atDay(day + 1)
                val isActive = activeDays.contains(currentDate)

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .background(
                            color = if (isActive) Color(0xFF778FD2) else Color.Transparent,
                            shape = MaterialTheme.shapes.small
                        )
                        .clickable { /* Обработчик клика по дню */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = (day + 1).toString())
                }
            }
        }
    }
}


