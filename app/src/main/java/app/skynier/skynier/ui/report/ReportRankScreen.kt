package app.skynier.skynier.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import app.skynier.skynier.R
import app.skynier.skynier.database.entities.AccountEntity
import app.skynier.skynier.database.entities.CurrencyEntity
import app.skynier.skynier.database.entities.RecordEntity
import app.skynier.skynier.database.entities.UserSettingsEntity
import app.skynier.skynier.library.SharedOptions
import app.skynier.skynier.library.textColor
import app.skynier.skynier.ui.record.RecordDayListScreen
import app.skynier.skynier.ui.record.RecordDialog
import app.skynier.skynier.ui.theme.Gray
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.RecordViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import java.text.DecimalFormat

@Composable
fun ReportAssetScreen(
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
            Text(stringResource(id = R.string.no_data_available), fontSize = 16.sp)
        }
        return
    }

    // 獲取使用者的主要貨幣代碼
    val primaryCurrencyCode = userSettings?.currency ?: "USD"
    val primaryCurrencyRate =
        currencyList.find { it.currency == primaryCurrencyCode }?.exchangeRate ?: 1.0

    var showReportCategoryListDialog by rememberSaveable { mutableStateOf(false) }
    val subCategoriesByMainCategory by subCategoryViewModel.subCategoriesByMainCategory.observeAsState(
        emptyMap()
    )

    LaunchedEffect(Unit) {
        subCategoryViewModel.loadAllSubCategoriesAndGroupByMainCategory()
    }

    var showRecordDialog by rememberSaveable { mutableStateOf(false) }
    var selectedRecord by remember { mutableStateOf<RecordEntity?>(null) }

    if (selectedMainCategoryId == null) {
        val filteredRecords = when (selectedCategoryType) {
            0 -> recordTotal.filter { it.type == 1 } // Expenses
            1 -> recordTotal.filter { it.type == 2 } // Income
            2 -> recordTotal.filter { it.type == 3 } // Transfers
//            3 -> recordTotal.filter { it.type == 4 } // 轉入
            else -> emptyList()
        }
//        val convertedRecords: List<RecordEntity> = filteredRecords.map { record ->
//            val recordCurrencyRate =
//                currencyList.find { it.currency == record.currency }?.exchangeRate ?: 1.0
//            val convertedAmount = record.amount / recordCurrencyRate * primaryCurrencyRate
//            record.copy(amount = convertedAmount)
//        }.sortedByDescending { it.amount }

        val convertedRecords: List<RecordEntity> = filteredRecords.sortedByDescending { record ->
            val recordCurrencyRate =
                currencyList.find { it.currency == record.currency }?.exchangeRate ?: 1.0
            val convertedAmount = record.amount / recordCurrencyRate * primaryCurrencyRate
            convertedAmount
        }
        // 顯示主類別
//        val categoryMap = convertedRecords.groupBy { it.mainCategoryId }

        Column {
            ReportCategoryTypeSwitch(
                selectedCategoryType = selectedCategoryType,
                onCategoryTypeSelected = { newType -> selectedCategoryType = newType },
            )
            LazyColumn {
                items(convertedRecords) { record ->
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
                                            accounts.find { it.accountId == record.accountId }
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
            }
        }
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