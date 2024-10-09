package app.skynier.skynier

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.skynier.skynier.ui.account.AccountAddScreen
import app.skynier.skynier.ui.account.AccountEditScreen
import app.skynier.skynier.ui.account.AccountScreen
import app.skynier.skynier.ui.layouts.NavigationBarScreen
import app.skynier.skynier.ui.record.RecordAddScreen
import app.skynier.skynier.ui.record.RecordEditScreen
import app.skynier.skynier.ui.record.RecordMainScreen
import app.skynier.skynier.ui.settings.AccountCategoryScreen
import app.skynier.skynier.ui.settings.CurrencyScreen
import app.skynier.skynier.ui.settings.IconScreen
import app.skynier.skynier.ui.settings.MainCategoryScreen
import app.skynier.skynier.ui.settings.SettingsScreen
import app.skynier.skynier.ui.settings.SubCategoryScreen
import app.skynier.skynier.ui.settings.ThemeScreen
import app.skynier.skynier.viewmodels.AccountCategoryViewModel
import app.skynier.skynier.viewmodels.AccountViewModel
import app.skynier.skynier.viewmodels.CategoryViewModel
import app.skynier.skynier.viewmodels.CurrencyApiViewModel
import app.skynier.skynier.viewmodels.CurrencyViewModel
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.RecordViewModel
import app.skynier.skynier.viewmodels.SkynierViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import app.skynier.skynier.viewmodels.UserSettingsViewModel

@Composable
fun MainScreen(
    skynierViewModel: SkynierViewModel,
    categoryViewModel: CategoryViewModel,
    mainCategoryViewModel: MainCategoryViewModel,
    subCategoryViewModel: SubCategoryViewModel,
    accountViewModel: AccountViewModel,
    currencyViewModel: CurrencyViewModel,
    accountCategoryViewModel: AccountCategoryViewModel,
    currencyApiViewModel: CurrencyApiViewModel,
    userSettingsViewModel: UserSettingsViewModel,
    recordViewModel: RecordViewModel,
) {
    val selectedItemIndex = rememberSaveable { mutableIntStateOf(0) }
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBarScreen(navController, selectedItemIndex)
        },
