package app.skynier.skynier.ui.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import app.skynier.skynier.viewmodels.AccountCategoryViewModel
import app.skynier.skynier.viewmodels.AccountViewModel
import app.skynier.skynier.viewmodels.CategoryViewModel
import app.skynier.skynier.viewmodels.CurrencyViewModel
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.RecordViewModel
import app.skynier.skynier.viewmodels.SkynierViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun RecordMainScreen(
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
//    val selectedDate by remember { mutableStateOf(LocalDate.now()) }
//    val localDate = LocalDate.now()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    Scaffold(
        topBar = {
            RecordMainScreen(
                navController,
                selectedDate,
                onClickPrev = {
                    selectedDate = selectedDate.minusMonths(1)
                },
                onClickNext = {
                    selectedDate = selectedDate.plusMonths(1)
                },
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("日") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("月") }
                    )
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        text = { Text("年") }
                    )
                    Tab(
                        selected = selectedTabIndex == 3,
                        onClick = { selectedTabIndex = 3 },
                        text = { Text("全部") }
                    )
                }
                when (selectedTabIndex) {
                    0 -> {
                        RecordDayScreen(selectedDate)
                    }
                    1 -> {
                        Text("月")
                    }
                    2 -> {
                        Text("年")
                    }
                    3 -> {
                        Text("全部")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordMainScreen(
    navController: NavHostController,
    localDate: LocalDate,
    onClickNext: () -> Unit,
    onClickPrev: () -> Unit,
) {
    val currentLocale = Locale.getDefault()
    val dateFormatter = DateTimeFormatter.ofPattern("MMM, yyyy", currentLocale)
    val formattedDate = localDate.format(dateFormatter)
    CenterAlignedTopAppBar(
        title = {
            Row {
                IconButton(onClick = onClickPrev) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
                }
                Text(text = formattedDate)
                IconButton(onClick = onClickNext) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
                }
            }
        },
//        navigationIcon = {
//            IconButton(onClick = { navController.popBackStack() }) {
//                Icon(
//                    imageVector = Icons.Filled.ArrowBackIosNew,
//                    contentDescription = "Back"
//                )
//            }
//        },
//        actions = {
//            IconButton(onClick = {
//                navController.navigate("account_add")
//            }) {
//                Icon(
//                    imageVector = Icons.Filled.Add,
//                    contentDescription = "Add"
//                )
//            }
//        }
    )
}