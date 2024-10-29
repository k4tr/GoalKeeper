package com.example.goalkeeper.module

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.goalkeeper.R
import kotlinx.coroutines.delay

@SuppressLint("MissingInflatedId")
@Composable
fun CustomToast(context: Context, message: String, onDismiss: () -> Unit) {
    val toast = Toast(context)
    val view = LayoutInflater.from(context).inflate(R.layout.custom_toast_layout, null)

    // Пример кастомизации. Можно настроить в layout/custom_toast_layout.xml
    view.findViewById<TextView>(R.id.toast_text).text = message

    toast.view = view
    toast.duration = Toast.LENGTH_SHORT
    // Показать тост
    LaunchedEffect(Unit) {
        toast.show()
        delay(2000) // Задержка, чтобы тост оставался видимым 2 секунды
        onDismiss() // Вызываем onDismiss после показа
    }
}