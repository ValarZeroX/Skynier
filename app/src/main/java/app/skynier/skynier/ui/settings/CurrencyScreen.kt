package app.skynier.skynier.ui.settings

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import app.skynier.skynier.database.entities.CurrencyEntity
import app.skynier.skynier.database.entities.UserSettingsEntity
import app.skynier.skynier.library.CurrencyUtils
import app.skynier.skynier.library.SharedOptions
import app.skynier.skynier.ui.account.CurrencyPickerDialog
import app.skynier.skynier.ui.theme.Gray
import app.skynier.skynier.viewmodels.CurrencyApiViewModel
import app.skynier.skynier.viewmodels.CurrencyViewModel
import app.skynier.skynier.viewmodels.SkynierViewModel
import app.skynier.skynier.viewmodels.UserSettingsViewModel
import java.util.Locale

@Composable
fun CurrencyScreen(
    navController: NavHostController,
    skynierViewModel: SkynierViewModel,
    currencyViewModel: CurrencyViewModel,
    currencyApiViewModel: CurrencyApiViewModel,
    userSettingsViewModel: UserSettingsViewModel,
) {
    val currencyList by currencyViewModel.currencies.observeAsState(emptyList())
    val currencyRates by currencyApiViewModel.currencyRates.observeAsState()
    val userSettings by userSettingsViewModel.userSettings.observeAsState()
    LaunchedEffect(Unit) {
        currencyApiViewModel.fetchCurrencyRates()
        currencyViewModel.loadAllCurrencies()
        userSettingsViewModel.loadUserSettings()
    }

    val context = LocalContext.current
    val currencyNames = SharedOptions.currencyCodes.map {
        it to CurrencyUtils.getCurrencyName(context, it)
    }
    var selectedCurrency by rememberSaveable { mutableStateOf( "USD") }
    var showCurrencyPicker by rememberSaveable { mutableStateOf(false) } // 控制Dialog是否顯示

    val isUserSettingsLoaded = userSettings != null
    if (isUserSettingsLoaded) {
        selectedCurrency = userSettings!!.currency
    }
//    val mainCurrencyRate by remember {
//        derivedStateOf {
//            val usdToSelectedCurrencyRate = currencyRates?.get("USD$selectedCurrency")?.exchangeRate ?: 1.0
//            usdToSelectedCurrencyRate.toString()
//        }
//    }

    fun convertToMainCurrency(currencyCode: String): String {
        val usdToTargetCurrencyRate = currencyRates?.get("USD$currencyCode")?.exchangeRate ?: 1.0
        val usdToSelectedCurrencyRate = currencyRates?.get("USD$selectedCurrency")?.exchangeRate ?: 1.0
        val conversionRate = usdToSelectedCurrencyRate / usdToTargetCurrencyRate
        return String.format(Locale.US, "%.8f", conversionRate)
    }

    Scaffold(
        topBar = {
            CurrencyScreenHeader(
                navController,
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LazyColumn {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "主幣別",
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(1f),
                            color = Gray
                        )
                    }
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedCurrency,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(1f)
                        )
                        IconButton(onClick = {
                            showCurrencyPicker = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.CurrencyExchange,
                                contentDescription = "CurrencyExchange"
                            )
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "匯率",
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(1f),
                            color = Gray
                        )
                    }
                    HorizontalDivider()
                }
                items(currencyList) { currency ->
                    val conversionRate = convertToMainCurrency(currency.currency)
                    ListItem(
                        headlineContent = { Text(text = "${currency.currency} 1.00 = $selectedCurrency $conversionRate") },
                    )
                    HorizontalDivider()
                }
            }
        }
        if (showCurrencyPicker) {
            CurrencyPickerDialog(
                currencies = currencyNames,
                initialSelection = selectedCurrency,
                onCurrencySelected = { selectedCurrencyCode ->
                    selectedCurrency = selectedCurrencyCode

                    // 更新用户设置
                    userSettingsViewModel.updateUserSettings(
                        UserSettingsEntity(
                            id = userSettings?.id ?: 0, // 如果有则更新，没有则新建
                            currency = selectedCurrency
                        )
                    )

                    // 更新数据库中的汇率
                    val currencyKey = if (selectedCurrency != "USD") "USD$selectedCurrency" else "USD"
                    val currencyRate = currencyRates?.get(currencyKey)
                    currencyRate?.let { it ->
                        val existingCurrency = currencyList.find { it.currency == selectedCurrency }
                        val newCurrency = CurrencyEntity(
                            currencyId = existingCurrency?.currencyId ?: 0, // 若已存在，使用原ID
                            currency = selectedCurrency,
                            exchangeRate = it.exchangeRate,
                            lastUpdatedTime = System.currentTimeMillis()
                        )
                        if (existingCurrency != null) {
                            currencyViewModel.updateCurrency(newCurrency)
                        } else {
                            currencyViewModel.insertCurrency(newCurrency)
                        }
                    }

                    showCurrencyPicker = false
                },
                onDismiss = { showCurrencyPicker = false }
            )
        }
    }
}

