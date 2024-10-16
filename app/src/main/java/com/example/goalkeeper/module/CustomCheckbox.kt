package com.example.goalkeeper.module

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.goalkeeper.ui.theme.Maroon

@Composable
fun CustomCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = CheckboxDefaults.colors(
            checkmarkColor = Color.White, // Цвет галочки
            uncheckedColor = Maroon, // Цвет обводки, когда чекбокс неактивен
            checkedColor = Color(0xFF778FD2), // Цвет фона активного чекбокса
            disabledCheckedColor = Color(0xFF778FD2) // Цвет для состояния, когда чекбокс неактивен, но отмечен
        )
    )
}