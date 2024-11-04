package app.skynier.skynier.ui.account

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import app.skynier.skynier.R
import app.skynier.skynier.database.entities.RecordEntity
import app.skynier.skynier.database.entities.UserSettingsEntity
import app.skynier.skynier.library.CustomDatePickerDialog
import app.skynier.skynier.library.MonthPickerDialog
import app.skynier.skynier.library.YearPickerDialog
import app.skynier.skynier.library.textColor
import app.skynier.skynier.ui.record.RecordDayListScreen
import app.skynier.skynier.ui.report.FilterDialog
import app.skynier.skynier.viewmodels.AccountCategoryViewModel
import app.skynier.skynier.viewmodels.AccountViewModel
import app.skynier.skynier.viewmodels.CategoryViewModel
import app.skynier.skynier.viewmodels.CurrencyViewModel
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.RecordViewModel
import app.skynier.skynier.viewmodels.SkynierViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import app.skynier.skynier.viewmodels.UserSettingsViewModel
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

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




    val filteredRecordByCount = recordTotal.filter {
        it.accountId == accountId
    }

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

    var selectedTypeFilter by rememberSaveable { mutableStateOf<Int?>(null) }
    val filteredRecordsByType = if (selectedTypeFilter != null) {
        if (selectedTypeFilter == 3 || selectedTypeFilter == 4) {
            finalFilteredRecords.filter { it.type == 3 || it.type == 4 }
        } else {
            finalFilteredRecords.filter { it.type == selectedTypeFilter }
        }
    } else {
        finalFilteredRecords
    }
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
                if (recordTotal.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(id = R.string.no_data_available), fontSize = 16.sp)
                    }
                    return@Box
                }
                AccountRecordBarChart(
                    filteredRecordByCount,
                    userSettings,
                    selectedTypeFilter = selectedTypeFilter,
                    onTypeSelected = { type ->
                        selectedTypeFilter = type
                    }
                )
                RecordDayListScreen(
                    recordTotal = filteredRecordsByType,
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
fun AccountRecordBarChart(
    records: List<RecordEntity>,
    userSettings: UserSettingsEntity?,
    selectedTypeFilter: Int?,
    onTypeSelected: (Int?) -> Unit
) {
    // 將記錄根據類型分組，並計算每個類型的金額總和和筆數
    val groupedRecords = records.groupBy { it.type }
    val expense = groupedRecords[1]?.sumOf { it.amount } ?: 0.0
    val income = groupedRecords[2]?.sumOf { it.amount } ?: 0.0
    val transferOut = groupedRecords[3]?.sumOf { it.amount } ?: 0.0
    val transferIn = groupedRecords[4]?.sumOf { it.amount } ?: 0.0

    val expenseCount = groupedRecords[1]?.size ?: 0
    val incomeCount = groupedRecords[2]?.size ?: 0
    val transferOutCount = groupedRecords[3]?.size ?: 0
    val transferInCount = groupedRecords[4]?.size ?: 0

    val total = income + transferIn - expense - transferOut

    // 找出最大絕對值
    val maxAmount = listOf(
        expense,
        income,
        transferOut,
        transferIn,
        total
    ).maxOfOrNull { abs(it) } ?: 1.0

    var textColorExpense = MaterialTheme.colorScheme.onBackground
    var textColorIncome = MaterialTheme.colorScheme.onBackground
    userSettings?.let {
        textColorExpense = textColor(it.textColor, 1)
        textColorIncome= textColor(it.textColor, 2)
    }
    // 使用 Column 佈局顯示不同類型的數據
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
        // 顯示每個類型的數據
        if (expenseCount > 0) {
            RecordBarChartItem(
                label = stringResource(id = R.string.expense),
                count = expenseCount,
                amount = -expense,
                color = textColorExpense,
                maxAmount = maxAmount,
                type = 1,
                isSelected = selectedTypeFilter == 1,
                onClick = onTypeSelected
            )
        }
        if (incomeCount > 0) {
            RecordBarChartItem(
                label = stringResource(id = R.string.income),
                count = incomeCount,
                amount = income,
                color = textColorIncome,
                maxAmount = maxAmount,
                type = 2,
                isSelected = selectedTypeFilter == 2,
                onClick = onTypeSelected
            )
        }
        if (transferOutCount > 0) {
            RecordBarChartItem(
                label = stringResource(id = R.string.transfer_out),
                count = transferOutCount,
                amount = -transferOut,
                color = textColorExpense, // 橘色
                maxAmount = maxAmount,
                type = 3,
                isSelected = selectedTypeFilter == 3,
                onClick = onTypeSelected
            )
        }
        if (transferInCount > 0) {
            RecordBarChartItem(
                label = stringResource(id = R.string.transfer_in),
                count = transferInCount,
                amount = transferIn,
                color = textColorIncome, // 金黃色
                maxAmount = maxAmount,
                type = 4,
                isSelected = selectedTypeFilter == 4,
                onClick = onTypeSelected
            )
        }
        if (expenseCount + incomeCount + transferOutCount + transferInCount > 0) {
            RecordBarChartItem(
                label = stringResource(id = R.string.total),
                count = expenseCount + incomeCount + transferOutCount + transferInCount,
                amount = total,
                color = Color(0xFF800080), // 紫色
                maxAmount = maxAmount,
                type = null,
                isSelected = selectedTypeFilter == null,
                onClick = onTypeSelected
            )
        }
    }
}

@Composable
fun RecordBarChartItem(
    label: String,
    count: Int,
    amount: Double,
    color: Color,
    maxAmount: Double,
    type: Int?,
    isSelected: Boolean,
    onClick: (Int?) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp).background(if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent)
            .clickable { onClick(type) }
    ) {
        // 顯示標籤和筆數
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(6f)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color = color, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "$label ($count)", fontSize = 14.sp)
        }
        // 計算條狀圖的相對寬度比例
        val percentage = (kotlin.math.abs(amount) / maxAmount).coerceIn(0.0, 1.0)
        // 顯示條狀圖
        Box(
            modifier = Modifier
                .padding(start = 10.dp)
                .height(10.dp)
                .weight(8f)
        ) {
            Box (
                modifier = Modifier
                    .padding(start = 10.dp)
                    .height(10.dp)
                    .fillMaxWidth(fraction = percentage.toFloat()) // 根據百分比來決定條狀圖的寬度
                    .background(color = color.copy(alpha = 0.7f), shape = RoundedCornerShape(8.dp))
            )
        }
        // 顯示金額
        Box(
            modifier = Modifier
                .weight(8f)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "${if (amount >= 0) "+" else "-"}\$${"%,.0f".format(kotlin.math.abs(amount))}",
                color = color,
                fontSize = 14.sp,
            )
        }
    }
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

fun formatAmount(amount: Float): String {
    return when {
        amount >= 1_000_000_000 -> DecimalFormat("#.#B").format(amount / 1_000_000_000) // 十億以上顯示為 B
        amount >= 1_000_000 -> DecimalFormat("#.#M").format(amount / 1_000_000) // 百萬以上顯示為 M
        else -> DecimalFormat("#,###").format(amount) // 其他顯示為原始數字
    }
}