//@Composable
//fun MainCurrencyPickerDialog(
//    currencies: List<Pair<String, String>>, // 包含幣別代碼和對應名稱的列表
//    initialSelection: String,
//    onCurrencySelected: (String) -> Unit,
//    onDismiss: () -> Unit
//) {
//    var selectedCurrency by rememberSaveable { mutableStateOf(initialSelection) }
//    Dialog(onDismissRequest = onDismiss) {
//        Surface(
//            shape = MaterialTheme.shapes.medium,
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Column(modifier = Modifier.padding(16.dp)) {
//                // 搜索框
//                var searchQuery by rememberSaveable { mutableStateOf("") }
//                val filteredCurrencies = currencies.filter {
//                    it.second.contains(searchQuery, ignoreCase = true) ||
//                            it.first.contains(searchQuery, ignoreCase = true)
//                }
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 8.dp)
//                        .background(MaterialTheme.colorScheme.surface, CircleShape) // 背景為圓角
//                        .padding(horizontal = 8.dp, vertical = 4.dp) // 適當的內邊距
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.Search, // 搜索圖標
//                        contentDescription = "Search Currency",
//                        modifier = Modifier.size(28.dp), // 設置圖標大小
////                        tint = Color(android.graphics.Color.parseColor("#FFFFFF")) // 設置圖標顏色
//                    )
//
//                    Spacer(modifier = Modifier.width(8.dp)) // 圖標與輸入框之間的空隙
//
//                    BasicTextField(
//                        value = searchQuery,
//                        onValueChange = { newQuery -> searchQuery = newQuery },
//                        singleLine = true,
//                        modifier = Modifier
//                            .fillMaxWidth(), // 讓輸入框填滿剩餘寬度
//                        textStyle = LocalTextStyle.current.copy(
//                            color = MaterialTheme.colorScheme.onSurface
//                        ),
//                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
//                    )
//                }
//
//                // 幣別列表
//                LazyColumn(
//                    modifier = Modifier.height(200.dp)
//                ) {
//                    items(filteredCurrencies) { (currencyCode, currencyName) ->
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable {
//                                    selectedCurrency = currencyCode
////                                    onCurrencySelected(currencyCode)
//                                }
//                                .padding(16.dp),
//                            horizontalArrangement = Arrangement.SpaceBetween,
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(text = "$currencyName($currencyCode)")
//                            if (selectedCurrency == currencyCode) {
//                                Icon(
//                                    imageVector = Icons.Default.Check,
//                                    contentDescription = "Selected"
//                                )
//                            }
//                        }
//                    }
//                }
//                HorizontalDivider()
//                // 確認按鈕
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.End
//                ) {
//                    TextButton(onClick = onDismiss) {
//                        Text(text = stringResource(id = R.string.cancel))
//                    }
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Button(onClick = { onCurrencySelected(selectedCurrency) }) {
//                        Text(text = stringResource(id = R.string.confirm))
//                    }
//                }
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyScreenHeader(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            Text("幣別匯率")
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Back"
                )
            }

        }
    )
}