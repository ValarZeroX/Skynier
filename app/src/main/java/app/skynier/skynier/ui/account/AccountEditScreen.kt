package app.skynier.skynier.ui.account

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import app.skynier.skynier.R
import app.skynier.skynier.viewmodels.AccountViewModel
import app.skynier.skynier.viewmodels.CategoryViewModel
import app.skynier.skynier.viewmodels.CurrencyViewModel
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.SkynierViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.skynier.skynier.database.entities.AccountEntity
import app.skynier.skynier.database.entities.CurrencyEntity
import app.skynier.skynier.library.CurrencyUtils
import app.skynier.skynier.library.SharedOptions
import app.skynier.skynier.ui.theme.Gray
import app.skynier.skynier.viewmodels.AccountCategoryViewModel
import app.skynier.skynier.viewmodels.CurrencyApiViewModel

@Composable
fun AccountEditScreen(
    navController: NavHostController,
    skynierViewModel: SkynierViewModel,
    categoryViewModel: CategoryViewModel,
    mainCategoryViewModel: MainCategoryViewModel,
    subCategoryViewModel: SubCategoryViewModel,
    accountViewModel: AccountViewModel,
    currencyViewModel: CurrencyViewModel,
    accountCategoryViewModel: AccountCategoryViewModel,
    currencyApiViewModel: CurrencyApiViewModel
) {
    val context = LocalContext.current
    val selectedAccount = skynierViewModel.selectedAccountToEdit
    var displayIcon = SharedOptions.iconMap[selectedAccount!!.accountIcon]
    val defaultIcon = selectedAccount.accountIcon
//    skynierViewModel.updateSelectedIcon(displayIcon!!)
    var hexCode by rememberSaveable {
        mutableStateOf(selectedAccount.accountBackgroundColor)
    }
    var name by rememberSaveable { mutableStateOf(selectedAccount.accountName) }
    val untitled = stringResource(id = R.string.untitled)
    var showEditIconDialog by rememberSaveable { mutableStateOf(false) }
    var showCategoryPicker by rememberSaveable { mutableStateOf(false) }
    var showCurrencyPicker by rememberSaveable { mutableStateOf(false) }

    val accountCategories = accountCategoryViewModel.accountCategories.observeAsState(emptyList())
    LaunchedEffect(Unit) {
        accountCategoryViewModel.loadAllAccountCategories()
        accountViewModel.loadAllAccounts()
    }

    var selectedCategory by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(accountCategories.value) {
        accountCategories.value.find { it.accountCategoryId == selectedAccount.accountCategoryId }?.let { category ->
            val resourceId = context.resources.getIdentifier(
                category.accountCategoryNameKey,
                "string",
                context.packageName
            )
            selectedCategory = if (resourceId != 0) {
                context.getString(resourceId)
            } else {
                category.accountCategoryNameKey
            }
        }
    }

    var balance by rememberSaveable { mutableStateOf(selectedAccount.initialBalance.toString()) }
    val numericRegex = Regex("^-?\\d*\\.?\\d*$")


    val currencyNames = SharedOptions.currencyCodes.map {
        it to CurrencyUtils.getCurrencyName(context, it)
    }
    var selectedCurrency by rememberSaveable { mutableStateOf(selectedAccount.currency) }
    var selectedAccountCategoryId by rememberSaveable { mutableIntStateOf(selectedAccount.accountCategoryId) }

    val currencyList = currencyViewModel.currencies.observeAsState(emptyList())
    val currencyRates by currencyApiViewModel.currencyRates.observeAsState()
    LaunchedEffect(Unit) {
        currencyApiViewModel.fetchCurrencyRates()
        currencyViewModel.loadAllCurrencies()
    }
    Scaffold(
        topBar = {
            AccountEditScreenHeader(
                navController,
                onEditClick = {
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
                    val displayedHexCode = hexCode.takeLast(6).uppercase()
                    if (name.isEmpty()){
                        name = untitled
                    }
                    accountViewModel.updateAccount(
                        AccountEntity(
                            accountId = selectedAccount.accountId,
                            accountName = name,
                            accountCategoryId = selectedAccountCategoryId,
                            currency = selectedCurrency,
                            initialBalance = initialBalanceDouble,
                            accountIcon = accountIconKey,
                            accountBackgroundColor = displayedHexCode,
                            accountIconColor = selectedAccount.accountIconColor,
                            accountSort = selectedAccount.accountSort
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
                            },
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
                        .padding(vertical = 10.dp),
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
                        .padding(vertical = 10.dp),
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
                        .padding(vertical = 10.dp),
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
                        defaultIcon
                    )
                }
                // 當需要顯示類別選擇器時彈出Dialog
                if (showCategoryPicker) {
                    CategoryPickerDialog(
                        categories = accountCategories.value,
                        initialSelection = selectedCategory,
                        initialSelectionId = selectedAccountCategoryId,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEditScreenHeader(
    navController: NavHostController,
    onEditClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = "編輯資產")
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
                onEditClick()
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