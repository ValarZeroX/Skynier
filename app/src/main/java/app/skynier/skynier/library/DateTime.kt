package app.skynier.skynier.library

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
fun combineDateAndTimeVersion2(selectedDate: Long?, selectedTime: TimePickerState): Long {
    // 如果日期或时间为空，则返回 null
    if (selectedDate == null) return System.currentTimeMillis()
    // 创建 Calendar 实例
    val calendar = Calendar.getInstance().apply {
        // 设置日期部分
        timeInMillis = selectedDate
        // 设置时间部分
        set(Calendar.HOUR_OF_DAY, selectedTime.hour)
        set(Calendar.MINUTE, selectedTime.minute)
        set(Calendar.SECOND, 0) // 可选，设置秒钟
        set(Calendar.MILLISECOND, 0) // 可选，设置毫秒
    }

    // 返回时间戳
    return calendar.timeInMillis
}