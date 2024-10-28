package app.skynier.skynier.ui.report

//import android.graphics.Color
import android.graphics.Typeface
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import app.skynier.skynier.R
import app.skynier.skynier.database.entities.AccountEntity
import app.skynier.skynier.database.entities.CurrencyEntity
import app.skynier.skynier.database.entities.MainCategoryEntity
import app.skynier.skynier.database.entities.RecordEntity
import app.skynier.skynier.database.entities.SubCategoryEntity
import app.skynier.skynier.database.entities.UserSettingsEntity
import app.skynier.skynier.library.SharedOptions
import app.skynier.skynier.library.textColorBasedOnAmount
import app.skynier.skynier.ui.record.MergedTransferEntity
import app.skynier.skynier.ui.record.RecordDialog
import app.skynier.skynier.ui.record.RecordMergeDialog
import app.skynier.skynier.ui.record.mergeTransferRecords
import app.skynier.skynier.ui.theme.Gray
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.RecordViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ReportCategoryScreen(
    recordTotal: List<RecordEntity>,
    mainCategoryViewModel: MainCategoryViewModel,
    subCategoryViewModel: SubCategoryViewModel,
    userSettings: UserSettingsEntity?,
    currencyList: List<CurrencyEntity>,
    navController: NavHostController,
    accounts: List<AccountEntity>,
    recordViewModel: RecordViewModel,
) {
    val context = LocalContext.current
    val mainCategories by mainCategoryViewModel.mainCategories.observeAsState(emptyList())
    val subCategories by subCategoryViewModel.subCategories.observeAsState(emptyList())
    var selectedMainCategoryId by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedCategoryType by rememberSaveable { mutableIntStateOf(0) }
    var selectSubCategoryId by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        mainCategoryViewModel.loadAllMainCategories()
        if (selectedMainCategoryId != null) {
            subCategoryViewModel.loadSubCategoriesByMainCategoryId(selectedMainCategoryId!!)
        }
    }
    if (recordTotal.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No data available", fontSize = 16.sp)
        }
        return
    }

    // 獲取使用者的主要貨幣代碼
    val primaryCurrencyCode = userSettings?.currency ?: "USD"
    val primaryCurrencyRate =
        currencyList.find { it.currency == primaryCurrencyCode }?.exchangeRate ?: 1.0

    var showReportCategoryListDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        subCategoryViewModel.loadAllSubCategoriesAndGroupByMainCategory()
    }

    if (selectedMainCategoryId == null) {
        val filteredRecords = when (selectedCategoryType) {
            0 -> recordTotal.filter { it.type == 1 } // Expenses
            1 -> recordTotal.filter { it.type == 2 } // Income
            2 -> recordTotal.filter { it.type == 3 } // Transfers
//            3 -> recordTotal.filter { it.type == 4 } // 轉入
            else -> emptyList()
        }
        val convertedRecords = filteredRecords.map { record ->
            val recordCurrencyRate =
                currencyList.find { it.currency == record.currency }?.exchangeRate ?: 1.0
            val convertedAmount = record.amount / recordCurrencyRate * primaryCurrencyRate
            record.copy(amount = convertedAmount) // 使用轉換後的金額創建新記錄
        }
        // 顯示主類別
        val categoryMap = convertedRecords.groupBy { it.mainCategoryId }
        Column {
            ReportCategoryTypeSwitch(
                selectedCategoryType = selectedCategoryType,
                onCategoryTypeSelected = { newType -> selectedCategoryType = newType },
            )
            ReportCategoryPieChart(categoryMap, selectedCategoryType, mainCategories)

            LazyColumn {
                items(categoryMap.entries.toList()) { (mainCategoryId, records) ->
                    val mainCategory = mainCategories.find { it.mainCategoryId == mainCategoryId }
                    // 分類圖示
                    val recordIcon = SharedOptions.iconMap[mainCategory?.mainCategoryIcon]
                    val backgroundColor = mainCategory?.mainCategoryBackgroundColor?.let {
                        Color(android.graphics.Color.parseColor("#$it"))
                    } ?: MaterialTheme.colorScheme.surfaceContainer
                    val iconColor = mainCategory?.mainCategoryIconColor?.let {
                        Color(android.graphics.Color.parseColor("#$it"))
                    } ?: MaterialTheme.colorScheme.onSurface
                    // 分類名稱
                    val categoryName = mainCategory?.let {
                        val resourceId = context.resources.getIdentifier(
                            it.mainCategoryNameKey,
                            "string",
                            context.packageName
                        )
                        if (resourceId != 0) {
                            context.getString(resourceId) // 如果語系字串存在，顯示語系的值
                        } else {
                            it.mainCategoryNameKey // 如果語系字串不存在，顯示原始值
                        }
                    } ?: "Unknown"
                    // 計算這個分類的總金額
                    val totalAmount = records.sumOf { it.amount }

                    val decimalFormat = DecimalFormat("#,###.##")
                    val formattedValue = decimalFormat.format(totalAmount)
                    var textColor =
                        textColorBasedOnAmount(userSettings?.textColor ?: 0, totalAmount)
                    if (selectedCategoryType == 0) {
                        textColor =
                            textColorBasedOnAmount(userSettings?.textColor ?: 0, 0 - totalAmount)
                    }
                    ListItem(
                        modifier = Modifier.clickable {
                            // 點擊主類別顯示對應的子類別
                            selectedMainCategoryId = mainCategoryId
                            subCategoryViewModel.loadSubCategoriesByMainCategoryId(mainCategoryId)
                        },
                        headlineContent = { Text(text = "$categoryName (${records.size})") },
                        trailingContent = {
                            Text(
                                text = "$$formattedValue",
                                color = textColor,
                                fontSize = 14.sp
                            )
                        },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(backgroundColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                recordIcon?.let { iconData ->
                                    if (mainCategory != null) {
                                        Icon(
                                            imageVector = iconData.icon,
                                            contentDescription = mainCategory.mainCategoryNameKey,
                                            modifier = Modifier.size(20.dp),
                                            tint = iconColor
                                        )
                                    }
                                } ?: run {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = "Default icon",
                                        modifier = Modifier.size(18.dp),
                                        tint = androidx.compose.ui.graphics.Color.Gray
                                    )
                                }
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    } else {
        val filteredRecords = when (selectedCategoryType) {
            0 -> recordTotal.filter { it.type == 1 && it.mainCategoryId == selectedMainCategoryId } // Expenses
            1 -> recordTotal.filter { it.type == 2 && it.mainCategoryId == selectedMainCategoryId } // Income
            2 -> recordTotal.filter { it.type == 3 && it.mainCategoryId == selectedMainCategoryId } // 轉出
//            3 -> recordTotal.filter { it.type == 4  && it.mainCategoryId == selectedMainCategoryId} // 轉入
            else -> emptyList()
        }
        val convertedRecords = filteredRecords.map { record ->
            val recordCurrencyRate =
                currencyList.find { it.currency == record.currency }?.exchangeRate ?: 1.0
            val convertedAmount = record.amount / recordCurrencyRate * primaryCurrencyRate
            record.copy(amount = convertedAmount) // 使用轉換後的金額創建新記錄
        }
        // 顯示主類別
        val categoryMap = convertedRecords.groupBy { it.subCategoryId }

        Column {
            Row(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            ) {
                AssistChip(
                    onClick = { selectedMainCategoryId = null },
                    label = { Text(stringResource(id = R.string.back)) },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.ArrowBackIosNew,
                            contentDescription = "back",
                            Modifier.size(AssistChipDefaults.IconSize)
                        )
                    },
                )

            }
            ReportSubCategoryPieChart(categoryMap, selectedCategoryType, subCategories)
            LazyColumn {
                items(categoryMap.entries.toList()) { (subCategoryId, records) ->
                    val subCategory = subCategories.find { it.subCategoryId == subCategoryId }
                    // 分類圖示
                    val recordIcon = SharedOptions.iconMap[subCategory?.subCategoryIcon]
                    val backgroundColor = subCategory?.subCategoryBackgroundColor?.let {
                        Color(android.graphics.Color.parseColor("#$it"))
                    } ?: MaterialTheme.colorScheme.surfaceContainer
                    val iconColor = subCategory?.subCategoryIconColor?.let {
                        Color(android.graphics.Color.parseColor("#$it"))
                    } ?: MaterialTheme.colorScheme.onSurface
                    // 分類名稱
                    val categoryName = subCategory?.let {
                        val resourceId = context.resources.getIdentifier(
                            it.subCategoryNameKey,
                            "string",
                            context.packageName
                        )
                        if (resourceId != 0) {
                            context.getString(resourceId) // 如果語系字串存在，顯示語系的值
                        } else {
                            it.subCategoryNameKey // 如果語系字串不存在，顯示原始值
                        }
                    } ?: "Unknown"
                    // 計算這個分類的總金額
                    val totalAmount = records.sumOf { it.amount }

                    val decimalFormat = DecimalFormat("#,###.##")
                    val formattedValue = decimalFormat.format(totalAmount)
                    var textColor =
                        textColorBasedOnAmount(userSettings?.textColor ?: 0, totalAmount)
                    if (selectedCategoryType == 0) {
                        textColor =
                            textColorBasedOnAmount(userSettings?.textColor ?: 0, 0 - totalAmount)
                    }
                    ListItem(
                        modifier = Modifier.clickable {
                            selectSubCategoryId = subCategoryId
                            showReportCategoryListDialog = true
                        },
                        headlineContent = { Text(text = "$categoryName (${records.size})") },
                        trailingContent = {
                            Text(
                                text = "$$formattedValue",
                                color = textColor,
                                fontSize = 14.sp
                            )
                        },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(backgroundColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                recordIcon?.let { iconData ->
                                    if (subCategory != null) {
                                        Icon(
                                            imageVector = iconData.icon,
                                            contentDescription = subCategory.subCategoryNameKey,
                                            modifier = Modifier.size(20.dp),
                                            tint = iconColor
                                        )
                                    }
                                } ?: run {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = "Default icon",
                                        modifier = Modifier.size(18.dp),
                                        tint = androidx.compose.ui.graphics.Color.Gray
                                    )
                                }
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
            if (showReportCategoryListDialog) {
                ReportCategoryListDialog(
                    recordTotal = recordTotal,
                    mainCategoryViewModel = mainCategoryViewModel,
                    subCategoryViewModel = subCategoryViewModel,
                    userSettings = userSettings,
                    currencyList = currencyList,
                    selectedMainCategoryId = selectedMainCategoryId,
                    selectedCategoryType = selectedCategoryType,
                    selectSubCategoryId = selectSubCategoryId,
                    onDismissRequest = { showReportCategoryListDialog = false },
                    navController = navController,
                    accounts = accounts,
                    recordViewModel = recordViewModel
                )
            }
        }
    }
}

@Composable
fun ReportCategoryListDialog(
    recordTotal: List<RecordEntity>,
    mainCategoryViewModel: MainCategoryViewModel,
    subCategoryViewModel: SubCategoryViewModel,
    userSettings: UserSettingsEntity?,
    currencyList: List<CurrencyEntity>,
    selectedMainCategoryId: Int?,
    selectedCategoryType: Int,
    selectSubCategoryId: Int,
    onDismissRequest: () -> Unit,
    navController: NavHostController,
    accounts: List<AccountEntity>,
    recordViewModel: RecordViewModel
) {
    val subCategoriesByMainCategory by subCategoryViewModel.subCategoriesByMainCategory.observeAsState(
        emptyMap()
    )
    var showRecordDialog by rememberSaveable { mutableStateOf(false) }
    var selectedRecord by remember { mutableStateOf<RecordEntity?>(null) }
    var showRecordMergeDialog by rememberSaveable { mutableStateOf(false) }
    var selectedMergeRecord by remember { mutableStateOf<MergedTransferEntity?>(null) }

    val filteredRecords = when (selectedCategoryType) {
        0 -> recordTotal.filter { it.type == 1 && it.mainCategoryId == selectedMainCategoryId && it.subCategoryId == selectSubCategoryId } // Expenses
        1 -> recordTotal.filter { it.type == 2 && it.mainCategoryId == selectedMainCategoryId && it.subCategoryId == selectSubCategoryId } // Income
        2 -> recordTotal.filter { (it.type == 3 || it.type == 4) && it.mainCategoryId == selectedMainCategoryId && it.subCategoryId == selectSubCategoryId } // 轉出
//            3 -> recordTotal.filter { it.type == 4  && it.mainCategoryId == selectedMainCategoryId} // 轉入
        else -> emptyList()
    }

//    if (selectedCategoryType == 2) {
    val mergedTransferRecords = remember(filteredRecords) {
        val transfersOut = filteredRecords.filter { it.type == 3 }
        val transfersIn = filteredRecords.filter { it.type == 4 }
        transfersOut.mapNotNull { outRecord ->
            transfersIn.find { inRecord ->
                // 可以根據具體條件匹配，比如相同的金額和日期
                inRecord.datetime == outRecord.datetime
            }?.let { inRecord ->
                mergeTransferRecords(outRecord, inRecord) // 如果找到匹配的轉入記錄，合併
            }
        }
    }
    Log.d("mergedTransferRecords", "$mergedTransferRecords")
//    }
    Log.d("filteredRecords", "$filteredRecords")

//    val categoryName = subCategory?.let {
//        val resourceId = context.resources.getIdentifier(
//            it.subCategoryNameKey,
//            "string",
//            context.packageName
//        )
//        if (resourceId != 0) {
//            context.getString(resourceId) // 如果語系字串存在，顯示語系的值
//        } else {
//            it.subCategoryNameKey // 如果語系字串不存在，顯示原始值
//        }
//    } ?: "Unknown"

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .padding(5.dp)
                .heightIn(max = 800.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.account_category),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                HorizontalDivider()
                if (selectedCategoryType == 2) {
                    LazyColumn {
                        items(mergedTransferRecords) { record ->
                            val subCategory =
                                subCategoryViewModel.subCategories.value?.find { it.subCategoryId == record.subCategoryId }
                            val categoryName = subCategory?.let {
                                val resourceId = LocalContext.current.resources.getIdentifier(
                                    it.subCategoryNameKey,
                                    "string",
                                    LocalContext.current.packageName
                                )
                                if (resourceId != 0) {
                                    LocalContext.current.getString(resourceId) // 如果語系字串存在，顯示語系的值
                                } else {
                                    it.subCategoryNameKey // 如果語系字串不存在，顯示原始值
                                }
                            } ?: "Unknown"
                            val recordCurrencyRate =
                                currencyList.find { it.currency == record.outCurrency }?.exchangeRate
                                    ?: 1.0
                            val primaryCurrencyRate =
                                currencyList.find { it.currency == userSettings?.currency }?.exchangeRate
                                    ?: 1.0
                            val convertedAmount =
                                record.outAmount / recordCurrencyRate * primaryCurrencyRate
                            val decimalFormat = DecimalFormat("#,###.##")
                            val formattedValue = decimalFormat.format(convertedAmount)
                            val textColor =
                                textColorBasedOnAmount(
                                    userSettings?.textColor ?: 0,
                                    convertedAmount
                                )
//                            if (selectedCategoryType == 0) {
//                                textColor =
//                                    textColorBasedOnAmount(
//                                        userSettings?.textColor ?: 0,
//                                        0 - convertedAmount
//                                    )
//                            }

                            val timestamp = record.datetime // 假设这是 Long 类型的毫秒时间戳
                            val dateTime = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(timestamp),
                                ZoneId.systemDefault()
                            )
                            val formatter = DateTimeFormatter.ofPattern(
                                "yyyy/MM/dd E HH:mm",
                                Locale.getDefault()
                            )
                            Box(
                                modifier = Modifier.clickable {
                                    selectedMergeRecord = record
                                    showRecordMergeDialog = true
                                },
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    ListItem(
                                        headlineContent = { Text(text = record.name.ifEmpty { categoryName }) },
                                        trailingContent = {
                                            Text(
                                                text = "$$formattedValue",
                                                color = textColor,
                                                fontSize = 14.sp
                                            )
                                        },
                                        supportingContent = {
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                Text(
                                                    text = record.description,
                                                    fontSize = 12.sp,
                                                    color = Gray,
                                                    maxLines = 2
                                                )
                                            }
                                        }
                                    )
                                    Text(
                                        text = dateTime.format(formatter),
                                        fontSize = 10.sp,
                                        color = Gray,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn {
                        items(filteredRecords) { record ->
                            val subCategory =
                                subCategoryViewModel.subCategories.value?.find { it.subCategoryId == record.subCategoryId }
                            val categoryName = subCategory?.let {
                                val resourceId = LocalContext.current.resources.getIdentifier(
                                    it.subCategoryNameKey,
                                    "string",
                                    LocalContext.current.packageName
                                )
                                if (resourceId != 0) {
                                    LocalContext.current.getString(resourceId) // 如果語系字串存在，顯示語系的值
                                } else {
                                    it.subCategoryNameKey // 如果語系字串不存在，顯示原始值
                                }
                            } ?: "Unknown"
                            val recordCurrencyRate =
                                currencyList.find { it.currency == record.currency }?.exchangeRate
                                    ?: 1.0
                            val primaryCurrencyRate =
                                currencyList.find { it.currency == userSettings?.currency }?.exchangeRate
                                    ?: 1.0
                            val convertedAmount =
                                record.amount / recordCurrencyRate * primaryCurrencyRate
                            val decimalFormat = DecimalFormat("#,###.##")
                            val formattedValue = decimalFormat.format(convertedAmount)
                            var textColor =
                                textColorBasedOnAmount(
                                    userSettings?.textColor ?: 0,
                                    convertedAmount
                                )
                            if (selectedCategoryType == 0) {
                                textColor =
                                    textColorBasedOnAmount(
                                        userSettings?.textColor ?: 0,
                                        0 - convertedAmount
                                    )
                            }

                            val timestamp = record.datetime // 假设这是 Long 类型的毫秒时间戳
                            val dateTime = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(timestamp),
                                ZoneId.systemDefault()
                            )
                            val formatter = DateTimeFormatter.ofPattern(
                                "yyyy/MM/dd E HH:mm",
                                Locale.getDefault()
                            )
                            Box(
                                modifier = Modifier.clickable {
                                    selectedRecord = record
                                    showRecordDialog = true
                                },
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    ListItem(
                                        headlineContent = { Text(text = record.name.ifEmpty { categoryName }) },
                                        trailingContent = {
                                            Text(
                                                text = "$$formattedValue",
                                                color = textColor,
                                                fontSize = 14.sp
                                            )
                                        },
                                        supportingContent = {
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                Text(
                                                    text = record.description,
                                                    fontSize = 12.sp,
                                                    color = Gray,
                                                    maxLines = 2
                                                )
                                            }
                                        }
                                    )
                                    Text(
                                        text = dateTime.format(formatter),
                                        fontSize = 10.sp,
                                        color = Gray,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (showRecordMergeDialog && selectedCategoryType == 2) {
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
    if (showRecordDialog && selectedCategoryType != 2) {
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

@Composable
fun ReportCategoryTypeSwitch(selectedCategoryType: Int, onCategoryTypeSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
    ) {
        AssistChip(
            onClick = { onCategoryTypeSelected(0) },
            label = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.expense))
                }
            },
            leadingIcon = {
                if (selectedCategoryType == 0) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Checked",
                        Modifier.size(AssistChipDefaults.IconSize)
                    )
                }
            },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))

        AssistChip(
            onClick = { onCategoryTypeSelected(1) },
            label = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.income))
                }
            },
            leadingIcon = {
                if (selectedCategoryType == 1) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Checked",
                        Modifier.size(AssistChipDefaults.IconSize)
                    )
                }
            },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))

        AssistChip(
            onClick = { onCategoryTypeSelected(2) },
            label = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.transfer))
                }
            },
            leadingIcon = {
                if (selectedCategoryType == 2) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Checked",
                        Modifier.size(AssistChipDefaults.IconSize)
                    )
                }
            },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))

