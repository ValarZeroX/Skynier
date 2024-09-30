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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePickerState
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
import app.skynier.skynier.database.entities.AccountEntity
import app.skynier.skynier.database.entities.MainCategoryEntity
import app.skynier.skynier.database.entities.SubCategoryEntity
import app.skynier.skynier.library.DatePicker
import app.skynier.skynier.library.SharedOptions
import app.skynier.skynier.library.TimePicker
import app.skynier.skynier.library.combineDateAndTimeVersion2
import app.skynier.skynier.ui.theme.Gray
import app.skynier.skynier.viewmodels.AccountCategoryViewModel
import app.skynier.skynier.viewmodels.AccountViewModel
import app.skynier.skynier.viewmodels.CategoryViewModel
import app.skynier.skynier.viewmodels.CurrencyViewModel
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.RecordViewModel
import app.skynier.skynier.viewmodels.SkynierViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordAddScreen(
    navController: NavHostController,
    skynierViewModel: SkynierViewModel,
    categoryViewModel: CategoryViewModel,
    mainCategoryViewModel: MainCategoryViewModel,
    subCategoryViewModel: SubCategoryViewModel,
    accountCategoryViewModel: AccountCategoryViewModel,
    accountViewModel: AccountViewModel,
    currencyViewModel: CurrencyViewModel,
    recordViewModel: RecordViewModel,
) {
    val categories = categoryViewModel.categories.observeAsState(emptyList())
    val mainCategories = mainCategoryViewModel.mainCategories.observeAsState(emptyList())
    val subCategories = subCategoryViewModel.subCategories.observeAsState(emptyList())
    var selectedTabIconIndex by remember { mutableIntStateOf(0) }
    var selectedSubCategories by remember { mutableStateOf<SubCategoryEntity?>(null) }
    val currencyList by currencyViewModel.currencies.observeAsState(emptyList())
    LaunchedEffect(Unit) {
        categoryViewModel.loadAllCategories()
        mainCategoryViewModel.loadMainCategoriesByMainCategoryId(selectedTabIconIndex + 1)
        currencyViewModel.loadAllCurrencies()
    }

    // 當主分類加載完成時，載入對應的子分類
    LaunchedEffect(mainCategories.value) {
        if (mainCategories.value.isNotEmpty()) {
            val mainCategoryId = mainCategories.value.first().mainCategoryId
            subCategoryViewModel.loadSubCategoriesByMainCategoryId(mainCategoryId)
        }
    }

    // 當子分類加載完成時，自動選擇第一個子分類
    LaunchedEffect(subCategories.value) {
        if (subCategories.value.isNotEmpty()) {
            selectedSubCategories = subCategories.value.first()
        }
    }

    // 載入資料
    LaunchedEffect(selectedTabIconIndex) {
        mainCategoryViewModel.loadMainCategoriesByMainCategoryId(selectedTabIconIndex + 1)
        Log.d("isNotEmpty", "${mainCategories.value.isNotEmpty()}")
        if (mainCategories.value.isNotEmpty()) {
            val mainCategoryId = mainCategories.value.first().mainCategoryId
            subCategoryViewModel.loadSubCategoriesByMainCategoryId(mainCategoryId)
        }
    }

    val numericRegex = Regex("^-?\\d*\\.?\\d*$")
    var amount by remember {
        mutableStateOf("0")
    }

    var remark by rememberSaveable { mutableStateOf("") }
    var showAsset by rememberSaveable { mutableStateOf(false) }

    val accounts = accountViewModel.accounts.observeAsState(emptyList())
    LaunchedEffect(Unit) {
        accountViewModel.loadAllAccounts()
    }
    Log.d("accounts", "$accounts")

    var selectedAsset by remember { mutableStateOf<AccountEntity?>(if (accounts.value.isNotEmpty()) accounts.value.first() else null) }
    var showCategory by rememberSaveable { mutableStateOf(false) }

    //日期選擇棄
    val initialDate = Calendar.getInstance().timeInMillis
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(initialDate) }
    val formattedDate = selectedDate?.let {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = it
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(calendar.time)
    } ?: "選擇日期"

    //時間選擇器
    val initialDateTime = System.currentTimeMillis()
    val calendar = Calendar.getInstance().apply {
        timeInMillis = initialDateTime
    }
    val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
    val initialMinute = calendar.get(Calendar.MINUTE)

    var selectedTime by remember {
        mutableStateOf(
            TimePickerState(
                initialHour = initialHour,
                initialMinute = initialMinute,
                is24Hour = true
            )
        )
    }
    var showTimePicker by remember { mutableStateOf(false) }
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val buttonText = run {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, selectedTime.hour)
            set(Calendar.MINUTE, selectedTime.minute)
        }
        formatter.format(cal.time)
    }

    val transactionDate: Long = combineDateAndTimeVersion2(selectedDate, selectedTime)

    var selectedTransferAssetFrom by remember {
        mutableStateOf<AccountEntity?>(if (accounts.value.isNotEmpty()) accounts.value.first() else null)
    }
    var selectedTransferAssetTo by remember {
        mutableStateOf<AccountEntity?>(if (accounts.value.isNotEmpty()) accounts.value.first() else null)
    }
    var showTransferAssetFrom by rememberSaveable { mutableStateOf(false) }
    var showTransferAssetTo by rememberSaveable { mutableStateOf(false) }
    var transferAmountFrom by remember {
        mutableStateOf("0.00")
    }
    var transferAmountTo by remember {
        mutableStateOf("0.00")
    }

    val fromCurrencyRate = currencyList.find { it.currency == selectedTransferAssetFrom?.currency }?.exchangeRate ?: 1.0
    val toCurrencyRate = currencyList.find { it.currency == selectedTransferAssetTo?.currency }?.exchangeRate ?: 1.0

    LaunchedEffect(selectedTransferAssetFrom) {
        val fromAmount = transferAmountFrom.toDoubleOrNull() ?: 0.0
        val conversionRate = toCurrencyRate / fromCurrencyRate
        val convertedAmount = fromAmount * conversionRate
        transferAmountTo = String.format(Locale.US, "%.2f", convertedAmount)
    }
    LaunchedEffect(selectedTransferAssetTo) {
        val fromAmount = transferAmountFrom.toDoubleOrNull() ?: 0.0
        val conversionRate = toCurrencyRate / fromCurrencyRate
        val convertedAmount = fromAmount * conversionRate
        transferAmountTo = String.format(Locale.US, "%.2f", convertedAmount)
    }

    Scaffold(
        topBar = {
            RecordAddScreenHeader(
                navController,
                onAddClick = {

                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column {
                TabRow(selectedTabIndex = selectedTabIconIndex) {
                    categories.value.forEachIndexed { index, value ->
                        val context = LocalContext.current
                        val resourceId =
                            context.resources.getIdentifier(
                                value.categoryIdNameKey,
                                "string",
                                context.packageName
                            )
                        val displayName = if (resourceId != 0) {
                            context.getString(resourceId) // 如果語系字串存在，顯示語系的值
                        } else {
                            value.categoryIdNameKey // 如果語系字串不存在，顯示原始值
                        }
                        Tab(
                            selected = selectedTabIconIndex == index,
                            onClick = {
                                selectedTabIconIndex = index
//                                selectedSubCategories = null

                            },
                            text = { Text(text = displayName) }
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (selectedTabIconIndex == 2) {
                        Row(modifier = Modifier.padding(top = 16.dp)) {
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp),
                                onClick = {
                                    selectedAsset?.let {
                                        showTransferAssetFrom = true
                                    }?: run {
                                        navController.navigate("account_add")
                                    }
                                }) {
                                selectedTransferAssetFrom?.let {
                                    val accountIcon =
                                        SharedOptions.iconMap[it.accountIcon]
                                    Icon(
                                        imageVector = accountIcon!!.icon,
                                        contentDescription = it.accountIcon
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = it.accountName)
                                } ?: run {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Add"
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = "新增資產") // 或者显示提示信息
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 16.dp),
                                onClick = {
                                    selectedAsset?.let {
                                        showTransferAssetTo = true
                                    }?: run {
                                        navController.navigate("account_add")
                                    }
                                }) {
                                selectedTransferAssetTo?.let {
                                    val accountIcon =
                                        SharedOptions.iconMap[it.accountIcon]
                                    Icon(
                                        imageVector = accountIcon!!.icon,
                                        contentDescription = it.accountIcon
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = it.accountName)
                                } ?: run {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Add"
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = "新增資產") // 或者显示提示信息
                                }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.End, // 添加此项使左右文本框有空隙
//                            verticalAlignment = Alignment.CenterVertically // 使文本框垂直居中
                        ) {
                            OutlinedTextField(
                                value = transferAmountFrom,
                                onValueChange = { newInput ->
                                    // 过滤输入，只接受有效的浮点数、负数和小数点
                                    if (newInput.isEmpty() || newInput.matches(numericRegex)) {
                                        transferAmountFrom =
                                            if (newInput.startsWith("0") && newInput.length > 1 && !newInput.startsWith(
                                                    "0."
                                                )
                                            ) {
                                                newInput.trimStart('0')
                                            } else {
                                                newInput
                                            }
                                            val fromAmount = transferAmountFrom.toDoubleOrNull() ?: 0.0
                                            val conversionRate = toCurrencyRate / fromCurrencyRate
                                            val convertedAmount = fromAmount * conversionRate
                                            transferAmountTo = String.format(Locale.US, "%.2f", convertedAmount)
                                    }
                                },
                                label = { Text("From") }, // 添加标签
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Decimal // 显示带有小数点的数字键盘
                                ),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(
                                    textAlign = TextAlign.End, // 文本右对齐
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp)
                                    .onFocusChanged { focusState ->
                                        if (transferAmountFrom == "0") {
                                            transferAmountFrom = ""
                                        }
                                        if (!focusState.isFocused && transferAmountFrom.isEmpty()) {
                                            transferAmountFrom = "0" // 当输入框失去焦点且为空时，填入0
                                        }
                                        if (!focusState.isFocused && transferAmountFrom.isNotEmpty()) {
                                            // 執行換算邏輯當失去焦點時
                                            val fromAmount = transferAmountFrom.toDoubleOrNull() ?: 0.0
                                            val conversionRate = toCurrencyRate / fromCurrencyRate
                                            val convertedAmount = fromAmount * conversionRate
                                            transferAmountTo = String.format(Locale.US, "%.2f", convertedAmount)
                                        }
                                    },
                                shape = RoundedCornerShape(8.dp), // 设置边框圆角
                                leadingIcon = {
                                    selectedTransferAssetFrom?.let { Text(it.currency) }
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            OutlinedTextField(
                                value = transferAmountTo,
                                onValueChange = { newInput ->
                                    // 过滤输入，只接受有效的浮点数、负数和小数点
                                    if (newInput.isEmpty() || newInput.matches(numericRegex)) {
                                        transferAmountTo =
                                            if (newInput.startsWith("0") && newInput.length > 1 && !newInput.startsWith(
                                                    "0."
                                                )
                                            ) {
                                                newInput.trimStart('0')
                                            } else {
                                                newInput
                                            }
                                    }
                                },
                                label = { Text("To") }, // 添加标签
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Decimal // 显示带有小数点的数字键盘
                                ),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(
                                    textAlign = TextAlign.End, // 文本右对齐
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 16.dp)
                                    .onFocusChanged { focusState ->
                                        if (transferAmountTo == "0") {
                                            transferAmountTo = ""
                                        }
                                        if (!focusState.isFocused && transferAmountTo.isEmpty()) {
                                            transferAmountTo = "0" // 当输入框失去焦点且为空时，填入0
                                        }
//                                        if (!focusState.isFocused && transferAmountTo.isNotEmpty()) {
//                                            // 執行反向換算邏輯當失去焦點時
//                                            val toAmount = transferAmountTo.toDoubleOrNull() ?: 0.0
//                                            val reverseConversionRate = fromCurrencyRate / toCurrencyRate
//                                            val convertedAmount = toAmount * reverseConversionRate
//                                            transferAmountFrom = String.format(Locale.US, "%.2f", convertedAmount)
//                                        }
                                    },
                                shape = RoundedCornerShape(8.dp),
                                leadingIcon = {
                                    selectedTransferAssetTo?.let { Text(it.currency) }
                                },
                                enabled = false
                            )
                        }
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
                                    .weight(1f)
                            )
                            Text(
                                text = String.format(Locale.US, "%.4f", toCurrencyRate),
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .weight(1f),
                                textAlign = TextAlign.End,
                                color = Gray
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.amount),
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .weight(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                            ) {
                                BasicTextField(
                                    value = amount,
                                    onValueChange = { newInput ->
                                        // 過濾輸入，只接受有效的浮點數、負數和小數點
                                        if (newInput.isEmpty() || newInput.matches(numericRegex)) {
                                            amount =
                                                if (newInput.startsWith("0") && newInput.length > 1 && !newInput.startsWith(
                                                        "0."
                                                    )
                                                ) {
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
                                        if (amount == "0") {
                                            amount = ""
                                        }
                                        if (!focusState.isFocused && amount.isEmpty()) {
                                            amount = "0" // 當輸入框失去焦點且為空時，填入0
                                        }
                                    },
                                    textStyle = LocalTextStyle.current.copy(
                                        textAlign = TextAlign.End,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
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
                                text = stringResource(id = R.string.asset),
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .weight(1f)
                            )
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 16.dp, start = 10.dp),
                                onClick = {
                                    selectedAsset?.let {
                                        showAsset = true
                                    }?: run {
                                        navController.navigate("account_add")
                                    }
                                }) {
                                selectedAsset?.let {
                                    val accountIcon =
                                        SharedOptions.iconMap[it.accountIcon]
                                    Icon(
                                        imageVector = accountIcon!!.icon,
                                        contentDescription = it.accountIcon
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = it.accountName)
                                } ?: run {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Add"
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = "新增資產") // 或者显示提示信息
                                }
                            }
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
                            text = "類別",
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(1f)
                        )
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 16.dp, start = 10.dp),
                            onClick = {
                                showCategory = true
                            }) {
                            selectedSubCategories?.let {
                                val mainIcon = SharedOptions.iconMap[it.subCategoryIcon]
                                Icon(
                                    imageVector = mainIcon!!.icon,
                                    contentDescription = it.subCategoryIcon
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                val context = LocalContext.current
                                val resourceId =
                                    context.resources.getIdentifier(
                                        it.subCategoryNameKey,
                                        "string",
                                        context.packageName
                                    )
                                val displayName = if (resourceId != 0) {
                                    context.getString(resourceId) // 如果語系字串存在，顯示語系的值
                                } else {
                                    it.subCategoryNameKey // 如果語系字串不存在，顯示原始值
                                }
                                Text(text = displayName)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp),
                            onClick = {
                                showDatePicker = true
                            }) {
                            Text(formattedDate)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 16.dp),
                            onClick = {
                                showTimePicker = true
                            }) {
                            Text(buttonText)
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                    ) {
                        OutlinedTextField(
                            value = remark,
                            onValueChange = { newRemark -> remark = newRemark },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            label = { Text("輸入備註") },
                            minLines = 3,
                            maxLines = 6
                        )
                    }
                }

                if (showAsset) {
                    AssetDialog(
                        accounts = accounts.value, // 传递账户列表
                        onDismiss = { showAsset = false },
                        onAssetSelected = { selectedAccount ->
                            selectedAsset = selectedAccount // 更新选中的资产
                            showAsset = false
                        }
                    )
                }
                if (showTransferAssetFrom) {
                    AssetDialog(
                        accounts = accounts.value, // 传递账户列表
                        onDismiss = { showTransferAssetFrom = false },
                        onAssetSelected = { selectedAccount ->
                            selectedTransferAssetFrom = selectedAccount // 更新选中的资产
                            showTransferAssetFrom = false


//                            val fromAmount = transferAmountFrom.toDoubleOrNull() ?: 0.0
//                            val conversionRate = toCurrencyRate / fromCurrencyRate
//                            val convertedAmount = fromAmount * conversionRate
//                            transferAmountTo = String.format(Locale.US, "%.2f", convertedAmount)
                        }
                    )
                }
                if (showTransferAssetTo) {
                    AssetDialog(
                        accounts = accounts.value, // 传递账户列表
                        onDismiss = { showTransferAssetTo = false },
                        onAssetSelected = { selectedAccount ->
                            selectedTransferAssetTo = selectedAccount // 更新选中的资产

//                            // 執行反向換算邏輯當失去焦點時
//                            val toAmount = transferAmountTo.toDoubleOrNull() ?: 0.0
//                            val reverseConversionRate = fromCurrencyRate / toCurrencyRate
//                            val convertedAmount = toAmount * reverseConversionRate
//                            transferAmountFrom = String.format(Locale.US, "%.2f", convertedAmount)

//                            Log.d()
                            showTransferAssetTo = false
                        }
                    )
                }

                if (showCategory) {
                    CategoryDialog(
                        mainCategories = mainCategories.value,
                        subCategories = subCategories.value, // 从 ViewModel 中获取子分类数据
                        subCategoryViewModel = subCategoryViewModel, // 传递 ViewModel，用于加载子分类
                        onDismiss = { showCategory = false },
//                        onMainCategoriesSelected = { selectedMainCategory ->
//                            selectedMainCategories = selectedMainCategory
//                            // 加载子分类
//                            subCategoryViewModel.loadSubCategoriesByMainCategoryId(selectedMainCategory.mainCategoryId)
//                        },
                        onSubCategorySelected = { selectedSubCategory ->
                            // 选择子分类后执行的操作
                            selectedSubCategories = selectedSubCategory
                            showCategory = false
                        }
                    )
                }
                if (showDatePicker) {
                    DatePicker(
                        selectedDate = selectedDate,
                        onDateSelected = { date ->
                            selectedDate = date
                            showDatePicker = false
                        },
                        onDismiss = { showDatePicker = false }
                    )
                }
                if (showTimePicker) {
                    TimePicker(
                        selectedTime = selectedTime,
                        onDismiss = { showTimePicker = false },
                        onConfirm = { time ->
                            selectedTime = time
                            showTimePicker = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryDialog(
    mainCategories: List<MainCategoryEntity>,
    subCategories: List<SubCategoryEntity>,
    subCategoryViewModel: SubCategoryViewModel,
    onDismiss: () -> Unit,
    onSubCategorySelected: (SubCategoryEntity) -> Unit
) {
    var isShowingSubCategories by remember { mutableStateOf(false) }
    var selectedMainCategoryId by remember { mutableStateOf<Int?>(null) }

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
                    Text(
                        text = if (isShowingSubCategories) "選擇子分類" else "選擇分類",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                HorizontalDivider()

                if (!isShowingSubCategories) {
                    // 显示主分类列表
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(mainCategories) { mainCategory ->
                            val context = LocalContext.current
                            val resourceId =
                                context.resources.getIdentifier(
                                    mainCategory.mainCategoryNameKey,
                                    "string",
                                    context.packageName
                                )
                            val displayName = if (resourceId != 0) {
                                context.getString(resourceId)
                            } else {
                                mainCategory.mainCategoryNameKey
                            }
                            val mainIcon = SharedOptions.iconMap[mainCategory.mainCategoryIcon]

                            ListItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // 加载子分类
                                        subCategoryViewModel.loadSubCategoriesByMainCategoryId(
                                            mainCategory.mainCategoryId
                                        )
                                        selectedMainCategoryId = mainCategory.mainCategoryId
                                        isShowingSubCategories = true
                                    },
                                headlineContent = { Text(text = displayName) },
                                leadingContent = {
                                    mainIcon?.let {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .background(
                                                    Color(android.graphics.Color.parseColor("#${mainCategory.mainCategoryBackgroundColor}")),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = it.icon,
                                                contentDescription = mainCategory.mainCategoryIcon,
                                                modifier = Modifier.size(28.dp),
                                                tint = Color(android.graphics.Color.parseColor("#${mainCategory.mainCategoryIconColor}"))
                                            )
                                        }
                                    }
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                } else {
                    // 显示子分类列表
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(subCategories) { subCategory ->
                            val subIcon = SharedOptions.iconMap[subCategory.subCategoryIcon]
                            val context = LocalContext.current
                            val resourceId =
                                context.resources.getIdentifier(
                                    subCategory.subCategoryNameKey,
                                    "string",
                                    context.packageName
                                )
                            val displayName = if (resourceId != 0) {
                                context.getString(resourceId)
                            } else {
                                subCategory.subCategoryNameKey
                            }
                            ListItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSubCategorySelected(subCategory)
                                        onDismiss()
                                    },
                                headlineContent = { Text(text = displayName) },
                                leadingContent = {
                                    subIcon?.let {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .background(
                                                    Color(android.graphics.Color.parseColor("#${subCategory.subCategoryBackgroundColor}")),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = it.icon,
                                                contentDescription = subCategory.subCategoryIconColor,
                                                modifier = Modifier.size(28.dp),
                                                tint = Color(android.graphics.Color.parseColor("#${subCategory.subCategoryIconColor}"))
                                            )
                                        }
                                    }
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }

                // 返回按钮和取消按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    if (isShowingSubCategories) {
                        TextButton(onClick = {
                            isShowingSubCategories = false
                        }) {
                            Text(text = "返回")
                        }
                    }
                    // 取消按钮
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                }
            }
        }
    }
}

//@Composable
//fun CategoryDialog(
//    mainCategories: List<MainCategoryEntity>,
//    onDismiss: () -> Unit,
//    onMainCategoriesSelected: (MainCategoryEntity) -> Unit
//){
//    Dialog(onDismissRequest = onDismiss) {
//        Surface(
//            shape = MaterialTheme.shapes.medium,
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Column(modifier = Modifier.padding(16.dp)) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 16.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(text = "選擇分類", modifier = Modifier.padding(bottom = 8.dp))
//                }
//                HorizontalDivider()
//                LazyColumn(modifier = Modifier.fillMaxWidth()) {
//                    items(mainCategories) { mainCategory ->
//                        val context = LocalContext.current
//                        val resourceId =
//                            context.resources.getIdentifier(
//                                mainCategory.mainCategoryNameKey,
//                                "string",
//                                context.packageName
//                            )
//                        val displayName = if (resourceId != 0) {
//                            context.getString(resourceId) // 如果語系字串存在，顯示語系的值
//                        } else {
//                            mainCategory.mainCategoryNameKey // 如果語系字串不存在，顯示原始值
//                        }
//                        val mainIcon = SharedOptions.iconMap[mainCategory.mainCategoryIcon]
//                        ListItem(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable {
//                                    onMainCategoriesSelected(mainCategory)
//                                    onDismiss()
//                                },
//                            headlineContent = { Text(text = displayName) },
//                            trailingContent = {
//                            },
//                            leadingContent = {
//                                mainIcon?.let {
//                                    Box(
//                                        modifier = Modifier
//                                            .size(46.dp)
//                                            .background(
//                                                Color(android.graphics.Color.parseColor("#${mainCategory.mainCategoryBackgroundColor}")),
//                                                CircleShape
//                                            ),
//                                        contentAlignment = Alignment.Center
//                                    ) {
//                                        Icon(
//                                            imageVector = it.icon,
//                                            contentDescription = mainCategory.mainCategoryIcon,
//                                            modifier = Modifier.size(28.dp), // Set icon size
//                                            tint = Color(android.graphics.Color.parseColor("#${mainCategory.mainCategoryIconColor}"))
//                                        )
//                                    }
//                                }
//                            }
//                        )
//                        HorizontalDivider()
//                    }
//                }
//                // 添加关闭按钮
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.End
//                ) {
//                    TextButton(onClick = onDismiss) {
//                        Text(text = stringResource(id = R.string.cancel))
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
fun AssetDialog(
    accounts: List<AccountEntity>,
    onDismiss: () -> Unit,
    onAssetSelected: (AccountEntity) -> Unit
) {
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
                    Text(text = "選擇資產", modifier = Modifier.padding(bottom = 8.dp))
                }
                HorizontalDivider()
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(accounts) { account ->
                        val accountIcon = SharedOptions.iconMap[account.accountIcon]
                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onAssetSelected(account)
                                    onDismiss()
                                },
                            headlineContent = { Text(text = account.accountName) },
                            supportingContent = {
                                Row {
                                    Text(text = account.currency)
                                    Text(text = "$${account.initialBalance}")
                                }
                            },
                            trailingContent = {
                            },
                            leadingContent = {
                                accountIcon?.let {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .background(
                                                Color(android.graphics.Color.parseColor("#${account.accountBackgroundColor}")),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = it.icon,
                                            contentDescription = account.accountIconColor,
                                            modifier = Modifier.size(28.dp), // Set icon size
                                            tint = Color(android.graphics.Color.parseColor("#${account.accountIconColor}"))
                                        )
                                    }
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
                // 添加关闭按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordAddScreenHeader(
    navController: NavHostController,
    onAddClick: () -> Unit
    ) {
    CenterAlignedTopAppBar(
        title = {
            Text("新增記錄")
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
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }

        }
    )
}