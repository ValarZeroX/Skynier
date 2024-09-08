package app.skynier.skynier

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.skynier.skynier.layouts.NavigationBarScreen
import app.skynier.skynier.record.AddRecordScreen
import app.skynier.skynier.settings.MainCategoryScreen
import app.skynier.skynier.settings.SettingsScreen
import app.skynier.skynier.settings.ThemeScreen
import app.skynier.skynier.viewmodels.MainCategoryViewModel

@Composable
fun MainScreen(
    mainCategoryViewModel: MainCategoryViewModel
) {
    val selectedItemIndex = rememberSaveable { mutableIntStateOf(0) }
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBarScreen(navController, selectedItemIndex)
        },
        topBar = {
            TopBar(navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Navigation(navController, mainCategoryViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    CenterAlignedTopAppBar(
        title = {
            when (currentRoute) {
                "home" -> Text("Home")
                "report" -> Text("Report")
                "settings" -> Text("Settings")
                "theme" -> Text("主題顏色")
                else -> Text("Skynier")
            }
        },
        navigationIcon = {
            if (currentRoute != "home" && currentRoute != "settings") {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
            }
        },
    )
}

@Composable
fun Navigation(
    navController: NavHostController,
    mainCategoryViewModel: MainCategoryViewModel
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen() }
        composable("report") { ReportScreen() }
        composable("settings") { SettingsScreen(navController) }
        composable("theme") { ThemeScreen(navController) }
        composable("add_record") { AddRecordScreen(navController) }
        composable("main_category") { MainCategoryScreen(navController, mainCategoryViewModel) }


    }
}

@Composable
fun HomeScreen() {
    Text(
        text = "在 WordPress 網站展示漂亮的中文字型，一直是每位網站管理者優先處理的工作之一，在 google fonts – 如何為佈景主題新增中文字型這一篇文章，已經跟大家分享了如何的幫自",
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun ReportScreen() {
    Text(
        text = "Report Page Noto Sans Traditional Chinese",
        style = MaterialTheme.typography.bodyLarge
    )
}

