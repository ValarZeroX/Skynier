package app.skynier.skynier.ui.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import app.skynier.skynier.R
import app.skynier.skynier.library.MonthPickerDialog
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
fun RecordMainScreen(
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

    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var searchText by rememberSaveable { mutableStateOf("") }
//    val startDateMillis = firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
//    val endDateMillis = lastDayOfMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000

    val startOfDay = selectedDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
    val endOfDay =
        selectedDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000

    val firstDayOfMonth = selectedDate.withDayOfMonth(1)  // 當月第一天
    val lastDayOfMonth = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth())  // 當月最後一天

    val startMonthDateMillis =
        firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
    val endMonthDateMillis =
        lastDayOfMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000


    val recordsMonth by recordViewModel.getDateSerialNumberMapByDateRange(
        startMonthDateMillis,
        endMonthDateMillis,
        searchText
    ).observeAsState(initial = emptyMap())

    val recordsDay by recordViewModel.getRecordsByDateRange(startOfDay, endOfDay)
        .observeAsState(emptyList())
    val filteredRecordsDay = recordsDay.filter {
        it.name.contains(searchText, ignoreCase = true) ||
                it.description.contains(searchText, ignoreCase = true)
    }
    //列表
    val recordTotal by recordViewModel.getRecordsByDateRange(
        startMonthDateMillis,
        endMonthDateMillis,
    ).observeAsState(emptyList())

    val filteredRecords = recordTotal.filter {
        it.name.contains(searchText, ignoreCase = true) ||
                it.description.contains(searchText, ignoreCase = true)
    }


    val userSettings by userSettingsViewModel.userSettings.observeAsState()
    LaunchedEffect(Unit) {
        subCategoryViewModel.loadAllSubCategoriesAndGroupByMainCategory()
        userSettingsViewModel.loadUserSettings()
    }

    val accounts by accountViewModel.accounts.observeAsState(emptyList())
//    Log.d("recordsMonth", "$recordsMonth")
//    Log.d("recordTotal", "$recordTotal")
    Scaffold(
        topBar = {
            RecordMainScreenHeader(
                navController,
                selectedDate,
//                onClickPrev = {
//                    selectedDate = selectedDate.minusMonths(1)
//                },
//                onClickNext = {
//                    selectedDate = selectedDate.plusMonths(1)
//                },
                onClickPrev = {
                    val previousMonthDate = selectedDate.minusMonths(1).withDayOfMonth(1)
                    selectedDate = previousMonthDate
                },
                onClickNext = {
                    val nextMonthDate = selectedDate.plusMonths(1).withDayOfMonth(1)
                    selectedDate = nextMonthDate
                },
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onDateSelected = { newDate ->
                    selectedDate = newDate
                },
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text(stringResource(id = R.string.calendar)) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text(stringResource(id = R.string.list)) }
                    )
                }
                when (selectedTabIndex) {
                    0 -> {
                        Column {
                            RecordDashboard(recordTotal,userSettings)
                            HorizontalDivider()
                            RecordDayScreen(
                                selectedDate,
                                onDateSelected = { newDate ->
                                    selectedDate = newDate // 在上層組件內更新日期狀態
                                },
                                recordsDay = filteredRecordsDay,
                                recordsMonth,
                                subCategoryViewModel,
                                userSettings,
                                accounts,
                                navController,
                                recordViewModel
                            )
                        }
                    }

                    1 -> {
                        RecordDashboard(recordTotal,userSettings)
                        HorizontalDivider()
                        RecordDayListScreen(
                            recordTotal = filteredRecords,
                            userSettings,
                            subCategoryViewModel,
                            accounts,
                            navController,
                            recordViewModel
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordMainScreenHeader(
    navController: NavHostController,
    localDate: LocalDate,
    onClickNext: () -> Unit,
    onClickPrev: () -> Unit,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    var showSearchBar by rememberSaveable { mutableStateOf(false) }

    val currentLocale = Locale.getDefault()
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM, yyyy", currentLocale)
    val formattedDate = localDate.format(dateFormatter)
    var showMonthPicker by rememberSaveable { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            if (showSearchBar) {
                BasicTextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    singleLine = true,
                    modifier = Modifier
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "Search Icon",
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (searchText.isEmpty()) {
                                    Text(
                                        text = stringResource(id = R.string.enter_search_content),
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                                innerTextField()
                            }
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = {
                                    onSearchTextChange("")
                                    showSearchBar = false
                                }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Close Search")
                                }
                            }
                        }
                    }
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClickPrev) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
                    }
                    Box(
                        modifier = Modifier.clickable {
                            showMonthPicker = true
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
                    IconButton(onClick = onClickNext) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
                    }
                }
            }
        },
        navigationIcon = {
            if (showSearchBar) {
                IconButton(onClick = { showSearchBar = false }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (!showSearchBar) {
                Row {
                    IconButton(onClick = { showSearchBar = true }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }
                }
            }
        }
    )
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
}