//        AssistChip(
//            onClick = { onCategoryTypeSelected(3) },
//            label = { Text("轉入") },
//            leadingIcon = {
//                if (selectedCategoryType == 3) {
//                    Icon(
//                        Icons.Filled.Check,
//                        contentDescription = "Checked",
//                        Modifier.size(AssistChipDefaults.IconSize)
//                    )
//                }
//            },
//            modifier = Modifier.weight(1f)
//        )
    }
}


@Composable
fun ReportCategoryPieChart(
    categoryMap: Map<Int, List<RecordEntity>>,
    selectedCategoryType: Int,
    mainCategories: List<MainCategoryEntity>
) {
    val context = LocalContext.current

    val m3OnSurface = MaterialTheme.colorScheme.onSurface.toArgb()
    val m3Surface = MaterialTheme.colorScheme.surface.toArgb()


    val totalAmountMap = categoryMap.mapValues { (_, records) ->
        records.sumOf { it.amount }.toFloat()
    }

    if (totalAmountMap.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No data available", fontSize = 16.sp)
        }
        return
    }

    val totalAmountSum = totalAmountMap.values.sum()
    val entries = mutableListOf<PieEntry>()
    val colors = mutableListOf<Int>()

    totalAmountMap.forEach { (mainCategoryId, totalAmount) ->
        val category = mainCategories.find { it.mainCategoryId == mainCategoryId }
        val categoryName = category?.let {
            val resourceId = context.resources.getIdentifier(
                it.mainCategoryNameKey,
                "string",
                context.packageName
            )
            if (resourceId != 0) {
                context.getString(resourceId) // 如果語系字串存在，顯示語系的值
            } else {
                it.mainCategoryNameKey // 如果語系字串不存在，顯示原始值
            }
        } ?: "Unknown"

        // 獲取分類顏色
        val categoryColor = category?.mainCategoryBackgroundColor?.let { colorHex ->
            try {
                android.graphics.Color.parseColor("#$colorHex")
            } catch (e: Exception) {
                android.graphics.Color.GRAY // 如果顏色無效，使用灰色作為回退顏色
            }
        } ?: android.graphics.Color.GRAY // 如果分類為 null，使用灰色作為回退顏色

        entries.add(PieEntry(totalAmount / totalAmountSum * 100, categoryName))
        colors.add(categoryColor)
    }

    val dataSet = PieDataSet(entries, "Category Distribution").apply {
        this.colors = colors
        setValueTextColors(colors)
        valueLinePart1Length = 0.6f
        valueLinePart2Length = 0.3f
        valueLineWidth = 2f
        valueLinePart1OffsetPercentage = 115f
        isUsingSliceColorAsValueLineColor = true
        yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        valueTextSize = 16f
        valueTypeface = Typeface.DEFAULT_BOLD
        valueFormatter = object : ValueFormatter() {
            private val formatter = DecimalFormat("0.0%")

            override fun getFormattedValue(value: Float) =
                formatter.format(value / 100f)
        }
    }

    val pieData = PieData(dataSet)

    Column {
        AndroidView(
            factory = { context ->
                PieChart(context).apply {
                    this.data = pieData
                    this.description.isEnabled = false
                    this.legend.isEnabled = false
                    this.setUsePercentValues(true)
                    this.isDrawHoleEnabled = true
                    this.holeRadius = 60f
                    this.setHoleColor(m3Surface)
                    this.setDrawCenterText(true)
                    this.setCenterTextSize(18f)
                    this.setCenterTextColor(m3OnSurface)
                    this.centerText = formatAmount(totalAmountSum)
                    this.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
                    this.setExtraOffsets(0f, 20f, 0f, 20f)
                }
            },
            update = { chart ->
                chart.data = pieData
                chart.centerText = formatAmount(totalAmountSum)
                chart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}

@Composable
fun ReportSubCategoryPieChart(
    categoryMap: Map<Int, List<RecordEntity>>,
    selectedCategoryType: Int,
    subCategories: List<SubCategoryEntity>
) {
    val context = LocalContext.current

    val m3OnSurface = MaterialTheme.colorScheme.onSurface.toArgb()
    val m3Surface = MaterialTheme.colorScheme.surface.toArgb()


    val totalAmountMap = categoryMap.mapValues { (_, records) ->
        records.sumOf { it.amount }.toFloat()
    }

    if (totalAmountMap.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No data available", fontSize = 16.sp)
        }
        return
    }

    val totalAmountSum = totalAmountMap.values.sum()
    val entries = totalAmountMap.map { (subCategoryId, totalAmount) ->
        val category = subCategories.find { it.subCategoryId == subCategoryId }
        val categoryName = category?.let {
            val resourceId = context.resources.getIdentifier(
                it.subCategoryNameKey,
                "string",
                context.packageName
            )
            if (resourceId != 0) {
                context.getString(resourceId) // 如果語系字串存在，顯示語系的值
            } else {
                it.subCategoryNameKey // 如果語系字串不存在，顯示原始值
            }
        } ?: "Unknown"
//        val categoryName = SharedOptions.mainCategoryMap[mainCategoryId]?.categoryName ?: "Unknown"
        PieEntry(totalAmount / totalAmountSum * 100, categoryName)
    }

    val dataSet = PieDataSet(entries, "Category Distribution").apply {
        colors = listOf(
            Color(0xFF4777C0).toArgb(),  // 轉換為 Int 類型
            Color(0xFFA374C6).toArgb(),
            Color(0xFF4FB3E8).toArgb(),
            Color(0xFF99CF43).toArgb(),
            Color(0xFFFDC135).toArgb(),
            Color(0xFFFD9A47).toArgb(),
            Color(0xFFEB6E7A).toArgb(),
            Color(0xFF6785C2).toArgb(),
            Color(0xFFFF6F61).toArgb(),  // 柔和的红色
            Color(0xFF6B5B95).toArgb(),  // 中性色紫色
            Color(0xFF88B04B).toArgb(),  // 柔和的绿色
            Color(0xFFF7CAC9).toArgb(),  // 粉红色
            Color(0xFF92A8D1).toArgb(),  // 柔和的蓝色
            Color(0xFF955251).toArgb(),  // 红褐色
            Color(0xFFB565A7).toArgb(),  // 紫罗兰色
            Color(0xFF009B77).toArgb(),  // 松绿色
            Color(0xFFDD4124).toArgb(),  // 灼热的橙红色
            Color(0xFF45B8AC).toArgb()   // 青色
        )
        setValueTextColors(colors)
        valueLinePart1Length = 0.6f
        valueLinePart2Length = 0.3f
        valueLineWidth = 2f
        valueLinePart1OffsetPercentage = 115f
        isUsingSliceColorAsValueLineColor = true
        yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        valueTextSize = 16f
        valueTypeface = Typeface.DEFAULT_BOLD
        valueFormatter = object : ValueFormatter() {
            private val formatter = DecimalFormat("0.0%")

            override fun getFormattedValue(value: Float) =
                formatter.format(value / 100f)
        }
    }

    val pieData = PieData(dataSet)

    Column {
        AndroidView(
            factory = { context ->
                PieChart(context).apply {
                    this.data = pieData
                    this.description.isEnabled = false
                    this.legend.isEnabled = false
                    this.setUsePercentValues(true)
                    this.isDrawHoleEnabled = true
                    this.holeRadius = 60f
                    this.setHoleColor(m3Surface)
                    this.setDrawCenterText(true)
                    this.setCenterTextSize(18f)
                    this.setCenterTextColor(m3OnSurface)
                    this.centerText = formatAmount(totalAmountSum)
                    this.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
                    this.setExtraOffsets(0f, 20f, 0f, 20f)
                }
            },
            update = { chart ->
                chart.data = pieData
                chart.centerText = formatAmount(totalAmountSum)
                chart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}

fun formatAmount(amount: Float): String {
    return when {
        amount >= 1_000_000_000 -> DecimalFormat("#.#B").format(amount / 1_000_000_000) // 十億以上顯示為 B
        amount >= 1_000_000 -> DecimalFormat("#.#M").format(amount / 1_000_000) // 百萬以上顯示為 M
        amount >= 1_000 -> DecimalFormat("#.#K").format(amount / 1_000) // 千以上顯示為 K
        else -> DecimalFormat("#,###").format(amount) // 其他顯示為原始數字
    }
}