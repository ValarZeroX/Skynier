package app.skynier.skynier.ui.layouts

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.skynier.skynier.ui.theme.Blue
import app.skynier.skynier.ui.theme.Gray
import app.skynier.skynier.ui.theme.Red
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import kotlin.math.ceil

@Composable
fun CustomCalendar(
    selectedDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit,
    startFromSunday: Boolean,
    highlightDays: Map<LocalDate, Boolean>
) {
    val today = LocalDate.now()
    val daysInMonth = selectedDate.lengthOfMonth() // 當前月有幾天
    val firstDayOfMonth = selectedDate.withDayOfMonth(1).dayOfWeek.value % 7 // 當前月的第一天是星期幾（1是星期一）
    val weekdays = getWeekDays(startFromSunday)

    Column(modifier = Modifier.padding(6.dp)) {
        // 顯示星期標題
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            weekdays.forEach { day ->
                WeekdayCell(weekday = day, modifier = Modifier.weight(1f))
            }
        }

        // 顯示日期網格
        val totalCells = daysInMonth + firstDayOfMonth
        val rows = ceil(totalCells / 7.0).toInt()

        Column {
            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until 7) {
                        val dayIndex = row * 7 + col
                        val day = dayIndex - firstDayOfMonth + 1

                        if (day in 1..daysInMonth) {
                            val date = selectedDate.withDayOfMonth(day)
                            val isSelected = date == selectedDate
                            val isToday = date == today
                            // 根據 recordData 判斷該日期是否有資料
                            val hasRecord = highlightDays[date] == true
                            Column(
                                modifier = Modifier
                                    .weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                       .size(40.dp)
                                        .clip(CircleShape)
                                        .clickable(
                                            onClick = { onDateSelected(date) },
                                            indication = ripple(bounded = true),
                                            interactionSource = remember { MutableInteractionSource() },
                                        )
                                        .padding(4.dp).border(
                                            width = 2.dp, // 边框宽度
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, // 今天的日期使用特定颜色，其他日期边框透明
                                            shape = CircleShape // 边框形状为圆形
                                        ).aspectRatio(1f)
                                        .background(
                                            color = if (isToday) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent,
                                            shape = CircleShape // 设置为圆形背景
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        color = if (hasRecord) MaterialTheme.colorScheme.onBackground else Gray,
                                    )
                                }
                            }
                        } else {
                            // 空格子
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekdayCell(weekday: Int, modifier: Modifier = Modifier) {
    val text = weekday.getDayOfWeek3Letters()

    val textColor = when (weekday) {
        Calendar.SUNDAY -> Red      // 禮拜日設置為紅色
        Calendar.SATURDAY -> Blue   // 禮拜六設置為藍色
        else -> Color.Unspecified         // 其他天不設置顏色
    }
    Text(
        text = text.orEmpty(),
        color = textColor,
        modifier = modifier,  // 使用傳入的修飾符並應用 weight
        textAlign = TextAlign.Center,
        fontSize = 14.sp
    )
}

private fun Int.getDayOfWeek3Letters(): String? = Calendar.getInstance().apply {
    set(Calendar.DAY_OF_WEEK, this@getDayOfWeek3Letters)
}.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())

private fun getWeekDays(startFromSunday: Boolean): List<Int> {
    val lists = (1..7).toList()
    return if (startFromSunday) lists else lists.drop(1) + lists.take(1)
}

//@Composable
//fun RecordScreen() {
//    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
//
//    // 模擬數據，日期對應的一些記錄
//    val recordData = mapOf(
//        LocalDate.now() to "資料1",
//        LocalDate.now().minusDays(1) to "資料2",
//        LocalDate.now().minusDays(3) to "資料3"
//    )
//
//    CustomCalendar(
//        selectedDate = selectedDate,
//        onDateSelected = { newDate ->
//            selectedDate = newDate // 更新選中的日期
//        },
//        recordData = recordData // 提供資料以顯示在對應日期內
//    )
//}