package com.example.goalkeeper.view

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.goalkeeper.data.model.Goal
import com.example.goalkeeper.module.AppBottomBar
import com.example.goalkeeper.module.BottomNavTab
import com.example.goalkeeper.ui.theme.Maroon
import com.example.goalkeeper.viewmodel.GoalViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.example.goalkeeper.module.CustomToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun SearchScreen(
    navController: NavController,
    goalViewModel: GoalViewModel,
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    onBackClick: () -> Unit
) {

    val searchQuery = remember { mutableStateOf("") }
    val allGoals by goalViewModel.allGoals.collectAsState() // Получаем все цели
    var filteredGoals = allGoals.filter {
        it.name.contains(searchQuery.value, ignoreCase = true)
    }
    var isFocused by remember { mutableStateOf(false) } // Состояние фокуса для TextField
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Поиск") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
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

    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        isFocused = false // Сбрасываем фокус при нажатии вне TextField
                    })
                }
        ) {
            Spacer(modifier = Modifier.height(66.dp))

            // Поле для ввода поиска
            TextField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                label = {
                    Text(
                        text = "Поиск",
                        color = Color.Gray
                    )
                },
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused // Обновляем состояние фокуса
                    }
                    .focusRequester(FocusRequester.Default.takeIf { isFocused } ?: FocusRequester())
                    .border(
                        width = 1.dp, // Ширина обводки
                        color = Maroon, // Цвет обводки
                        shape = RoundedCornerShape(12.dp) // Форма обводки
                    )
                    .background(Color.White, shape = RoundedCornerShape(12.dp)), // Закругленные углы
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    unfocusedTextColor = Color(0xff888888),
                    focusedContainerColor = Color.White,
                    focusedTextColor = Color(0xff222222),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {

                items(
                    filteredGoals,
                    key = { goal -> goal.id }
                ) { goal ->
                    GoalItem(
                        goal = goal,
                        modifier = Modifier
                            .animateItemPlacement( // Анимация перемещения
                                animationSpec = tween(3000) // Настройка скорости анимации
                            ),
                        onDelete = { deletedGoal ->
                            // Удаление цели из ViewModel
                            goalViewModel.deleteGoal(deletedGoal)
                            // Обновляем список после удаления

                            filteredGoals = filteredGoals.toMutableList().apply {
                                remove(deletedGoal)

                            }

                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DismissBackground(dismissState: DismissState) {
    val color = when (dismissState.dismissDirection) {
        DismissDirection.EndToStart -> Color(0xB2CC3350)
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape = RoundedCornerShape(12.dp))
            .background(color),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = Color.White,
            modifier = Modifier
                .size(45.dp)
                .padding(end = 8.dp)
        )
    }
}
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun GoalItem(
    goal: Goal,
    modifier: Modifier = Modifier,
    onDelete: (Goal) -> Unit
) {
    val dismissState = rememberDismissState(DismissValue.Default)
    val scope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(true) }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        dismissThresholds = { FractionalThreshold(0.66f) },
        background = { DismissBackground(dismissState) },
        dismissContent = {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)) + expandVertically(),
                exit = fadeOut(animationSpec = tween(durationMillis = 300)) + shrinkVertically(),
            ) {
                ItemInRow(goal = goal)
            }
        }
    )

    // Проверяем состояние свайпа
    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
        // Запускаем анимацию скрытия и удаляем элемент
        scope.launch {
            isVisible = false
            delay(300)  // Задержка для завершения анимации
            onDelete(goal)
        }
    }
}

@Composable
fun ItemInRow(goal: Goal) {
    Box(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(12.dp)) // Белая подложка с закругленными углами
            .border(
                width = 1.dp, // Ширина обводки
                color = Maroon, // Цвет обводки
                shape = RoundedCornerShape(12.dp) // Форма обводки
            )
            .padding(16.dp)
    ) {
        Column {
            Text(text = goal.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Divider(
                color = Maroon,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 6.dp)
            )
            Text(text = goal.difficulty.name, color = Color.Gray)
        }
    }

}



