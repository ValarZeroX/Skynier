package app.skynier.skynier.ui.account

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import app.skynier.skynier.R
import app.skynier.skynier.database.entities.RecordEntity
import app.skynier.skynier.library.CustomDatePickerDialog
import app.skynier.skynier.library.MonthPickerDialog
import app.skynier.skynier.library.SharedOptions
import app.skynier.skynier.library.YearPickerDialog
import app.skynier.skynier.library.textColor
import app.skynier.skynier.ui.record.MergedTransferEntity
import app.skynier.skynier.ui.record.RecordDayListScreen
import app.skynier.skynier.ui.record.RecordDialog
import app.skynier.skynier.ui.record.RecordMergeDialog
import app.skynier.skynier.ui.record.mergeTransferRecords
import app.skynier.skynier.ui.report.FilterDialog
import app.skynier.skynier.ui.report.ReportMainScreenHeader
import app.skynier.skynier.ui.theme.Blue
import app.skynier.skynier.ui.theme.Gray
import app.skynier.skynier.ui.theme.Red
import app.skynier.skynier.viewmodels.AccountCategoryViewModel
import app.skynier.skynier.viewmodels.AccountViewModel
import app.skynier.skynier.viewmodels.CategoryViewModel
import app.skynier.skynier.viewmodels.CurrencyViewModel
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.RecordViewModel
import app.skynier.skynier.viewmodels.SkynierViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import app.skynier.skynier.viewmodels.UserSettingsViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AccountRecordScreen(
    navController: NavHostController,
    skynierViewModel: SkynierViewModel,
    categoryViewModel: CategoryViewModel,
    mainCategoryViewModel: MainCategoryViewModel,
    subCategoryViewModel: SubCategoryViewModel,
    accountCategoryViewModel: AccountCategoryViewModel,
    accountViewModel: AccountViewModel,
    currencyViewModel: CurrencyViewModel,
    recordViewModel: RecordViewModel,
    userSettingsViewModel: UserSettingsViewModel,
    accountId: Int,
) {
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var selectedFilter by rememberSaveable { mutableStateOf("Month") } // Default to "Month"

//    val firstDayOfMonth = selectedDate.withDayOfMonth(1)  // 當月第一天
//    val lastDayOfMonth = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth())  // 當月最後一天

    val startMonthDateMillis: Long
    val endMonthDateMillis: Long

// 根據 selectedFilter 計算要查詢的日期範圍
    when (selectedFilter) {
        "Day" -> {
            startMonthDateMillis =
                selectedDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
            endMonthDateMillis = selectedDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault())
                .toEpochSecond() * 1000
        }

        "Month" -> {
            val firstDayOfMonth = selectedDate.withDayOfMonth(1)
            val lastDayOfMonth = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth())
            startMonthDateMillis =
                firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
            endMonthDateMillis = lastDayOfMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault())
                .toEpochSecond() * 1000
        }

        "Year" -> {
            val firstDayOfYear = selectedDate.withDayOfYear(1)
            val lastDayOfYear = selectedDate.withDayOfYear(selectedDate.lengthOfYear())
            startMonthDateMillis =
                firstDayOfYear.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
            endMonthDateMillis = lastDayOfYear.atTime(23, 59, 59).atZone(ZoneId.systemDefault())
                .toEpochSecond() * 1000
        }

        else -> {
            // 自訂範圍，這裡假設是選擇月
            val firstDayOfMonth = selectedDate.withDayOfMonth(1)
            val lastDayOfMonth = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth())
            startMonthDateMillis =
                firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
            endMonthDateMillis = lastDayOfMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault())
                .toEpochSecond() * 1000
        }
    }

//    val recordTotal by recordViewModel.getRecordsByDateRangeAccountId(
//        startMonthDateMillis,
//        endMonthDateMillis,
//        accountId
//    ).observeAsState(emptyList())
    val recordTotal by recordViewModel.getRecordsByDateRange(
        startMonthDateMillis,
        endMonthDateMillis,
    ).observeAsState(emptyList())

