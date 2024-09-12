package app.skynier.skynier.ui.settings

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Restaurant
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import app.skynier.skynier.Navigation
import app.skynier.skynier.R
import app.skynier.skynier.database.entities.MainCategoryEntity
import app.skynier.skynier.library.CategoryIcon
import app.skynier.skynier.library.SharedOptions
import app.skynier.skynier.library.SwipeBox
import app.skynier.skynier.ui.layouts.NavigationBarScreen
import app.skynier.skynier.ui.theme.Blue
import app.skynier.skynier.ui.theme.Gray
import app.skynier.skynier.ui.theme.Red
import app.skynier.skynier.viewmodels.CategoryViewModel
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.SkynierViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.math.RoundingMode

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainCategoryScreen(
    navController: NavHostController,
    skynierViewModel: SkynierViewModel,
    categoryViewModel: CategoryViewModel,
    mainCategoryViewModel: MainCategoryViewModel,
    subCategoryViewModel: SubCategoryViewModel
) {
    val categories = categoryViewModel.categories.observeAsState(emptyList())
    var selectedTabCategoryIndex by rememberSaveable { mutableIntStateOf(0) }


    val mainCategories = mainCategoryViewModel.mainCategories.observeAsState(emptyList())
    // 載入資料
    LaunchedEffect(selectedTabCategoryIndex) {
        mainCategoryViewModel.loadMainCategoriesByMainCategoryId(selectedTabCategoryIndex + 1)
    }
    LaunchedEffect(Unit) {
        categoryViewModel.loadAllCategories()
    }

    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    val newCategoryName by rememberSaveable { mutableStateOf("") }

    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState = rememberReorderableLazyListState(
        lazyListState = lazyListState,
    ) { from, to ->
        val updatedList = mainCategories.value.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        mainCategoryViewModel.updateMainCategoryOrder(updatedList)
    }
    Scaffold(
        topBar = {
            MainCategoryScreenHeader(navController, onAddClick = { showAddDialog = true })
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column {
                TabRow(selectedTabIndex = selectedTabCategoryIndex) {
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
                            selected = selectedTabCategoryIndex == index,
                            onClick = {
                                selectedTabCategoryIndex = index
                            },
                            text = { Text(text = displayName) }
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state = lazyListState
                ) {
                    itemsIndexed(
                        mainCategories.value,
                        key = { _, category -> category.mainCategoryId }) { _, category ->
                        ReorderableItem(
                            reorderableLazyColumnState,
                            category.mainCategoryId
                        ) {
                            MainCategoryItem(
                                navController,
                                skynierViewModel,
                                category,
                                this,
                                mainCategoryViewModel
                            )
                        }
                    }
                }
            }
        }
    }
    if (showAddDialog) {
        AddCategory(
            navController,
            skynierViewModel,
            onDismiss = { showAddDialog = false },
            onAdd = { name, hexCode, selectedIcon ->
                val selectedIconKey =
                    SharedOptions.iconMap.entries.find { it.value == selectedIcon }?.key
                val displayedHexCode = hexCode.takeLast(6).uppercase()
                val mainCategorySort = mainCategories.value.size + 1
                mainCategoryViewModel.insertMainCategory(
                    MainCategoryEntity(
                        categoryId = selectedTabCategoryIndex + 1,
                        mainCategoryIcon = selectedIconKey ?: "Restaurant",
                        mainCategoryNameKey = name,
                        mainCategoryBackgroundColor = displayedHexCode,
                        mainCategoryIconColor = "FFFFFF",
                        mainCategorySort = mainCategorySort,
                    )
                )
                showAddDialog = false
            },
            initialName = newCategoryName
        )
    }
}

@Composable
fun AddCategory(
    navController: NavHostController,
    skynierViewModel: SkynierViewModel,
    onDismiss: () -> Unit,
    onAdd: (String, String, CategoryIcon?) -> Unit,
    initialName: String
) {
    val selectedIcon by skynierViewModel.selectedIcon.observeAsState()
    val displayIcon = selectedIcon ?: SharedOptions.iconMap["Restaurant"] // 使用預設

    var name by rememberSaveable { mutableStateOf(initialName) }
    var hexCode by rememberSaveable {
        mutableStateOf("009EEC")
    }
//    var tempHexCode by remember { mutableStateOf(hexCode) }
    val controller = rememberColorPickerController()
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
                        text = "新增主類別",
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
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
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
//                Row(
//                    horizontalArrangement = Arrangement.Center,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 16.dp)
//                ) {
//                    OutlinedTextField(
//                        value = hexCode.takeLast(6).uppercase(),
//                        onValueChange = { newText ->
//                            if (newText.isEmpty() || newText.matches(Regex("^#[0-9A-Fa-f]{6}$"))) {
//                                hexCode = newText
//                            }
//                        },
//                        leadingIcon = { Text(text = "#")},
//                        placeholder = { Text(text = hexCode)},
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(end = 5.dp)
//                    )
//                }
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
                            onAdd(name, hexCode, displayIcon)
                        }
                    ) {
                        Text(stringResource(id = R.string.add))
                    }
                }
            }
        }
    }
}

