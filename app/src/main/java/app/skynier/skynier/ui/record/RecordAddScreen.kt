package app.skynier.skynier.ui.record

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import app.skynier.skynier.R
import app.skynier.skynier.viewmodels.CategoryViewModel
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.SkynierViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel

@Composable
fun RecordAddScreen(
    navController: NavHostController,
    skynierViewModel: SkynierViewModel,
    categoryViewModel: CategoryViewModel,
    mainCategoryViewModel: MainCategoryViewModel,
    subCategoryViewModel: SubCategoryViewModel
) {
    val categories = categoryViewModel.categories.observeAsState(emptyList())
    LaunchedEffect(Unit) {
        categoryViewModel.loadAllCategories()
    }
    var selectedTabIconIndex by remember { mutableIntStateOf(0) }

    val numericRegex = Regex("^-?\\d*\\.?\\d*$")
    var amount by remember {
        mutableStateOf("0")
    }
    Log.d("categories", "$categories")
    Scaffold(
        topBar = {
            RecordAddScreenHeader(navController)
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
                            },
                            text = { Text(text = displayName) }
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    item {
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
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordAddScreenHeader(navController: NavHostController) {
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

            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }

        }
    )
}