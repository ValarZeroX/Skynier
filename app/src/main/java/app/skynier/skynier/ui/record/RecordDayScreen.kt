package app.skynier.skynier.ui.record

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import app.skynier.skynier.R
import app.skynier.skynier.database.entities.AccountEntity
import app.skynier.skynier.database.entities.RecordEntity
import app.skynier.skynier.database.entities.SubCategoryEntity
import app.skynier.skynier.database.entities.UserSettingsEntity
import app.skynier.skynier.library.SharedOptions
import app.skynier.skynier.library.textColor
import app.skynier.skynier.ui.layouts.CustomCalendar
import app.skynier.skynier.ui.theme.Gray
import app.skynier.skynier.viewmodels.AccountViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import app.skynier.skynier.viewmodels.UserSettingsViewModel
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun RecordDayScreen(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    recordsDay: List<RecordEntity>,
    recordsMonth: Map<Int, Int>,
    subCategoryViewModel: SubCategoryViewModel,
    userSettingsViewModel: UserSettingsViewModel,
    accountViewModel: AccountViewModel,
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
    val subCategoriesByMainCategory by subCategoryViewModel.subCategoriesByMainCategory.observeAsState(
        emptyMap()
    )
    val userSettings by userSettingsViewModel.userSettings.observeAsState()
    LaunchedEffect(Unit) {
        subCategoryViewModel.loadAllSubCategoriesAndGroupByMainCategory()
        userSettingsViewModel.loadUserSettings()
    }

    val accounts by accountViewModel.accounts.observeAsState(emptyList())


    // 合併轉出(type 3)和轉入(type 4)的記錄
    val mergedTransferRecords = remember(recordsDay) {
        val transfersOut = recordsDay.filter { it.type == 3 }
        val transfersIn = recordsDay.filter { it.type == 4 }
        transfersOut.mapNotNull { outRecord ->
            transfersIn.find { inRecord ->
                // 可以根據具體條件匹配，比如相同的金額和日期
                inRecord.datetime == outRecord.datetime
            }?.let { inRecord ->
                mergeTransferRecords(outRecord, inRecord) // 如果找到匹配的轉入記錄，合併
            }
        }
    }

    var showRecordDialog by rememberSaveable { mutableStateOf(false) }
    var selectedRecord by remember { mutableStateOf<RecordEntity?>(null) }

    var showRecordMergeDialog by rememberSaveable { mutableStateOf(false) }
    var selectedMergeRecord by remember { mutableStateOf<MergedTransferEntity?>(null) }

    Column {
        CustomCalendar(
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            startFromSunday = true,
            highlightDays = highlightDays
        )
        HorizontalDivider()
        LazyColumn {
            items(mergedTransferRecords) { mergedRecord ->
                DisplayMergedTransferRecord(
                    mergedRecord,
                    accounts,
                    subCategoriesByMainCategory,
                    userSettings,
                    onClick = {
                        selectedMergeRecord = mergedRecord
                        showRecordMergeDialog = true
                    }
                )
            }
            // 顯示其他類型的記錄 (type 1 和 type 2)
            items(recordsDay.filter { it.type == 1 || it.type == 2 }) { record ->
                DisplaySingleRecord(
                    record,
                    subCategoriesByMainCategory,
                    accounts,
                    userSettings,
                    onClick = {
                        selectedRecord = record
                        showRecordDialog = true
                    }
                )
            }
        }
        // 顯示對話框
        if (showRecordMergeDialog) {
            RecordMergeDialog(
                record = selectedMergeRecord,
                onDismissRequest = { showRecordMergeDialog = false },
                userSettings,
                subCategoriesByMainCategory,
                accounts
            )
        }
        if (showRecordDialog) {
            RecordDialog(
                record = selectedRecord,
                onDismissRequest = { showRecordDialog = false },
                userSettings,
                subCategoriesByMainCategory,
                accounts
            )
        }
    }
}

