package app.skynier.skynier.ui.record

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.skynier.skynier.ui.layouts.CustomCalendar
import java.time.LocalDate

@Composable
fun RecordDayScreen(selectedDate: LocalDate) {
    // 模擬數據，日期對應的一些記錄
    val recordData = mapOf(
        LocalDate.now() to "資料1",
        LocalDate.now().minusDays(1) to "資料2",
        LocalDate.now().minusDays(3) to "資料3"
    )

    CustomCalendar(
        selectedDate = selectedDate,
        onDateSelected = { newDate ->
//            localDate = newDate // 更新選中的日期
        },
        recordData = recordData, // 提供資料以顯示在對應日期內,
        startFromSunday = true,
    )
}