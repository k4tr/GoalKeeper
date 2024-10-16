package com.example.goalkeeper.module

import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerExample(onTimeSelected: (Int) -> Unit) {

    // State for storing selected time
    val context = LocalContext.current
    var allUserTime by remember { mutableStateOf(LocalTime.of(0, 0)) }
    var isTimePickerVisible by remember { mutableStateOf(false) }

    // TimePickerDialog для выбора времени
    if (isTimePickerVisible) {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                allUserTime = LocalTime.of(hourOfDay, minute)
                isTimePickerVisible = false
            },
            allUserTime.hour,
            allUserTime.minute,
            true // 24-часовой формат
        ).show()
    }

    // Кнопка для вызова TimePicker
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(text = "Выбранное время: ${allUserTime.hour} ч. ${allUserTime.minute} мин.")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { isTimePickerVisible = true }) {
            Text("Выбрать время")
        }
    }
}