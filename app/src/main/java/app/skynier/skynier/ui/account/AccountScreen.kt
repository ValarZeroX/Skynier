package app.skynier.skynier.ui.account

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import app.skynier.skynier.R
import app.skynier.skynier.library.SharedOptions
import app.skynier.skynier.library.SwipeBox
import app.skynier.skynier.ui.settings.MainCategoryScreenHeader
import app.skynier.skynier.ui.theme.Blue
import app.skynier.skynier.ui.theme.Red
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
fun AccountScreen(
    navController: NavHostController,
    skynierViewModel: SkynierViewModel,
    categoryViewModel: CategoryViewModel,
    mainCategoryViewModel: MainCategoryViewModel,
    subCategoryViewModel: SubCategoryViewModel,
    accountViewModel: AccountViewModel,
    currencyViewModel: CurrencyViewModel
) {
    val accounts by accountViewModel.accounts.observeAsState(emptyList())

    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState = rememberReorderableLazyListState(
        lazyListState = lazyListState,
    ) { from, to ->
        val updatedList = accounts.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        accountViewModel.updateAccountOrder(updatedList)
    }
    LaunchedEffect(Unit) {
        accountViewModel.loadAllAccounts()
    }
    Scaffold(
        topBar = {
            AccountScreenHeader(navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column {
                LazyColumn(
                    state = lazyListState
                ) {
                    itemsIndexed(
                        accounts,
                        key = { _, accounts -> accounts.accountId }) { _, accounts ->
                        ReorderableItem(
                            reorderableLazyColumnState,
                            accounts.accountId
                        ) {
                            val accountIcon = SharedOptions.iconMap[accounts.accountIcon]
//                            val context = LocalContext.current
//                            val resourceId =
//                                context.resources.getIdentifier(
//                                    accountCategories.accountCategoryNameKey,
//                                    "string",
//                                    context.packageName
//                                )
//                            val displayName = if (resourceId != 0) {
//                                context.getString(resourceId) // 如果語系字串存在，顯示語系的值
//                            } else {
//                                accountCategories.accountCategoryNameKey // 如果語系字串不存在，顯示原始值
//                            }
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
                                                    skynierViewModel.updateSelectedAccount(accounts)
                                                    navController.navigate("account_edit")
//                                                    selectedAccountCategory = accountCategories
//                                                    showEditDialog = true
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
                                    headlineContent = { Text(text = accounts.accountName) },
                                    supportingContent = {
                                        Row {
                                            Text(text = accounts.currency)
                                            Text(text = "$${accounts.initialBalance}")
                                        }
                                    },
                                    trailingContent = {
                                        IconButton(
                                            onClick = { /*TODO*/ },
                                            modifier = Modifier.draggableHandle()
                                        ) {
                                            Icon(
                                                Icons.Filled.DragHandle,
                                                contentDescription = "Reorder",

                                                )
                                        }
                                    },
                                    leadingContent = {
                                        accountIcon?.let {
                                            Box(
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .background(
                                                        Color(android.graphics.Color.parseColor("#${accounts.accountBackgroundColor}")),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = it.icon,
                                                    contentDescription = accounts.accountIconColor,
                                                    modifier = Modifier.size(28.dp), // Set icon size
                                                    tint = Color(android.graphics.Color.parseColor("#${accounts.accountIconColor}"))
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountDeleteDialog() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreenHeader(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            Text(stringResource(id = R.string.asset_overview))
        },
//        navigationIcon = {
//            IconButton(onClick = { navController.popBackStack() }) {
//                Icon(
//                    imageVector = Icons.Filled.ArrowBackIosNew,
//                    contentDescription = "Back"
//                )
//            }
//        },
        actions = {
            IconButton(onClick = {
                navController.navigate("account_add")
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }
        }
    )
}