//    val filteredRecords = recordTotal.filter {
//        it.accountId == accountId
//    }

    val filteredRecords = recordTotal.filter {
        it.accountId == accountId
    }.toMutableList() // 將 filteredRecords 轉換為可變列表以便於添加其他記錄

// 找到相同 datetime 的所有記錄並添加到 filteredRecords
    val filteredTransferRecords = filteredRecords.filter { it.type == 3 || it.type == 4 }

// 遍歷找到符合條件的記錄並加入到 filteredRecords
    filteredTransferRecords.forEach { transferRecord ->
        val matchingRecords = recordTotal.filter {
            it.datetime == transferRecord.datetime &&
                    (it.type == 3 || it.type == 4) &&
                    it.subCategoryId == transferRecord.subCategoryId &&
                    !filteredRecords.contains(it) // 確保不重複添加
        }
        filteredRecords.addAll(matchingRecords)
    }

// 如果需要將 filteredRecords 恢復為不可變列表，可以再將其轉換回來
    val finalFilteredRecords = filteredRecords.toList()

    Log.d("recordTotal", "$recordTotal")
    var showDialog by rememberSaveable { mutableStateOf(false) }
//    val accounts by accountViewModel.accounts.observeAsState(emptyList())



    val userSettings by userSettingsViewModel.userSettings.observeAsState()
    LaunchedEffect(Unit) {
        subCategoryViewModel.loadAllSubCategoriesAndGroupByMainCategory()
        userSettingsViewModel.loadUserSettings()
    }
    val accounts by accountViewModel.accounts.observeAsState(emptyList())


    Scaffold(
        topBar = {
            AccountRecordScreenHeader(
                navController,
                selectedDate,
                selectedFilter,
                onDateSelected = { newDate ->
                    selectedDate = newDate
                },
                onClickPrev = {
                    selectedDate = when (selectedFilter) {
                        "Day" -> selectedDate.minusDays(1)
                        "Month" -> selectedDate.minusMonths(1)
                        "Year" -> selectedDate.minusYears(1)
                        else -> selectedDate // Custom does not change with prev/next
                    }
                },
                onClickNext = {
                    selectedDate = when (selectedFilter) {
                        "Day" -> selectedDate.plusDays(1)
                        "Month" -> selectedDate.plusMonths(1)
                        "Year" -> selectedDate.plusYears(1)
                        else -> selectedDate // Custom does not change with prev/next
                    }
                },
                onFilterClick = {
                    showDialog = true
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column {
//                AccountRecordHorizontalBarChart(recordTotal, modifier = Modifier.height(300.dp).fillMaxWidth())
                RecordDayListScreen(
                    recordTotal = finalFilteredRecords,
                    userSettings,
                    subCategoryViewModel,
                    accounts,
                    navController,
                    recordViewModel
                )
            }

            if (showDialog) {
                FilterDialog(
                    selectedFilter = selectedFilter,
                    onFilterSelected = {
                        selectedFilter = it
                        showDialog = false
                    },
                    onDismissRequest = {
                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun AccountRecordHorizontalBarChart(recordTotal: List<RecordEntity>, modifier: Modifier = Modifier) {
    AndroidView(factory = { context ->
        HorizontalBarChart(context).apply {
            val entries = mutableListOf<BarEntry>()
            val labels = mutableListOf<String>()

            val countMap = mutableMapOf<String, Pair<Float, Int>>(
                "支出" to Pair(0f, 0),
                "收入" to Pair(0f, 0),
                "轉出" to Pair(0f, 0),
                "轉入" to Pair(0f, 0)
            )

            // 累計每個類型的金額和筆數
            recordTotal.forEach { record ->
                when (record.type) {
                    1 -> {
                        val currentValue = countMap["支出"]!!
                        countMap["支出"] = Pair(currentValue.first + record.amount.toFloat(), currentValue.second + 1)
                    }
                    2 -> {
                        val currentValue = countMap["收入"]!!
                        countMap["收入"] = Pair(currentValue.first + record.amount.toFloat(), currentValue.second + 1)
                    }
                    3 -> {
                        val currentValue = countMap["轉出"]!!
                        countMap["轉出"] = Pair(currentValue.first + record.amount.toFloat(), currentValue.second + 1)
                    }
                    4 -> {
                        val currentValue = countMap["轉入"]!!
                        countMap["轉入"] = Pair(currentValue.first + record.amount.toFloat(), currentValue.second + 1)
                    }
                }
            }

            // 為每個類型創建 BarEntry
            countMap.entries.forEachIndexed { index, entry ->
                val (amount, count) = entry.value
                entries.add(BarEntry(index.toFloat(), amount))
                labels.add("${entry.key} ($count)")
            }

            val dataSet = BarDataSet(entries, "").apply {
                colors = listOf(
                    Color(0xFFDC3545).toArgb(),  // Example color: Red
                    Color(0xFF6B5B95).toArgb(),  // Example color: Purple
                    Color(0xFF88B04B).toArgb(),  // Example color: Green
                    Color(0xFFF7CAC9).toArgb()   // Example color: Pink
                )
                valueTextSize = 12f
            }

            val data = BarData(dataSet)
            data.barWidth = 0.8f // 設定條形寬度

            this.data = data
            description.isEnabled = false

            // 設定 X 軸的標籤
            xAxis.apply {
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return labels.getOrNull(value.toInt()) ?: ""
                    }
                }
                textSize = 12f
                position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM_INSIDE
                setDrawGridLines(false)
            }

            // 設定 Y 軸
            axisLeft.isEnabled = false
            axisRight.textSize = 12f
            axisRight.setDrawGridLines(false)

            legend.isEnabled = false

            invalidate() // 刷新圖表
        }
    }, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountRecordScreenHeader(
    navController: NavHostController,
    localDate: LocalDate,
    selectedFilter: String,
    onDateSelected: (LocalDate) -> Unit,
    onClickNext: () -> Unit,
    onClickPrev: () -> Unit,
    onFilterClick: () -> Unit,
) {
    val currentLocale = Locale.getDefault()
    val dateFormatter = when (selectedFilter) {
        "Day" -> DateTimeFormatter.ofPattern("MMMM d, yyyy", currentLocale)
        "Month" -> DateTimeFormatter.ofPattern("MMMM, yyyy", currentLocale)
        "Year" -> DateTimeFormatter.ofPattern("yyyy", currentLocale)
        else -> DateTimeFormatter.ofPattern("MMMM, yyyy", currentLocale)
    }
    val formattedDate = localDate.format(dateFormatter)


    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showMonthPicker by rememberSaveable { mutableStateOf(false) }
    var showYearPicker by rememberSaveable { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectedFilter != "Custom") {
                    IconButton(onClick = onClickPrev) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous period")
                    }
                }

                Box(
                    modifier = Modifier.clickable {
                        when (selectedFilter) {
                            "Day" -> showDatePicker = true
                            "Month" -> showMonthPicker = true
                            "Year" -> showYearPicker = true
                        }
                    }
                ) {
                    Row {
                        Text(text = formattedDate)
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Calendar Month",
//                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                if (selectedFilter != "Custom") {
                    IconButton(onClick = onClickNext) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next period")
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Back"
                )
            }

        },
        actions = {
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Filter Options"
                )
            }
        }
    )
    if (showDatePicker) {
        CustomDatePickerDialog(
            initialDate = localDate,
            onDateSelected = { selectedDateMillis ->
                val selectedLocalDate = Instant.ofEpochMilli(selectedDateMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                onDateSelected(selectedLocalDate) // 通知父组件日期已更新
                showDatePicker = false
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }
    if (showMonthPicker) {
        MonthPickerDialog(
            initialYear = localDate.year,
            initialMonth = localDate.monthValue - 1,
            onMonthSelected = { year, month ->
                val selectedDate = LocalDate.of(year, month + 1, 1)
                onDateSelected(selectedDate)
                showMonthPicker = false
            },
            onDismiss = { showMonthPicker = false }
        )
    }
    if (showYearPicker) {
        YearPickerDialog(
            initialYear = localDate.year,
            onYearSelected = { year ->
                val selectedDate = LocalDate.of(year, 1, 1)
                onDateSelected(selectedDate)
                showYearPicker = false
            },
            onDismiss = { showYearPicker = false }
        )
    }
}