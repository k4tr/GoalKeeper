package com.example.goalkeeper.module

import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerExample(onTimeSelected: (Int) -> Unit) {

    // Состояние для отображения диалогового окна
    var isDialogVisible by remember { mutableStateOf(false) }

    // Получаем текущее время для установки начальных значений
    val currentTime = Calendar.getInstance()

    // Состояние для TimePicker
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true
    )

    // Кнопка для вызова диалога с TimePicker
    Button(onClick = { isDialogVisible = true }) {
        Text("Выбрать время")
    }

    // Если диалог должен быть видимым
    if (isDialogVisible) {
        AlertDialog(
            onDismissRequest = { isDialogVisible = false }, // Закрытие диалога при нажатии на пустую область
            title = { Text(text = "Выберите время") },
            text = {
                // Виджет для выбора времени
                TimeInput(
                    state = timePickerState
                )
            },
            confirmButton = {
                Button(onClick = {
                    // Рассчитываем общее время в минутах и передаем через onTimeSelected
                    val selectedTimeInMinutes = timePickerState.hour * 60 + timePickerState.minute
                    onTimeSelected(selectedTimeInMinutes)
                    isDialogVisible = false // Закрываем диалог
                }) {
                    Text("Подтвердить")
                }
            },
            dismissButton = {
                Button(onClick = { isDialogVisible = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}