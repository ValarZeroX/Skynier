package app.skynier.skynier.ui.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import app.skynier.skynier.R
import app.skynier.skynier.database.entities.AccountCategoryEntity
import app.skynier.skynier.database.entities.SubCategoryEntity
import app.skynier.skynier.library.CategoryIcon
import app.skynier.skynier.library.SharedOptions
import app.skynier.skynier.library.SwipeBox
import app.skynier.skynier.ui.account.AccountAddScreenHeader
import app.skynier.skynier.ui.theme.Blue
import app.skynier.skynier.ui.theme.Gray
import app.skynier.skynier.ui.theme.Red
import app.skynier.skynier.viewmodels.AccountCategoryViewModel
import app.skynier.skynier.viewmodels.AccountViewModel
import app.skynier.skynier.viewmodels.CategoryViewModel
import app.skynier.skynier.viewmodels.CurrencyViewModel
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.SkynierViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountCategoryScreen(
    navController: NavHostController,
    skynierViewModel: SkynierViewModel,
    accountViewModel: AccountViewModel,
    accountCategoryViewModel: AccountCategoryViewModel,
) {
    val accountCategories = accountCategoryViewModel.accountCategories.observeAsState(emptyList())
    LaunchedEffect(Unit) {
        accountCategoryViewModel.loadAllAccountCategories()
    }

    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState = rememberReorderableLazyListState(
        lazyListState = lazyListState,
    ) { from, to ->
        val updatedList = accountCategories.value.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        accountCategoryViewModel.updateAccountCategoryOrder(updatedList)
    }

    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AccountCategoryScreenHeader(navController, onAddClick = { showAddDialog = true })
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = lazyListState
            ) {
                itemsIndexed(
                    accountCategories.value,
                    key = { _, accountCategories -> accountCategories.accountCategoryId }) { _, accountCategories ->
                    ReorderableItem(
                        reorderableLazyColumnState,
                        accountCategories.accountCategoryId
                    ) {
                        val context = LocalContext.current
                        val resourceId =
                            context.resources.getIdentifier(accountCategories.accountCategoryNameKey, "string", context.packageName)
                        val displayName = if (resourceId != 0) {
                            context.getString(resourceId) // 如果語系字串存在，顯示語系的值
                        } else {
                            accountCategories.accountCategoryNameKey // 如果語系字串不存在，顯示原始值
                        }
                        var checked by remember { mutableStateOf(false) }
                        SwipeBox(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            bottomContent = {
                                Row {
                                    Box(
                                        modifier = Modifier
                                            .width(70.dp)
                                            .fillMaxHeight()
                                            .background(Blue)
                                            .clickable {
//                                                editCategory()
//                            stockViewModel.updateSelectedAccount(item)
//                            navController.navigate("editAccountScreen")
                                            }
                                    ) {
                                        Column(
                                            modifier = Modifier.align(Alignment.Center),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Edit,
                                                contentDescription = "Edit"
                                            )
                                            Text(
                                                text = stringResource(id = R.string.edit),
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(70.dp)
                                            .fillMaxHeight()
                                            .background(Red)
                                            .clickable {
                                                //彈跳Dialog視窗告知該帳戶有幾筆交易紀錄
//                                                stockAccountViewModel.deleteStockAccountById(item.accountId)
//                                                checked = false
//                            selectedAccountName = item.account
//                            selectedAccountId = item.accountId
//                            showDialog = true
                                            }
                                    ) {
                                        Column(
                                            modifier = Modifier.align(Alignment.Center),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = "Delete"
                                            )
                                            Text(
                                                text = stringResource(id = R.string.delete),
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }
                            }
                        ) {
                            ListItem(
                                headlineContent = { Text(text = displayName) },
//                                supportingContent = { Text(text = subcategoryText) },
                                trailingContent = {
                                    IconButton(onClick = { /*TODO*/ },
                                        modifier = Modifier.draggableHandle()) {
                                        Icon(
                                            Icons.Filled.DragHandle,
                                            contentDescription = "Reorder",

                                            )
                                    }
                                },
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
            if (showAddDialog) {
                AddAccountCategory(
                    onDismiss = { showAddDialog = false },
                    onAdd = { name ->
                        val accountCategorySort = accountCategories.value.size + 1
                        accountCategoryViewModel.insertAccountCategory(
                            AccountCategoryEntity(
                                accountCategoryNameKey = name,
                                accountCategorySort = accountCategorySort
                            )
                        )
                        showAddDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun AddAccountCategory(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    val untitled = stringResource(id = R.string.untitled)
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
                        text = "新增帳戶類別",
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
                            if (name.isEmpty()) {
                                name = untitled
                            }
                            onAdd(name)
                        }
                    ) {
                        Text(stringResource(id = R.string.add))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountCategoryScreenHeader(
    navController: NavHostController,
    onAddClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(stringResource(id = R.string.account_category))
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
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }
        }
    )
}