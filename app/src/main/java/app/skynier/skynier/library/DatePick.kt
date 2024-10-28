package app.skynier.skynier.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.skynier.skynier.R
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    selectedDate: Long?,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )


    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val selectedDateMillis = datePickerState.selectedDateMillis
                onDateSelected(selectedDateMillis)
                onDismiss()
            }) {
                Text(stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
//            title = {
//                Box(
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    Text(
//                        text = stringResource(id = R.string.select_start_date)
//                    )
//                }
//            },
            modifier = Modifier
                .fillMaxWidth()
                .height(514.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val selectedDateMillis = datePickerState.selectedDateMillis
                if (selectedDateMillis != null) {
                    onDateSelected(selectedDateMillis)
                }
                onDismiss()
            }) {
                Text(stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(514.dp)
        )
    }
}

@Composable
fun MonthPickerDialog(
    initialYear: Int,
    initialMonth: Int,
    onMonthSelected: (year: Int, month: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val yearRange = (1900..2100).toList()
    var selectedYear by rememberSaveable { mutableIntStateOf(initialYear) }
    var selectedMonth by rememberSaveable { mutableIntStateOf(initialMonth) }
    var showYearDropdown by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val months = context.resources.getStringArray(R.array.months)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.select_date))
        },
        text = {
            Column {
                // 年份选择
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showYearDropdown = !showYearDropdown }
                ) {
                    Text(text = "$selectedYear")
                    Icon(
                        imageVector = if (showYearDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
                if (showYearDropdown) {
                    val gridState = rememberLazyGridState()

                    LaunchedEffect(Unit) {
                        val initialIndex = yearRange.indexOf(selectedYear)
                        if (initialIndex != -1) {
                            val targetIndex = maxOf(initialIndex - 3, 0)
                            gridState.scrollToItem(targetIndex)
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        state = gridState,
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(yearRange.size) { index ->
                            val year = yearRange[index]
                            val isSelectedYear = year == selectedYear
                            Text(
                                text = year.toString(),
                                color = if (isSelectedYear) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = if (isSelectedYear) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        selectedYear = year
                                        showYearDropdown = false
                                    }
                                    .padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        content = {
                            itemsIndexed(months) { index, monthName ->
                                val isSelectedMonth = index == selectedMonth
                                Text(
                                    text = monthName,
                                    color = if (isSelectedMonth) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = if (isSelectedMonth) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            selectedMonth = index
                                            onMonthSelected(selectedYear, selectedMonth)
                                            onDismiss()
                                        }
                                        .padding(10.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}

@Composable
fun YearPickerDialog(
    initialYear: Int,
    onYearSelected: (year: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val minYear = 1900
    val maxYear = 2100
    val yearRange = (minYear..maxYear).toList()
    var selectedYear by rememberSaveable { mutableIntStateOf(initialYear) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.select_year))
        },
        text = {
            val gridState = rememberLazyGridState()

            LaunchedEffect(Unit) {
                val initialIndex = yearRange.indexOf(selectedYear)
                if (initialIndex != -1) {
                    val targetIndex = maxOf(initialIndex - 3, 0)
                    gridState.scrollToItem(targetIndex)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                state = gridState,
                modifier = Modifier.height(300.dp)
            ) {
                items(yearRange.size) { index ->
                    val year = yearRange[index]
                    val isSelectedYear = year == selectedYear
                    Text(
                        text = year.toString(),
                        color = if (isSelectedYear) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .background(
                                color = if (isSelectedYear) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedYear = year
                                onYearSelected(year)
                                onDismiss()
                            }
                            .padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}