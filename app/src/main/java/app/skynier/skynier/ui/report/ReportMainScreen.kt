package app.skynier.skynier.ui.report

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import app.skynier.skynier.R
import app.skynier.skynier.viewmodels.AccountCategoryViewModel
import app.skynier.skynier.viewmodels.AccountViewModel
import app.skynier.skynier.viewmodels.CategoryViewModel
import app.skynier.skynier.viewmodels.CurrencyViewModel
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.RecordViewModel
import app.skynier.skynier.viewmodels.SkynierViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import app.skynier.skynier.viewmodels.UserSettingsViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun ReportMainScreen(
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
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var selectedFilter by rememberSaveable { mutableStateOf("Month") } // Default to "Month"

//    val firstDayOfMonth = selectedDate.withDayOfMonth(1)  // 當月第一天
//    val lastDayOfMonth = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth())  // 當月最後一天

    val startMonthDateMillis: Long
    val endMonthDateMillis: Long

// 根據 selectedFilter 計算要查詢的日期範圍
    when (selectedFilter) {
        "Day" -> {
            startMonthDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
            endMonthDateMillis = selectedDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        }
        "Month" -> {
            val firstDayOfMonth = selectedDate.withDayOfMonth(1)
            val lastDayOfMonth = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth())
            startMonthDateMillis = firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
            endMonthDateMillis = lastDayOfMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        }
        "Year" -> {
            val firstDayOfYear = selectedDate.withDayOfYear(1)
            val lastDayOfYear = selectedDate.withDayOfYear(selectedDate.lengthOfYear())
            startMonthDateMillis = firstDayOfYear.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
            endMonthDateMillis = lastDayOfYear.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        }
        else -> {
            // 自訂範圍，這裡假設是選擇月
            val firstDayOfMonth = selectedDate.withDayOfMonth(1)
            val lastDayOfMonth = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth())
            startMonthDateMillis = firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
            endMonthDateMillis = lastDayOfMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        }
    }

    val recordTotal by recordViewModel.getRecordsByDateRange(
        startMonthDateMillis,
        endMonthDateMillis,
    ).observeAsState(emptyList())

    val userSettings by userSettingsViewModel.userSettings.observeAsState()
    LaunchedEffect(Unit) {
        userSettingsViewModel.loadUserSettings()
    }

    var showDialog by rememberSaveable { mutableStateOf(false) }
    Log.d("recordTotal", "$recordTotal")
    Scaffold(
        topBar = {
            ReportMainScreenHeader(
                navController,
                selectedDate,
                selectedFilter,
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
                TabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text(text = "類別") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text(stringResource(id = R.string.list)) }
                    )
                }
                when (selectedTabIndex) {
                    0 -> {
                        ReportCategoryScreen(recordTotal, mainCategoryViewModel, userSettings)
                    }

                    1 -> {

                    }
                }
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportMainScreenHeader(
    navController: NavHostController,
    localDate: LocalDate,
    selectedFilter: String,
    onClickNext: () -> Unit,
    onClickPrev: () -> Unit,
    onFilterClick: () -> Unit,
) {
    val currentLocale = Locale.getDefault()
    val dateFormatter = when (selectedFilter) {
        "Day" -> DateTimeFormatter.ofPattern("MMMM d, yyyy", currentLocale)
        "Month" -> DateTimeFormatter.ofPattern("MMMM, yyyy", currentLocale)
        "Year" -> DateTimeFormatter.ofPattern("yyyy", currentLocale)
        "Custom" -> DateTimeFormatter.ofPattern("MMM, yyyy ~ MMM, yyyy", currentLocale)
        else -> DateTimeFormatter.ofPattern("MMMM, yyyy", currentLocale)
    }
    val formattedDate = localDate.format(dateFormatter)
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectedFilter != "Custom") {
                    IconButton(onClick = onClickPrev) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous period")
                    }
                }
                Text(text = formattedDate)
                if (selectedFilter != "Custom") {
                    IconButton(onClick = onClickNext) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next period")
                    }
                }
            }
        },
        actions = {
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = "Filter Options"
                )
            }
        }
    )
}

@Composable
fun FilterDialog(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "選擇日期範圍")
        },
        text = {
            Column {
                val filters = listOf("Day", "Month", "Year", "Custom")
                filters.forEach { filter ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFilterSelected(filter) }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = filter)
                        Spacer(modifier = Modifier.weight(1f))
                        if (filter == selectedFilter) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("確認")
            }
        }
    )
}


enum class DateFilterOption {
    DAY, MONTH, YEAR, CUSTOM
}