package com.example.goalkeeper.module

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.goalkeeper.ui.theme.LightGreen
import com.example.goalkeeper.ui.theme.ThirdColor
import com.example.goalkeeper.ui.theme.aboba
import com.example.goalkeeper.ui.theme.border
import com.example.goalkeeper.viewmodel.TimeViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CircularTimeDistribution(timeViewModel: TimeViewModel) {
    var allUserTime by remember { mutableStateOf(60f) } // Инициализация значением по умолчанию

    var rightBoundaryEasy by remember { mutableStateOf(120f) } // Угол для сектора 1
    var rightBoundaryMed by remember { mutableStateOf(270f) } // Угол для сектора 2
    var rightBoundaryHard by remember { mutableStateOf(360f) }  // Угол для сектора 3
    var activeBoundary by remember { mutableStateOf<Int?>(null) }
    var iActiveHandle by remember { mutableStateOf<Int?>(0) } // Активная ручка для изменения
    var iDominatingSector by remember { mutableStateOf<Int?>(0) } // Самый большой сектор
    var fCircleRadius: Float = 20f
    var bIsBoundaryReached by remember { mutableStateOf<Boolean>(false) }

    // Вычисление времени для каждого сектора
    var easyGoalMinutes = ((rightBoundaryEasy - rightBoundaryHard+360f)%360f / 360f) * allUserTime
    var mediumGoalMinutes = ((rightBoundaryMed - rightBoundaryEasy+360f)%360f / 360f) * allUserTime
    var hardGoalMinutes = ((rightBoundaryHard - rightBoundaryMed+360f)%360f / 360f) * allUserTime
    //  Примерное время
    val avgEasyGoalTime = 10f // примерное среднее время для лёгкой цели
    val avgMediumGoalTime = 15f // для средней цели
    val avgHardGoalTime = 30f // для сложной цели
    // Рассчитываем количество целей для каждой категории на основе времени
    var easyGoalsCount = (easyGoalMinutes / avgEasyGoalTime).toInt()
    var mediumGoalsCount = (mediumGoalMinutes / avgMediumGoalTime).toInt()
    var hardGoalsCount = (hardGoalMinutes / avgHardGoalTime).toInt()

    LaunchedEffect(Unit) {
        // Получение времени из базы при старте
        val timeEntity = timeViewModel.getUserTime()
        if (timeEntity != null) {
            allUserTime = timeEntity.userTime
            easyGoalMinutes = timeEntity.easyGoalsTime
            mediumGoalMinutes = timeEntity.mediumGoalsTime
            hardGoalMinutes = timeEntity.hardGoalsTime
            rightBoundaryEasy = (easyGoalMinutes/allUserTime*360f + timeEntity.savedRightBoundaryHard)%360f
            rightBoundaryMed = ((easyGoalMinutes + mediumGoalMinutes)/allUserTime*360f + timeEntity.savedRightBoundaryHard)%360f
            rightBoundaryHard = timeEntity.savedRightBoundaryHard
        }
    }

    // Функция для вычисления угла касания относительно центра круга
    fun calculateAngle(center: Offset, touchPosition: Offset): Float {
        val dx = touchPosition.x - center.x
        val dy = touchPosition.y - center.y
        var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 90f
        if (angle < 0) angle += 360f
        return angle
    }

    // Определение ближайшей границы между секторами
    fun calculateClosestBoundary(angle: Float): Int {
        val distToSectorEasy = Math.abs(angle - rightBoundaryEasy)
        val distToSectorMed = Math.abs(angle - rightBoundaryMed)
        val distToSectorHard = Math.abs(angle - rightBoundaryHard)

        // Находим минимальное расстояние и возвращаем номер ближайшей границы
        return when {
            distToSectorEasy <= distToSectorMed && distToSectorEasy <= distToSectorHard -> 1
            distToSectorMed <= distToSectorEasy && distToSectorMed <= distToSectorHard -> 2
            else -> 3
        }
    }

    fun calculateDistance(touchPosition: Offset, center: Offset): Float {
        val dx = touchPosition.x - center.x
        val dy = touchPosition.y - center.y
        return sqrt(dx * dx + dy * dy)
    }
    // Обработка начала жеста
    fun onDragStart(touchPosition: Offset, center: Offset,center1: Offset, center2: Offset, center3: Offset) {
        iActiveHandle = 0
        if (calculateDistance(touchPosition, center1) < fCircleRadius*5)
        {
            iActiveHandle = 1
        }

        if (calculateDistance(touchPosition, center2) < fCircleRadius*5)
        {
            iActiveHandle = 2
        }

        if (calculateDistance(touchPosition, center3) < fCircleRadius*5)
        {
            iActiveHandle = 3
        }
        if (iActiveHandle != 0)
            bIsBoundaryReached = false;
        val angle = calculateAngle(center, touchPosition)
        activeBoundary = calculateClosestBoundary(angle) // Определяем активную границу
    }

    fun calculateBoarders(angle: Float, left: Float, right: Float): Float {
        var result: Float = angle

        val sectorE = (rightBoundaryEasy - rightBoundaryHard + 360f)%360f
        val sectorM = (rightBoundaryMed - rightBoundaryEasy + 360f)%360f
        val sectorH = (rightBoundaryHard - rightBoundaryMed + 360f)%360f

        iDominatingSector = 0
        if (sectorE >= sectorM && sectorE >= sectorH)
            iDominatingSector = 1
        else if (sectorM >= sectorH)
            iDominatingSector = 2
        else
            iDominatingSector = 3

        if (right > left) {
            // Случай, когда правая граница больше левой (обычный)
            result = angle.coerceIn(left, right)
        } else if (right == left){
            if (angle < right-3f) {
                result = angle.coerceIn(0f, right)
            }
            else if (angle > right+3f){
                result = angle.coerceIn(right, 360f)
            }
            else {
                result = right
                bIsBoundaryReached = true
            }
        } else {
            // Случай, когда правая граница меньше левой (пересечение через 0/360 градусов)
            if (angle <= right || angle >= left) {
                if (angle <= right) {
                    result = angle.coerceIn(0f, right)
                }
                else{
                    result = angle.coerceIn(left, 360f)
                }
            } else if (angle > right && angle < (right + left)/2) {
                result = right
                bIsBoundaryReached = true
            } else if (angle > (right + left)/2 && angle < left ) {
                result = left
                bIsBoundaryReached = true
            }
        }

        // Проверка, достигли ли границы
        if (result == left || result == right) {
            bIsBoundaryReached = true
        }

        // Возвращаем значение, корректированное для диапазона 0..360
        return (result + 360f) % 360f
    }

    // Обработка самого жеста
    fun onDrag(angle: Float, dragAmount: Float) {
        when (activeBoundary) {
            1 -> {
                if (!bIsBoundaryReached) {
                    rightBoundaryEasy = calculateBoarders(angle, rightBoundaryHard, rightBoundaryMed)
                }
            }

            2 -> {
                if (!bIsBoundaryReached) {
                    rightBoundaryMed = calculateBoarders(angle, rightBoundaryEasy, rightBoundaryHard)
                }
            }

            3 -> {
                if (!bIsBoundaryReached) {
                    rightBoundaryHard = calculateBoarders(angle, rightBoundaryMed, rightBoundaryEasy)
                }
            }
        }

        easyGoalMinutes = ((rightBoundaryEasy - rightBoundaryHard+360f)%360f / 360f) * allUserTime
        mediumGoalMinutes = ((rightBoundaryMed - rightBoundaryEasy+360f)%360f / 360f) * allUserTime
        hardGoalMinutes = ((rightBoundaryHard - rightBoundaryMed+360f)%360f / 360f) * allUserTime

        timeViewModel.updateUserTime(
            newUserTime = allUserTime,
            newEasyGoalsTime = easyGoalMinutes,
            newMediumGoalsTime = mediumGoalMinutes,
            newHardGoalsTime = hardGoalMinutes,
            savedRightBoundaryHard = rightBoundaryHard
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.padding(36.dp))
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center

        ) {
            Canvas(
                modifier = Modifier
                    .size(300.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { touchPosition ->
                                val center = Offset(
                                    (size.width / 2).toFloat(),
                                    (size.height / 2).toFloat()
                                )
                                val canvasSize = min(size.width, size.height)
                                val radius = canvasSize / 2 * 0.8f
                                var radianAngle =
                                    Math.toRadians((rightBoundaryEasy - 90f).toDouble())
                                var handleX = center.x + cos(radianAngle) * radius
                                var handleY = center.y + sin(radianAngle) * radius
                                val center1 = Offset(handleX.toFloat(), handleY.toFloat())

                                radianAngle = Math.toRadians((rightBoundaryMed - 90f).toDouble())
                                handleX = center.x + cos(radianAngle) * radius
                                handleY = center.y + sin(radianAngle) * radius
                                val center2 = Offset(handleX.toFloat(), handleY.toFloat())

                                radianAngle = Math.toRadians((rightBoundaryHard - 90f).toDouble())
                                handleX = center.x + cos(radianAngle) * radius
                                handleY = center.y + sin(radianAngle) * radius
                                val center3 = Offset(handleX.toFloat(), handleY.toFloat())
                                onDragStart(touchPosition, center, center1, center2, center3)
                            },
                            onDrag = { change, dragAmount ->
                                val center =
                                    Offset((size.width / 2).toFloat(), (size.height / 2).toFloat())
                                val touchPosition = change.position
                                val angle = calculateAngle(center, touchPosition)

                                // Обновляем углы секторов
                                onDrag(angle, dragAmount.x)
                            }
                        )
                    }
            ) {
                val canvasSize = size.minDimension
                val radius = canvasSize / 2 * 0.8f
                val center = Offset(size.width / 2, size.height / 2)

                // Отрисовка секторов
                drawArc(
                    color = LightGreen,
                    startAngle = (-90f + rightBoundaryHard + 360f) % 360f,
                    sweepAngle = if (iDominatingSector == 1 && rightBoundaryMed == rightBoundaryHard && rightBoundaryMed == rightBoundaryEasy) 360f else (rightBoundaryEasy - rightBoundaryHard + 720f) % 360f,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
                drawArc(
                    color = aboba,
                    startAngle = (-90f + rightBoundaryEasy + 360f) % 360f,
                    sweepAngle = if (iDominatingSector == 2 && rightBoundaryEasy == rightBoundaryHard && rightBoundaryMed == rightBoundaryEasy) 360f else (rightBoundaryMed - rightBoundaryEasy + 720f) % 360f,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
                drawArc(
                    color = ThirdColor,
                    startAngle = (-90f + rightBoundaryMed + 360f) % 360f,
                    sweepAngle = if (iDominatingSector == 3 && rightBoundaryMed == rightBoundaryEasy && rightBoundaryMed == rightBoundaryHard) 360f else (rightBoundaryHard - rightBoundaryMed + 720f) % 360f,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )

                // Отрисовка кнопок на границах секторов
                fun drawHandle(angle: Float, color: Color) {
                    val radianAngle = Math.toRadians((angle - 90).toDouble())
                    val handleX = center.x + cos(radianAngle) * radius
                    val handleY = center.y + sin(radianAngle) * radius

                    drawCircle(
                        color = color,
                        radius = fCircleRadius,
                        center = Offset(handleX.toFloat(), handleY.toFloat())
                    )
                }

                // Отрисовываем кнопки на границе между секторами
                drawHandle(rightBoundaryEasy, border)
                drawHandle(rightBoundaryMed, border)
                drawHandle(rightBoundaryHard, border)

                // Отрисовка границ круга
                drawCircle(
                    color = border,
                    radius = radius,
                    style = Stroke(4.dp.toPx())
                )
            }

        }
        // Текст для отображения времени каждого сектора
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text("Заданное вами время в минутах: ${allUserTime.toInt()} мин.")
            Spacer(modifier = Modifier.padding(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Легкие цели занимают: ${easyGoalMinutes.toInt()} мин.")
                Text("${easyGoalsCount} цели")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Легкие цели занимают: ${mediumGoalMinutes.toInt()} мин.")
                Text("${mediumGoalsCount} цели")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Легкие цели занимают: ${hardGoalMinutes.toInt()} мин.")
                Text("${hardGoalsCount} цели")
            }


        }

        // Кнопка для выбора времени
        Spacer(modifier = Modifier.height(16.dp))
        TimePickerExample { selectedTimeInMinutes ->
            allUserTime = selectedTimeInMinutes.toFloat()
            val easyGoalsTime = timeViewModel.easyGoalsTime.value
            val mediumGoalsTime = timeViewModel.mediumGoalsTime.value
            val hardGoalsTime = timeViewModel.hardGoalsTime.value

            timeViewModel.updateUserTime(
                newUserTime = selectedTimeInMinutes.toFloat(),
                newEasyGoalsTime = easyGoalsTime,
                newMediumGoalsTime = mediumGoalsTime,
                newHardGoalsTime = hardGoalsTime,
                savedRightBoundaryHard = rightBoundaryHard
            )
        }
    }

}