@Composable
fun MainCategoryItem(
    navController: NavHostController,
    skynierViewModel: SkynierViewModel,
    category: MainCategoryEntity,
    scope: ReorderableCollectionItemScope,
    mainCategoryViewModel: MainCategoryViewModel
) {

    val categoryIcon = SharedOptions.iconMap[category.mainCategoryIcon]
    val context = LocalContext.current
    val resourceId =
        context.resources.getIdentifier(category.mainCategoryNameKey, "string", context.packageName)
    val displayName = if (resourceId != 0) {
        context.getString(resourceId) // 如果語系字串存在，顯示語系的值
    } else {
        category.mainCategoryNameKey // 如果語系字串不存在，顯示原始值
    }

    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    fun editCategory() {
        skynierViewModel.updateSelectedIcon(categoryIcon!!)
        skynierViewModel.selectedMainCategoryToEdit = category
        showEditDialog = true
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
                            editCategory()
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
            modifier = Modifier.clickable {
                navController.navigate("sub_category/${category.mainCategoryId}/${displayName}")
            },
            headlineContent = { Text(text = displayName) },
            supportingContent = { Text("Secondary text") },
            trailingContent = {
                IconButton(onClick = { /*TODO*/ },
                    modifier = with(scope) {
                        Modifier.draggableHandle()
                    }) {
                    Icon(
                        Icons.Filled.DragHandle,
                        contentDescription = "Reorder",

                        )
                }
            },
            leadingContent = {
                categoryIcon?.let { // Ensure categoryIcon is not null
                    Box(
                        modifier = Modifier
                            .size(46.dp) // Set the size of the circular background
                            .background(
                                Color(android.graphics.Color.parseColor("#${category.mainCategoryBackgroundColor}")),
                                CircleShape
                            ), // Set background color and shape
                        contentAlignment = Alignment.Center
                    ) {
                        // Display the icon from the CategoryIcon object
                        Icon(
                            imageVector = it.icon, // Access the icon from CategoryIcon
                            contentDescription = category.mainCategoryNameKey,
                            modifier = Modifier.size(28.dp), // Set icon size
                            tint = Color(android.graphics.Color.parseColor("#${category.mainCategoryIconColor}")) // Set icon color
                        )
                    }
                }
            }
        )
    }
    HorizontalDivider()
    // 彈出編輯對話框
    if (showEditDialog) {
        EditMainCategory(
            navController = navController,
            skynierViewModel = skynierViewModel,
            onDismiss = { showEditDialog = false },
            onUpdate = { updatedCategory ->
                mainCategoryViewModel.updateMainCategory(updatedCategory)
                showEditDialog = false
            },
            categoryIcon = categoryIcon
        )
    }
}

@Composable
fun EditMainCategory(
    navController: NavHostController,
    skynierViewModel: SkynierViewModel,
    onDismiss: () -> Unit,
    onUpdate: (MainCategoryEntity) -> Unit,
    categoryIcon: CategoryIcon?,
) {
    val category = skynierViewModel.selectedMainCategoryToEdit
    val selectedIcon by skynierViewModel.selectedIcon.observeAsState()
    Log.d("selectedIcon", "$selectedIcon")
    Log.d("categoryIcon", "$categoryIcon")
    val displayIcon = selectedIcon ?: categoryIcon // 使用預設
    var name by rememberSaveable { mutableStateOf(category!!.mainCategoryNameKey) }
    var hexCode by rememberSaveable { mutableStateOf(category!!.mainCategoryBackgroundColor) }
    val controller = rememberColorPickerController()
    val untitled = stringResource(id = R.string.untitled)

    val context = LocalContext.current
    val resourceId =
        context.resources.getIdentifier(name, "string", context.packageName)
    name = if (resourceId != 0) {
        context.getString(resourceId) // 如果語系字串存在，顯示語系的值
    } else {
        name // 如果語系字串不存在，顯示原始值
    }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 標題
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "編輯主類別", modifier = Modifier.padding(bottom = 8.dp))
                }
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
                                imageVector = it.icon, // Display the selected icon
                                contentDescription = "Icon",
                                modifier = Modifier.size(28.dp),
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
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                // 顏色選擇
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope ->
                        hexCode = colorEnvelope.hexCode
                    },
                    initialColor = Color(android.graphics.Color.parseColor("#$hexCode")),
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
                // 保存按鈕
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
                            val selectedIconKey =
                                SharedOptions.iconMap.entries.find { it.value == selectedIcon }?.key
                            val updatedCategory = category!!.copy(
                                mainCategoryNameKey = name,
                                mainCategoryBackgroundColor = hexCode.takeLast(6).uppercase(),
                                mainCategoryIcon = selectedIconKey?: "Restaurant"
                            )
                            onUpdate(updatedCategory)
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
fun MainCategoryScreenHeader(navController: NavHostController, onAddClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text("主類別")
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
