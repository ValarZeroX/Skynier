package app.skynier.skynier.ui.record

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.time.LocalDate

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
        Log.d("transfersOut", "$transfersOut")
        Log.d("transfersIn", "$transfersIn")
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
                    userSettings
                )
            }
            // 顯示其他類型的記錄 (type 1 和 type 2)
            items(recordsDay.filter { it.type == 1 || it.type == 2 }) { record ->
                DisplaySingleRecord(record, subCategoriesByMainCategory, accounts, userSettings)
            }

//            items(recordsDay) { record ->
//                // 獲取該 mainCategoryId 下的子類別
//                val subCategoriesForThisMainCategory = subCategoriesByMainCategory[record.mainCategoryId] ?: emptyList()
//
//                // 根據 subCategoryId 查找匹配的子類別
//                val matchingSubCategory = subCategoriesForThisMainCategory.find { it.subCategoryId == record.subCategoryId }
//                // 如果找到匹配的子類別，進行處理
//                matchingSubCategory?.let { category ->
//                    val recordIcon = SharedOptions.iconMap[category.subCategoryIcon]
//                    val backgroundColor = Color(android.graphics.Color.parseColor("#${category.subCategoryBackgroundColor}"))
//                    val iconColor = Color(android.graphics.Color.parseColor("#${category.subCategoryIconColor}"))
//                    val decimalFormat = DecimalFormat("#,###.##")
//                    val formattedValue = decimalFormat.format(record.amount)
//
//                    var textColor = MaterialTheme.colorScheme.onBackground
//                    userSettings?.let { textColor = textColor(it.textColor, record.categoryId) }
//                    ListItem(
//                        headlineContent = { Text(text = record.name) },
//                        supportingContent = {
//                            Text(
//                                text = record.description,
//                                color = Gray
//                            )
//                        },
//                        trailingContent = {
//                            Column(
//                                horizontalAlignment = Alignment.End
//                            ) {
//                                Row(
//                                    modifier = Modifier.padding(vertical = 4.dp),
//                                    verticalAlignment = Alignment.CenterVertically,
//                                ) {
//                                    Text(
//                                        text = record.currency,
//                                        fontSize = 10.sp,
//                                        fontWeight = FontWeight.Bold,
//                                        color = Gray
//                                    )
//                                    Spacer(modifier = Modifier.width(4.dp))
//                                    Text(
//                                        text = "$$formattedValue",
//                                        color = textColor,
//                                        fontSize = 16.sp
//                                    )
//                                }
//                                Box(
//                                    modifier = Modifier
//                                        .border(
//                                            width = 1.dp,
//                                            color = Gray,
//                                            shape = RoundedCornerShape(8.dp)
//                                        )
//                                        .padding(top = 1.dp, bottom = 1.dp, start = 10.dp, end = 10.dp)
//                                ) {
//                                    Row(modifier = Modifier.padding(vertical = 2.dp),verticalAlignment = Alignment.CenterVertically) {
//                                        Spacer(modifier = Modifier.width(4.dp))
//                                        accounts.find { it.accountId == record.accountId }?.let {
//                                            val accountIcon = SharedOptions.iconMap[it.accountIcon]
//                                            if (accountIcon != null) {
//                                                Icon(
//                                                    accountIcon.icon,
//                                                    contentDescription = "Localized description",
//                                                    modifier = Modifier.size(16.dp),
//                                                    tint = MaterialTheme.colorScheme.primary
//                                                )
//                                            }
//                                            Spacer(modifier = Modifier.width(4.dp))
//                                            Text(
//                                                text = it.accountName,
//                                                fontSize = 12.sp,
//                                                fontWeight = FontWeight.Bold
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//                        },
//                        leadingContent = {
//                            Box(
//                                modifier = Modifier
//                                    .size(36.dp)
//                                    .background(backgroundColor, CircleShape),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                recordIcon?.let { iconData ->
//                                    // 只有在 recordIcon 不為 null 時才顯示圖標
//                                    Icon(
//                                        imageVector = iconData.icon,
//                                        contentDescription = category.subCategoryNameKey,
//                                        modifier = Modifier.size(20.dp),
//                                        tint = iconColor
//                                    )
//                                } ?: run {
//                                    // 如果沒有找到 icon，顯示一個默認的佔位圖標
//                                    Icon(
//                                        imageVector = Icons.Default.Error, // 可以換成適合的默認圖標
//                                        contentDescription = "Default icon",
//                                        modifier = Modifier.size(18.dp),
//                                        tint = Color.Gray
//                                    )
//                                }
//                            }
//                        }
//                    )
//                    HorizontalDivider()
//                }
//            }
        }
    }
}

@Composable
fun DisplayMergedTransferRecord(
    mergedRecord: MergedTransferEntity,
    accounts: List<AccountEntity>,
    subCategoriesByMainCategory: Map<Int, List<SubCategoryEntity>>,
    userSettings: UserSettingsEntity?,
) {
    val subCategoriesForThisMainCategory =
        subCategoriesByMainCategory[mergedRecord.mainCategoryId] ?: emptyList()
    val matchingSubCategory =
        subCategoriesForThisMainCategory.find { it.subCategoryId == mergedRecord.subCategoryId }

    matchingSubCategory?.let { category ->
        val recordIcon = SharedOptions.iconMap[category.subCategoryIcon]
        val backgroundColor =
            Color(android.graphics.Color.parseColor("#${category.subCategoryBackgroundColor}"))
        val iconColor =
            Color(android.graphics.Color.parseColor("#${category.subCategoryIconColor}"))
        val decimalFormat = DecimalFormat("#,###.##")
        val formattedValueOut = decimalFormat.format(mergedRecord.outAmount)
        val formattedValueIn = decimalFormat.format(mergedRecord.inAmount)

        var textColorMerge = MaterialTheme.colorScheme.onBackground
        userSettings?.let { textColorMerge = textColor(it.textColor, mergedRecord.categoryId) }
        ListItem(
            headlineContent = { Text(text = mergedRecord.name) },
            supportingContent = {
                Column {
                    Row {
                        Text(text="轉帳", color = Gray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Gray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(top = 1.dp, bottom = 1.dp, start = 10.dp, end = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(4.dp))
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
                        Text(text="⮕", color = Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Gray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(top = 1.dp, bottom = 1.dp, start = 10.dp, end = 10.dp)
                        ) {
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
//                Text(text = mergedRecord.inAccountId, color = Gray)
                    Text(text = mergedRecord.description, color = Gray)
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

        ListItem(
            headlineContent = { Text(text = record.name) },
            supportingContent = {
                Text(text = record.description, color = Gray)
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
                    Box(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Gray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(top = 1.dp, bottom = 1.dp, start = 10.dp, end = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(4.dp))
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
)

fun mergeTransferRecords(
    outRecord: RecordEntity, // 轉出記錄
    inRecord: RecordEntity   // 轉入記錄
): MergedTransferEntity {
    return MergedTransferEntity(
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
        subCategoryId = outRecord.subCategoryId
    )
}