@Composable
fun RecordMergeDialog(
    record: MergedTransferEntity?,
    onDismissRequest: () -> Unit,
    userSettings: UserSettingsEntity?,
    subCategoriesByMainCategory: Map<Int, List<SubCategoryEntity>>,
    accounts: List<AccountEntity>,
) {
    if (record != null) {
        val decimalFormat = DecimalFormat("#,###.##")
        val formattedValueOut = decimalFormat.format(record.outAmount)
        val formattedValueIn = decimalFormat.format(record.inAmount)
        val formattedValueFees = decimalFormat.format(record.fees)
        val subCategoriesForThisMainCategory =
            subCategoriesByMainCategory[record.mainCategoryId] ?: emptyList()
        val matchingSubCategory =
            subCategoriesForThisMainCategory.find { it.subCategoryId == record.subCategoryId }

        val dateTime =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(record.datetime), ZoneId.systemDefault())

        // 定义日期时间格式
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
        val newDateTime = dateTime.format(formatter)

        val categoryName = when (record.categoryId) {
            1 -> stringResource(id = R.string.expense)
            2 -> stringResource(id = R.string.income)
            3 -> stringResource(id = R.string.transfer)
            else -> {
                ""
            }
        }
        matchingSubCategory?.let { category ->
            val recordIcon = SharedOptions.iconMap[category.subCategoryIcon]
            val backgroundColor =
                Color(android.graphics.Color.parseColor("#${category.subCategoryBackgroundColor}"))
            val iconColor =
                Color(android.graphics.Color.parseColor("#${category.subCategoryIconColor}"))

            var textColorDialog = MaterialTheme.colorScheme.onBackground
            userSettings?.let { textColorDialog = textColor(it.textColor, record.categoryId) }

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
            Dialog(onDismissRequest = onDismissRequest) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(5.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // 左上角的關閉圖示
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                                    .clickable {
                                        onDismissRequest()
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close, // 假設使用默認的 Close 圖標
                                    contentDescription = "Close",
                                    modifier = Modifier.size(20.dp),
                                    tint = Gray
                                )
                            }

                            // 右上角的刪除和編輯圖示
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(14.dp) // 添加間距
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete, // 刪除圖標
                                        contentDescription = "Delete",
                                        modifier = Modifier.size(20.dp),
                                        tint = Gray
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.Edit, // 編輯圖標
                                        contentDescription = "Edit",
                                        modifier = Modifier.size(20.dp),
                                        tint = Gray
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }

                            // Icon 和 Text 水平居中
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
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
                                Spacer(modifier = Modifier.height(8.dp)) // 添加圖標與文本之間的間距
                                Text(
                                    text = record.name.ifEmpty { displayName },
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        HorizontalDivider()
                        Column(modifier = Modifier.padding(18.dp)) {
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
                                    text = record.outCurrency,
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
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                accounts.find { it.accountId == record.outAccountId }?.let {
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
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "⮕", color = Gray)
                                Spacer(modifier = Modifier.width(4.dp))
                                accounts.find { it.accountId == record.inAccountId }?.let {
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
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = record.outCurrency,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Gray
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$$formattedValueOut",
                                    color = textColorDialog,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "⮕", color = Gray)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = record.inCurrency,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Gray
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$$formattedValueIn",
                                    color = textColorDialog,
                                    fontSize = 16.sp
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth() // 让 Row 占满整个宽度
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = newDateTime,
                                    fontSize = 12.sp,
                                    color = Gray,
                                )
                            }
                            HorizontalDivider()
                            Text(
                                text = record.description.ifEmpty { "備註" },
                                color = Gray,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecordDialog(
    record: RecordEntity?,
    onDismissRequest: () -> Unit,
    userSettings: UserSettingsEntity?,
    subCategoriesByMainCategory: Map<Int, List<SubCategoryEntity>>,
    accounts: List<AccountEntity>,
) {
    if (record != null) {
        val decimalFormat = DecimalFormat("#,###.##")
        val formattedValue = decimalFormat.format(record.amount)
        val subCategoriesForThisMainCategory =
            subCategoriesByMainCategory[record.mainCategoryId] ?: emptyList()
        val matchingSubCategory =
            subCategoriesForThisMainCategory.find { it.subCategoryId == record.subCategoryId }

        val dateTime =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(record.datetime), ZoneId.systemDefault())

        // 定义日期时间格式
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
        val newDateTime = dateTime.format(formatter)

        val categoryName = when (record.categoryId) {
            1 -> stringResource(id = R.string.expense)
            2 -> stringResource(id = R.string.income)
            3 -> stringResource(id = R.string.transfer)
            else -> {
                ""
            }
        }
        matchingSubCategory?.let { category ->
            val recordIcon = SharedOptions.iconMap[category.subCategoryIcon]
            val backgroundColor =
                Color(android.graphics.Color.parseColor("#${category.subCategoryBackgroundColor}"))
            val iconColor =
                Color(android.graphics.Color.parseColor("#${category.subCategoryIconColor}"))

            var textColorDialog = MaterialTheme.colorScheme.onBackground
            userSettings?.let { textColorDialog = textColor(it.textColor, record.categoryId) }

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
            Dialog(onDismissRequest = onDismissRequest) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(5.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // 左上角的關閉圖示
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                                    .clickable {
                                        onDismissRequest()
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close, // 假設使用默認的 Close 圖標
                                    contentDescription = "Close",
                                    modifier = Modifier.size(20.dp),
                                    tint = Gray
                                )
                            }

                            // 右上角的刪除和編輯圖示
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(14.dp) // 添加間距
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete, // 刪除圖標
                                        contentDescription = "Delete",
                                        modifier = Modifier.size(20.dp),
                                        tint = Gray
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.Edit, // 編輯圖標
                                        contentDescription = "Edit",
                                        modifier = Modifier.size(20.dp),
                                        tint = Gray
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }

                            // Icon 和 Text 水平居中
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
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
                                Spacer(modifier = Modifier.height(8.dp)) // 添加圖標與文本之間的間距
                                Text(
                                    text = record.name.ifEmpty { displayName },
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        HorizontalDivider()
                        Column(modifier = Modifier.padding(18.dp)) {
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
                                    color = textColorDialog,
                                    fontSize = 16.sp
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth() // 让 Row 占满整个宽度
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = newDateTime,
                                    fontSize = 12.sp,
                                    color = Gray,
                                )
                            }
                            HorizontalDivider()
                            Text(
                                text = record.description.ifEmpty { "備註" },
                                color = Gray,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayMergedTransferRecord(
    mergedRecord: MergedTransferEntity,
    accounts: List<AccountEntity>,
    subCategoriesByMainCategory: Map<Int, List<SubCategoryEntity>>,
    userSettings: UserSettingsEntity?,
    onClick: () -> Unit
) {
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
        userSettings?.let { textColorMerge = textColor(it.textColor, mergedRecord.categoryId) }

        val categoryName = when (mergedRecord.categoryId) {
            1 -> stringResource(id = R.string.expense)
            2 -> stringResource(id = R.string.income)
            3 -> stringResource(id = R.string.transfer)
            else -> {
                ""
            }
        }
        ListItem(
            modifier = Modifier.clickable { onClick() },
            headlineContent = { Text(text = mergedRecord.name) },
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
                    Row {
                        Box {
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                accounts.find { it.accountId == mergedRecord.outAccountId }?.let {
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
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "⮕", color = Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Box {
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(4.dp))
                                accounts.find { it.accountId == mergedRecord.inAccountId }?.let {
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
        HorizontalDivider()
    }
}

@Composable
fun DisplaySingleRecord(
    record: RecordEntity,
    subCategoriesByMainCategory: Map<Int, List<SubCategoryEntity>>,
    accounts: List<AccountEntity>,
    userSettings: UserSettingsEntity?,
    onClick: () -> Unit // 添加 onClick 參數來處理點擊事件
) {
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

        ListItem(
            modifier = Modifier.clickable { onClick() },
            headlineContent = { Text(text = record.name) },
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
        HorizontalDivider()
    }
}

data class MergedTransferEntity(
    val outRecordId: Int,
    val inRecordId: Int,
    val outCurrency: String,   // 轉出幣種
    val inCurrency: String,    // 轉入幣種
    val outAccountId: Int,     // 轉出帳戶ID
    val inAccountId: Int,      // 轉入帳戶ID
    val outAmount: Double,     // 轉出金額
    val inAmount: Double,      // 轉入金額
    val outType: Int,          // 轉出類型
    val inType: Int,           // 轉入類型
    val name: String,    // 合併名稱
    val description: String, // 合併描述
    val categoryId: Int,
    val mainCategoryId: Int,
    val subCategoryId: Int,
    val datetime: Long,
    val fees: Double,
)

fun mergeTransferRecords(
    outRecord: RecordEntity, // 轉出記錄
    inRecord: RecordEntity   // 轉入記錄
): MergedTransferEntity {
    return MergedTransferEntity(
        outRecordId = outRecord.recordId,
        inRecordId = inRecord.recordId,
        outCurrency = outRecord.currency, // 保留轉出幣種
        inCurrency = inRecord.currency,   // 保留轉入幣種
        outAccountId = outRecord.accountId, // 保留轉出帳戶ID
        inAccountId = inRecord.accountId,   // 保留轉入帳戶ID
        outAmount = outRecord.amount, // 保留轉出金額
        inAmount = inRecord.amount,   // 保留轉入金額
        outType = outRecord.type,     // 保留轉出類型
        inType = inRecord.type,       // 保留轉入類型
        name = outRecord.name, // 合併名稱
        description = outRecord.description, // 合併描述,
        categoryId = outRecord.categoryId,
        mainCategoryId = outRecord.mainCategoryId,
        subCategoryId = outRecord.subCategoryId,
        datetime = outRecord.datetime,
        fees = outRecord.fee
    )
}