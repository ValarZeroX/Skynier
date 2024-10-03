package app.skynier.skynier.ui.record

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.skynier.skynier.database.entities.RecordEntity
import app.skynier.skynier.library.SharedOptions
import app.skynier.skynier.ui.layouts.CustomCalendar
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import java.time.LocalDate

@Composable
fun RecordDayScreen(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    recordsDay: List<RecordEntity>,
    recordsMonth: Map<Int, Int>,
    subCategoryViewModel: SubCategoryViewModel,
) {
    // 模擬數據，日期對應的一些記錄
//    val recordData = mapOf(
//        LocalDate.now() to "資料1",
//        LocalDate.now().minusDays(1) to "資料2",
//        LocalDate.now().minusDays(3) to "資料3"
//    )

    val highlightDays = remember(recordsMonth) {
        recordsMonth.values.associate { day ->
            Log.d("day", "$day")
            selectedDate.withDayOfMonth(day) to true // 將日期轉換為 LocalDate 並與 true 關聯
        }
    }
    val subCategoriesByMainCategory by subCategoryViewModel.subCategoriesByMainCategory.observeAsState(emptyMap())
    LaunchedEffect(Unit) {
        subCategoryViewModel.loadAllSubCategoriesAndGroupByMainCategory()
    }

    Log.d("recordsMonth", "$recordsMonth")
    Column {
        CustomCalendar(
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            startFromSunday = true,
            highlightDays = highlightDays
        )
        HorizontalDivider()
        LazyColumn {
            items(recordsDay) { record ->
                // 獲取該 mainCategoryId 下的子類別
                val subCategoriesForThisMainCategory = subCategoriesByMainCategory[record.mainCategoryId] ?: emptyList()

                // 根據 subCategoryId 查找匹配的子類別
                val matchingSubCategory = subCategoriesForThisMainCategory.find { it.subCategoryId == record.subCategoryId }

                // 如果找到匹配的子類別，進行處理
                matchingSubCategory?.let { category ->
                    val recordIcon = SharedOptions.iconMap[category.subCategoryIcon]
                    val backgroundColor = Color(android.graphics.Color.parseColor("#${category.subCategoryBackgroundColor}"))
                    val iconColor = Color(android.graphics.Color.parseColor("#${category.subCategoryIconColor}"))

                    ListItem(
                        headlineContent = { Text(text = record.name) },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(backgroundColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                recordIcon?.let { iconData ->
                                    // 只有在 recordIcon 不為 null 時才顯示圖標
                                    Icon(
                                        imageVector = iconData.icon,
                                        contentDescription = category.subCategoryNameKey,
                                        modifier = Modifier.size(28.dp),
                                        tint = iconColor
                                    )
                                } ?: run {
                                    // 如果沒有找到 icon，顯示一個默認的佔位圖標
                                    Icon(
                                        imageVector = Icons.Default.Error, // 可以換成適合的默認圖標
                                        contentDescription = "Default icon",
                                        modifier = Modifier.size(28.dp),
                                        tint = Color.Gray
                                    )
                                }
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}