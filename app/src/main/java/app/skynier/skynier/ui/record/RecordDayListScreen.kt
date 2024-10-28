package app.skynier.skynier.ui.record

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import app.skynier.skynier.R
import app.skynier.skynier.database.entities.AccountEntity
import app.skynier.skynier.database.entities.RecordEntity
import app.skynier.skynier.database.entities.UserSettingsEntity
import app.skynier.skynier.library.SharedOptions
import app.skynier.skynier.library.textColor
import app.skynier.skynier.ui.theme.Blue
import app.skynier.skynier.ui.theme.Gray
import app.skynier.skynier.ui.theme.Red
import app.skynier.skynier.viewmodels.RecordViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun RecordDayListScreen(
    recordTotal: List<RecordEntity>,
    userSettings: UserSettingsEntity?,
    subCategoryViewModel: SubCategoryViewModel,
    accounts: List<AccountEntity>,
    navController: NavHostController,
    recordViewModel: RecordViewModel,
) {
    val mergedTransferRecords = remember(recordTotal) {
        val transfersOut = recordTotal.filter { it.type == 3 }
        val transfersIn = recordTotal.filter { it.type == 4 }
        transfersOut.mapNotNull { outRecord ->
            transfersIn.find { inRecord ->
                // 可以根據具體條件匹配，比如相同的金額和日期
                inRecord.datetime == outRecord.datetime
            }?.let { inRecord ->
                mergeTransferRecords(outRecord, inRecord) // 如果找到匹配的轉入記錄，合併
            }
        }
    }

    val recordByMergeDate = mergedTransferRecords.groupBy { record ->
        // 將 timestamp 轉換為 LocalDate
        val date = Instant.ofEpochMilli(record.datetime)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        date
    }


    // 日期格式化器
    val dayFormatter = DateTimeFormatter.ofPattern("MMM d,")
    val weekFormatter = DateTimeFormatter.ofPattern("EE", Locale.getDefault())
    val monthFormatter = DateTimeFormatter.ofPattern("yyyy.MM")

    // 將紀錄按日期分組
    val recordsByDate = recordTotal.groupBy { record ->
        // 將 timestamp 轉換為 LocalDate
        val date = Instant.ofEpochMilli(record.datetime)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        date
    }

    val subCategoriesByMainCategory by subCategoryViewModel.subCategoriesByMainCategory.observeAsState(
        emptyMap()
    )
    // 排序日期
    val sortedMergeDates = recordByMergeDate.keys.sorted()
    val sortedDates = recordsByDate.keys.sorted()
    val combinedDates = (sortedDates + sortedMergeDates).distinct().sorted()

    var showRecordDialog by rememberSaveable { mutableStateOf(false) }
    var selectedRecord by remember { mutableStateOf<RecordEntity?>(null) }

    var showRecordMergeDialog by rememberSaveable { mutableStateOf(false) }
    var selectedMergeRecord by remember { mutableStateOf<MergedTransferEntity?>(null) }
    LazyColumn {
        combinedDates.forEach { date ->
            // 格式化日期顯示
            val formattedDate = "${date.format(dayFormatter)} ${date.format(monthFormatter)}"

            // 顯示日期標題
            item {
                Row {
                    Text(
                        text = date.format(dayFormatter),
                        fontSize = 16.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        modifier = Modifier.padding(
                            start = 10.dp,
                            end = 5.dp,
                            top = 3.dp,
                            bottom = 3.dp
                        )
                    )
                    Text(
                        text = date.format(weekFormatter),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(
                            start = 5.dp,
                            end = 5.dp,
                            top = 3.dp,
                            bottom = 3.dp
                        ),
                        color = when (date.dayOfWeek) {
                            DayOfWeek.SATURDAY -> Blue
                            DayOfWeek.SUNDAY -> Red
                            else -> Color.Unspecified
                        },
                    )
                }
                HorizontalDivider()
            }
            items(recordByMergeDate[date] ?: emptyList()) { mergedRecord ->
                val subCategoriesForThisMainCategory =
                    subCategoriesByMainCategory[mergedRecord.mainCategoryId] ?: emptyList()
                val matchingSubCategory =
                    subCategoriesForThisMainCategory.find { it.subCategoryId == mergedRecord.subCategoryId }
                val decimalFormat = DecimalFormat("#,###.##")
                val formattedValueOut = decimalFormat.format(mergedRecord.outAmount)
                val formattedValueIn = decimalFormat.format(mergedRecord.inAmount)
                val formattedValueFees = decimalFormat.format(mergedRecord.fees)
                matchingSubCategory?.let { category ->
                    val recordIcon = SharedOptions.iconMap[category.subCategoryIcon]
                    val backgroundColor =
                        Color(android.graphics.Color.parseColor("#${category.subCategoryBackgroundColor}"))
                    val iconColor =
                        Color(android.graphics.Color.parseColor("#${category.subCategoryIconColor}"))

                    var textColorMerge = MaterialTheme.colorScheme.onBackground
                    userSettings?.let {
                        textColorMerge = textColor(it.textColor, mergedRecord.categoryId)
                    }

                    val categoryName = when (mergedRecord.categoryId) {
                        1 -> stringResource(id = R.string.expense)
                        2 -> stringResource(id = R.string.income)
                        3 -> stringResource(id = R.string.transfer)
                        else -> {
                            ""
                        }
                    }
                    ListItem(
                        modifier = Modifier.clickable {
                            selectedMergeRecord = mergedRecord
                            showRecordMergeDialog = true
                        },
                        headlineContent = { Text(text = mergedRecord.name) },
                        supportingContent = {
                            Column {
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Filled.Payments,
                                        contentDescription = "Localized description",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = mergedRecord.outCurrency,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Gray
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$$formattedValueFees (${stringResource(id = R.string.fees)})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Filled.Sell,
                                        contentDescription = "Localized description",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = categoryName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row {
                                    Box {
                                        Row(
                                            modifier = Modifier.padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            accounts.find { it.accountId == mergedRecord.outAccountId }
                                                ?.let {
                                                    val accountIcon =
                                                        SharedOptions.iconMap[it.accountIcon]
                                                    if (accountIcon != null) {
                                                        Icon(
                                                            accountIcon.icon,
                                                            contentDescription = "Localized description",
                                                            modifier = Modifier.size(16.dp),
                                                            tint = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = it.accountName,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "⮕", color = Gray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box {
                                        Row(
                                            modifier = Modifier.padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            accounts.find { it.accountId == mergedRecord.inAccountId }
                                                ?.let {
                                                    val accountIcon =
                                                        SharedOptions.iconMap[it.accountIcon]
                                                    if (accountIcon != null) {
                                                        Icon(
                                                            accountIcon.icon,
                                                            contentDescription = "Localized description",
                                                            modifier = Modifier.size(16.dp),
                                                            tint = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = it.accountName,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                        }
                                    }
                                }
                                Text(
                                    text = mergedRecord.description,
                                    color = Gray,
                                    maxLines = 2, // 限制最多顯示兩行
                                    overflow = TextOverflow.Ellipsis // 超過兩行時用省略號顯示
                                )
                            }
                        },
                        trailingContent = {
                            Column(horizontalAlignment = Alignment.End) {
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = mergedRecord.outCurrency,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Gray
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$$formattedValueOut",
                                        color = textColorMerge,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(backgroundColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                recordIcon?.let { iconData ->
                                    Icon(
                                        imageVector = iconData.icon,
                                        contentDescription = category.subCategoryNameKey,
                                        modifier = Modifier.size(20.dp),
                                        tint = iconColor
                                    )
                                } ?: run {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = "Default icon",
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.Gray
                                    )
                                }
                            }
                        }
                    )
                }
            }
            // 顯示該日期的所有紀錄
            items(recordsByDate[date]?.filter { it.type == 1 || it.type == 2 }
                ?: emptyList()) { record ->
                val subCategoriesForThisMainCategory =
                    subCategoriesByMainCategory[record.mainCategoryId] ?: emptyList()
                val matchingSubCategory =
                    subCategoriesForThisMainCategory.find { it.subCategoryId == record.subCategoryId }
                matchingSubCategory?.let { category ->
                    val recordIcon = SharedOptions.iconMap[category.subCategoryIcon]
                    val backgroundColor =
                        Color(android.graphics.Color.parseColor("#${category.subCategoryBackgroundColor}"))
                    val iconColor =
                        Color(android.graphics.Color.parseColor("#${category.subCategoryIconColor}"))
                    val decimalFormat = DecimalFormat("#,###.##")
                    val formattedValue = decimalFormat.format(record.amount)

                    var textColor = MaterialTheme.colorScheme.onBackground
                    userSettings?.let { textColor = textColor(it.textColor, record.categoryId) }

                    val categoryName = when (record.categoryId) {
                        1 -> stringResource(id = R.string.expense)
                        2 -> stringResource(id = R.string.income)
                        3 -> stringResource(id = R.string.transfer)
                        else -> {
                            ""
                        }
                    }

                    val context = LocalContext.current
                    val resourceId =
                        context.resources.getIdentifier(
                            category.subCategoryNameKey,
                            "string",
                            context.packageName
                        )
                    val displayName = if (resourceId != 0) {
                        context.getString(resourceId) // 如果語系字串存在，顯示語系的值
                    } else {
                        category.subCategoryNameKey // 如果語系字串不存在，顯示原始值
                    }
                    ListItem(
                        modifier = Modifier.clickable {
                            selectedRecord = record
                            showRecordDialog = true
                        },
                        headlineContent = { Text(text = record.name.ifEmpty { displayName }) },
                        supportingContent = {
                            Column {
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Filled.Sell,
                                        contentDescription = "Localized description",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = categoryName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box {
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        accounts.find { it.accountId == record.accountId }?.let {
                                            val accountIcon = SharedOptions.iconMap[it.accountIcon]
                                            if (accountIcon != null) {
                                                Icon(
                                                    accountIcon.icon,
                                                    contentDescription = "Localized description",
                                                    modifier = Modifier.size(16.dp),
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = it.accountName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = record.description,
                                    color = Gray,
                                    maxLines = 2, // 限制最多顯示兩行
                                    overflow = TextOverflow.Ellipsis // 超過兩行時用省略號顯示
                                )
                            }
                        },
                        trailingContent = {
                            Column(horizontalAlignment = Alignment.End) {
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = record.currency,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Gray
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$$formattedValue",
                                        color = textColor,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(backgroundColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                recordIcon?.let { iconData ->
                                    Icon(
                                        imageVector = iconData.icon,
                                        contentDescription = category.subCategoryNameKey,
                                        modifier = Modifier.size(20.dp),
                                        tint = iconColor
                                    )
                                } ?: run {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = "Default icon",
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.Gray
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
    if (showRecordMergeDialog) {
        RecordMergeDialog(
            record = selectedMergeRecord,
            onDismissRequest = { showRecordMergeDialog = false },
            userSettings,
            subCategoriesByMainCategory,
            accounts,
            navController,
            recordViewModel
        )
    }
    if (showRecordDialog) {
        RecordDialog(
            record = selectedRecord,
            onDismissRequest = { showRecordDialog = false },
            userSettings,
            subCategoriesByMainCategory,
            accounts,
            navController,
            recordViewModel
        )
    }
}