//        topBar = {
//            TopBar(navController)
//        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(PaddingValues(bottom = innerPadding.calculateBottomPadding()))) {
//        Box(modifier = Modifier.padding(innerPadding)) {
            Navigation(
                navController,
                skynierViewModel,
                categoryViewModel,
                mainCategoryViewModel,
                subCategoryViewModel,
                accountViewModel,
                currencyViewModel,
                accountCategoryViewModel,
                currencyApiViewModel,
                userSettingsViewModel,
                recordViewModel
            )
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TopBar(navController: NavHostController) {
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//
//    CenterAlignedTopAppBar(
//        title = {
//            when (currentRoute) {
//                "home" -> Text("Home")
//                "report" -> Text("Report")
//                "settings" -> Text("Settings")
//                "theme" -> Text("主題顏色")
//                "main_category" -> Text("主類別")
//                else -> Text("Skynier")
//            }
//        },
//        navigationIcon = {
//            if (currentRoute != "home" && currentRoute != "settings") {
//                IconButton(onClick = { navController.popBackStack() }) {
//                    Icon(
//                        imageVector = Icons.Filled.ArrowBackIosNew,
//                        contentDescription = "Back"
//                    )
//                }
//            }
//        },
//        actions = {
//            if (currentRoute == "main_category") {
//                IconButton(onClick = { }) {
//                    Icon(
//                        imageVector = Icons.Filled.Add,
//                        contentDescription = "Add"
//                    )
//                }
//            }
//        }
//    )
//}

@Composable
fun Navigation(
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
    recordViewModel: RecordViewModel,
) {
    NavHost(navController = navController, startDestination = "account") {
        composable("report") { ReportScreen() }
        composable("settings") { SettingsScreen(navController) }
        composable("theme") { ThemeScreen(navController) }
        composable("record_add") {
            RecordAddScreen(
                navController,
                skynierViewModel,
                categoryViewModel,
                mainCategoryViewModel,
                subCategoryViewModel,
                accountCategoryViewModel,
                accountViewModel,
                currencyViewModel,
                recordViewModel
            )
        }
        composable("record_edit/{recordId}/{inRecordId}") { backStackEntry ->
            val recordId = backStackEntry.arguments?.getString("recordId")
            val inRecordId = backStackEntry.arguments?.getString("inRecordId")
            RecordEditScreen(
                navController,
                skynierViewModel,
                categoryViewModel,
                mainCategoryViewModel,
                subCategoryViewModel,
                accountCategoryViewModel,
                accountViewModel,
                currencyViewModel,
                recordViewModel,
                recordId?.toInt() ?: 0,
                inRecordId?.toInt() ?: 0,
            )
        }
        composable("main_category") {
            MainCategoryScreen(
                navController,
                skynierViewModel,
                categoryViewModel,
                mainCategoryViewModel,
                subCategoryViewModel
            )
        }
        composable("sub_category/{mainCategoryId}/{mainCategoryName}/{mainCategoryBackgroundColor}/{mainCategoryIconColor}") { backStackEntry ->
            val mainCategoryId = backStackEntry.arguments?.getString("mainCategoryId")
            val mainCategoryName = backStackEntry.arguments?.getString("mainCategoryName")
            val mainCategoryBackgroundColor =
                backStackEntry.arguments?.getString("mainCategoryBackgroundColor")
            val mainCategoryIconColor = backStackEntry.arguments?.getString("mainCategoryIconColor")
            SubCategoryScreen(
                navController,
                skynierViewModel,
                categoryViewModel,
                mainCategoryViewModel,
                subCategoryViewModel,
                mainCategoryId?.toInt() ?: 0,
                mainCategoryName?.toString() ?: "",
                mainCategoryBackgroundColor?.toString() ?: "000000",
                mainCategoryIconColor?.toString() ?: "FBFBFB",
            )
        }
        composable("icon") { IconScreen(navController, skynierViewModel) }
        composable("account") {
            AccountScreen(
                navController,
                skynierViewModel,
                categoryViewModel,
                mainCategoryViewModel,
                subCategoryViewModel,
                accountViewModel,
                currencyViewModel
            )
        }
        composable("account_add") {
            AccountAddScreen(
                navController,
                skynierViewModel,
                categoryViewModel,
                mainCategoryViewModel,
                subCategoryViewModel,
                accountViewModel,
                currencyViewModel,
                accountCategoryViewModel,
                currencyApiViewModel,
                userSettingsViewModel
            )
        }
        composable("account_category") {
            AccountCategoryScreen(
                navController,
                skynierViewModel,
                accountViewModel,
                accountCategoryViewModel
            )
        }
        composable("account_edit") {
            AccountEditScreen(
                navController,
                skynierViewModel,
                categoryViewModel,
                mainCategoryViewModel,
                subCategoryViewModel,
                accountViewModel,
                currencyViewModel,
                accountCategoryViewModel,
                currencyApiViewModel
            )
        }
        composable("currency") {
            CurrencyScreen(
                navController,
                skynierViewModel,
                currencyViewModel,
                currencyApiViewModel,
                userSettingsViewModel
            )
        }
        composable("record") {
            RecordMainScreen(
                navController,
                skynierViewModel,
                categoryViewModel,
                mainCategoryViewModel,
                subCategoryViewModel,
                accountCategoryViewModel,
                accountViewModel,
                currencyViewModel,
                recordViewModel,
                userSettingsViewModel
            )
        }
    }
}

//@Composable
//fun HomeScreen() {
//    Text(
//        text = "在 WordPress 網站展示漂亮的中文字型，一直是每位網站管理者優先處理的工作之一，在 google fonts – 如何為佈景主題新增中文字型這一篇文章，已經跟大家分享了如何的幫自",
//        style = MaterialTheme.typography.bodyLarge
//    )
//}

@Composable
fun ReportScreen() {
    Text(
        text = "Report Page Noto Sans Traditional Chinese",
        style = MaterialTheme.typography.bodyLarge
    )
}

