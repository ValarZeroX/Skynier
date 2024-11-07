package app.skynier.skynier.ui.account

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import app.skynier.skynier.R
import app.skynier.skynier.database.entities.AccountCategoryEntity
import app.skynier.skynier.database.entities.AccountEntity
import app.skynier.skynier.database.entities.CurrencyEntity
import app.skynier.skynier.database.entities.MainCategoryEntity
import app.skynier.skynier.database.entities.UserSettingsEntity
import app.skynier.skynier.library.CategoryIcon
import app.skynier.skynier.library.CurrencyUtils
import app.skynier.skynier.library.SharedOptions
import app.skynier.skynier.ui.theme.Gray
import app.skynier.skynier.viewmodels.AccountCategoryViewModel
import app.skynier.skynier.viewmodels.AccountViewModel
import app.skynier.skynier.viewmodels.CategoryViewModel
import app.skynier.skynier.viewmodels.CurrencyApiViewModel
import app.skynier.skynier.viewmodels.CurrencyViewModel
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.SkynierViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import app.skynier.skynier.viewmodels.UserSettingsViewModel
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun AccountAddScreen(
    navController: NavHostController,
    skynierViewModel: SkynierViewModel,
    categoryViewModel: CategoryViewModel,
    mainCategoryViewModel: MainCategoryViewModel,
    subCategoryViewModel: SubCategoryViewModel,
    accountViewModel: AccountViewModel,
    currencyViewModel: CurrencyViewModel,
    accountCategoryViewModel: AccountCategoryViewModel,
    currencyApiViewModel: CurrencyApiViewModel,
    userSettingsViewModel: UserSettingsViewModel,
) {
//    val selectedIcon by skynierViewModel.selectedIcon.observeAsState()
    var displayIcon = SharedOptions.iconMap["AccountBalance"] // 使用預設
    var hexCode by rememberSaveable {
        mutableStateOf("FF009EEC")
    }

    var name by rememberSaveable { mutableStateOf("") }
    val untitled = stringResource(id = R.string.untitled)
    val account = accountViewModel.accounts.observeAsState(emptyList())
    val accountCategories = accountCategoryViewModel.accountCategories.observeAsState(emptyList())
    LaunchedEffect(Unit) {
        accountCategoryViewModel.loadAllAccountCategories()
        accountViewModel.loadAllAccounts()
    }
    val cash = stringResource(id = R.string.account_category_cash)
    var selectedCategory by rememberSaveable { mutableStateOf(cash) }
    var selectedAccountCategoryId by rememberSaveable { mutableIntStateOf(1) }
    var showCategoryPicker by rememberSaveable { mutableStateOf(false) } // 控制Dialog是否顯示


    val context = LocalContext.current
    val currencyNames = SharedOptions.currencyCodes.map {
        it to CurrencyUtils.getCurrencyName(context, it)
    }
    var selectedCurrency by rememberSaveable { mutableStateOf("USD") }
    var showCurrencyPicker by rememberSaveable { mutableStateOf(false) } // 控制Dialog是否顯示


    var balance by rememberSaveable { mutableStateOf("0") }
    val numericRegex = Regex("^-?\\d*\\.?\\d*$")

    var showEditIconDialog by rememberSaveable { mutableStateOf(false) }

    val currencyList = currencyViewModel.currencies.observeAsState(emptyList())
    val currencyRates by currencyApiViewModel.currencyRates.observeAsState()
    LaunchedEffect(Unit) {
        currencyApiViewModel.fetchCurrencyRates()
        currencyViewModel.loadAllCurrencies()
    }

    Scaffold(
        topBar = {
            AccountAddScreenHeader(
                navController,
                onAddClick = {
                    var currencyKey = "USD"
                    if (selectedCurrency != "USD") {
                        currencyKey = "USD$selectedCurrency"
                    }

                    val currencyRate = currencyRates?.get(currencyKey)
                    val currentTime = System.currentTimeMillis()

                    val existingCurrency = currencyList.value.find { it.currency == selectedCurrency }

                    if (currencyRate != null) {
                        val newCurrency = CurrencyEntity(
                            currencyId = existingCurrency?.currencyId ?: 0, // 使用已有ID來更新，若沒有則為新插入
                            currency = selectedCurrency,
                            exchangeRate = currencyRate.exchangeRate,
                            lastUpdatedTime = currentTime
                        )

                        if (existingCurrency != null) {
                            // 已存在相同幣別，更新它
                            currencyViewModel.updateCurrency(newCurrency)
                        } else {
                            // 不存在相同幣別，插入新的
                            currencyViewModel.insertCurrency(newCurrency)
                        }
                    }


                    val initialBalanceDouble = balance.toDoubleOrNull() ?: 0.0
                    val accountIconKey = SharedOptions.iconMap.entries.find { it.value == displayIcon }?.key ?: "AccountBalance"
                    val displayedHexCode = hexCode.uppercase()
                    if (name.isEmpty()){
                        name = untitled
                    }

                    //第一次建立設定主幣別
                    if (account.value.isEmpty()) {
                        userSettingsViewModel.saveUserSettings(
                            UserSettingsEntity(
                                currency = selectedCurrency
                            )
                        )
                    }
                    accountViewModel.insertAccount(
                        AccountEntity(
                            accountName = name,
                            accountCategoryId = selectedAccountCategoryId,
                            currency = selectedCurrency,
                            initialBalance = initialBalanceDouble,
                            accountIcon = accountIconKey,
                            accountBackgroundColor = displayedHexCode,
                            accountIconColor = "FBFBFB",
                            accountSort = account.value.size +1

                        )
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center // Center the label
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp) // Set the size of the circular background
                            .background(
                                Color(android.graphics.Color.parseColor("#${hexCode}")),
                                CircleShape
                            )
                            .clickable {
                                skynierViewModel.updateSelectedIcon(displayIcon!!) // 将 displayIcon 设置为 selectedIcon
                                showEditIconDialog = true
//                                navController.navigate("icon")
                            }, // Set background color and shape
                        contentAlignment = Alignment.Center
                    ) {
                        displayIcon?.let {
                            Icon(
                                imageVector = it.icon,
                                contentDescription = "Icon",
                                modifier = Modifier.size(58.dp),
                                tint = Color(android.graphics.Color.parseColor("#FBFBFB"))
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center // Center the label
                ) {
                    if (name.isEmpty()) {
                        Text(text = untitled, color = Gray) // Display label text when empty
                    }
                    BasicTextField(
                        value = name,
                        onValueChange = { newName ->
                            if (newName.length <= 60) {
                                name = newName
                            }
                        },
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCategoryPicker = true } // 點擊打開Dialog
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.account_category),
                        modifier = Modifier.padding(start = 16.dp).weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                    ) {
                        Text(
                            text = selectedCategory,
                            color = MaterialTheme.colorScheme.primary
                        ) // 顯示當前選擇的類別
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCurrencyPicker = true } // 點擊打開Dialog
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.currency),
                        modifier = Modifier.padding(start = 16.dp).weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                    ) {
                        Text(
                            text = CurrencyUtils.getCurrencyName(context, selectedCurrency),
                            color = MaterialTheme.colorScheme.primary
                        ) // 顯示當前選擇的幣別名稱
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.balance),
                        modifier = Modifier.padding(start = 16.dp).weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                    ) {
//                        if (balance.isEmpty()) {
//                            balance = "0"
//                        }
                        BasicTextField(
                            value = balance,
                            onValueChange = { newInput ->
                                // 過濾輸入，只接受有效的浮點數、負數和小數點
                                if (newInput.isEmpty() || newInput.matches(numericRegex)) {
                                    balance = if (newInput.startsWith("0") && newInput.length > 1 && !newInput.startsWith("0.")) {
                                        newInput.trimStart('0')
                                    } else {
                                        newInput
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Decimal // 顯示帶有小數點的數字鍵盤6
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            singleLine = true,
                            modifier = Modifier.onFocusChanged { focusState ->
                                if (balance == "0") {
                                    balance = ""
                                }
                                if (!focusState.isFocused && balance.isEmpty()) {
                                    balance = "0" // 當輸入框失去焦點且為空時，填入0
                                }
                            },
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.End,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
                if (showEditIconDialog) {

                    IconPickerDialog(
                        navController,
                        skynierViewModel,
                        onDismiss = { showEditIconDialog = false },
                        onAdd = { newHexCode, newIcon ->
                            hexCode = newHexCode
                            displayIcon = newIcon
                            showEditIconDialog = false
                        },
                        displayIcon,
                        hexCode,
                        "AccountBalance"
                    )
                }
                // 當需要顯示類別選擇器時彈出Dialog
                if (showCategoryPicker) {
                    CategoryPickerDialog(
                        categories = accountCategories.value,
                        initialSelection = selectedCategory,
                        initialSelectionId =selectedAccountCategoryId,
                        onCategorySelected = { selected, accountCategoryId ->
                            selectedCategory = selected // 更新選中的類別
                            selectedAccountCategoryId = accountCategoryId
                            showCategoryPicker = false // 關閉Dialog
                        },
                        onDismiss = { showCategoryPicker = false }
                    )
                }
                if (showCurrencyPicker) {
                    CurrencyPickerDialog(
                        currencies = currencyNames,
                        initialSelection = selectedCurrency,
                        onCurrencySelected = { selectedCurrencyCode ->
                            selectedCurrency = selectedCurrencyCode
                            showCurrencyPicker = false
                        },
                        onDismiss = { showCurrencyPicker = false }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryPickerDialog(
    categories: List<AccountCategoryEntity>,
    initialSelection: String,
    initialSelectionId: Int,
    onCategorySelected: (String, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by rememberSaveable { mutableStateOf(initialSelection) }
    var selectedAccountCategoryId by rememberSaveable { mutableIntStateOf(initialSelectionId) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(id = R.string.account_category), modifier = Modifier.padding(bottom = 8.dp))
                }
                HorizontalDivider()
                LazyColumn(
                    modifier = Modifier.height(200.dp) // 設定滾輪的高度
                ) {
                    items(categories) { category ->
                        val context = LocalContext.current
                        val resourceId =
                            context.resources.getIdentifier(category.accountCategoryNameKey, "string", context.packageName)
                        val displayName = if (resourceId != 0) {
                            context.getString(resourceId) // 如果語系字串存在，顯示語系的值
                        } else {
                            category.accountCategoryNameKey // 如果語系字串不存在，顯示原始值
                        }
                        val accountCategoryId = category.accountCategoryId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCategory = displayName // 更新選擇的類別
                                    selectedAccountCategoryId = accountCategoryId
                                }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = displayName)
                            if (selectedAccountCategoryId == accountCategoryId) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected"
                                )
                            }
                        }
                    }
                }
                HorizontalDivider()
                // 確認按鈕
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onCategorySelected(selectedCategory, selectedAccountCategoryId) // 傳遞選中的類別和ID
                        onDismiss()
                    }) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyPickerDialog(
    currencies: List<Pair<String, String>>, // 包含幣別代碼和對應名稱的列表
    initialSelection: String,
    onCurrencySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCurrency by rememberSaveable { mutableStateOf(initialSelection) }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 搜索框
                var searchQuery by rememberSaveable { mutableStateOf("") }
                val filteredCurrencies = currencies.filter {
                    it.second.contains(searchQuery, ignoreCase = true) ||
                            it.first.contains(searchQuery, ignoreCase = true)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape) // 背景為圓角
                        .padding(horizontal = 8.dp, vertical = 4.dp) // 適當的內邊距
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search, // 搜索圖標
                        contentDescription = "Search Currency",
                        modifier = Modifier.size(28.dp), // 設置圖標大小
//                        tint = Color(android.graphics.Color.parseColor("#FFFFFF")) // 設置圖標顏色
                    )

                    Spacer(modifier = Modifier.width(8.dp)) // 圖標與輸入框之間的空隙

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { newQuery -> searchQuery = newQuery },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth(), // 讓輸入框填滿剩餘寬度
                        textStyle = LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                    )
                }

                // 幣別列表
                LazyColumn(
                    modifier = Modifier.height(200.dp)
                ) {
                    items(filteredCurrencies) { (currencyCode, currencyName) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCurrency = currencyCode
//                                    onCurrencySelected(currencyCode)
                                }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "$currencyName($currencyCode)")
                            if (selectedCurrency == currencyCode) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected"
                                )
                            }
                        }
                    }
                }
                HorizontalDivider()
                // 確認按鈕
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onCurrencySelected(selectedCurrency) }) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
fun IconPickerDialog(
    navController: NavHostController,
    skynierViewModel: SkynierViewModel,
    onDismiss: () -> Unit,
    onAdd: (String, CategoryIcon?) -> Unit,
    initialIcon: CategoryIcon?,
    initialHexCode: String,
    defaultIcon: String,
) {
    val selectedIcon by skynierViewModel.selectedIcon.observeAsState()
    val displayIcon = selectedIcon ?: SharedOptions.iconMap[defaultIcon] // 使用預設
//    var displayIcon by rememberSaveable { mutableStateOf(initialIcon) }
    var hexCode by rememberSaveable { mutableStateOf(initialHexCode) }
    val controller = rememberColorPickerController()
    LaunchedEffect(controller) {
        controller.selectByColor(Color(android.graphics.Color.parseColor("#$hexCode")), fromUser = false)
    }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center // Center the label
                ) {
                    Text(
                        text = stringResource(id = R.string.add_main_category),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                HorizontalDivider()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center // Center the label
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp) // Set the size of the circular background
                            .background(
                                Color(android.graphics.Color.parseColor("#${hexCode}")),
                                CircleShape
                            )
                            .clickable {
                                navController.navigate("icon")
                            }, // Set background color and shape
                        contentAlignment = Alignment.Center
                    ) {
                        displayIcon?.let {
                            Icon(
                                imageVector = it.icon,
                                contentDescription = "Icon",
                                modifier = Modifier.size(28.dp),
                                tint = Color(android.graphics.Color.parseColor("#FBFBFB"))
                            )
                        }
                    }
                }
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        hexCode = colorEnvelope.hexCode
                    },
                    initialColor = Color(android.graphics.Color.parseColor("#${hexCode}")),
                )
                AlphaSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(14.dp),
                    controller = controller,
                )
                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(14.dp),
                    controller = controller,
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onAdd(hexCode, displayIcon)
                        }
                    ) {
                        Text(stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountAddScreenHeader(
    navController: NavHostController,
    onAddClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(stringResource(id = R.string.add_asset))
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
            IconButton(onClick = {
                onAddClick()
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Check"
                )
            }
        }
    )
}