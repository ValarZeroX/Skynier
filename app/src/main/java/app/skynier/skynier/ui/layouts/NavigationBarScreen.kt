package app.skynier.skynier.ui.layouts


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import app.skynier.skynier.R

@Composable
fun NavigationBarScreen(navController: NavHostController, selectedItemIndex: MutableState<Int>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Box {
        NavigationBar {
            NavigationBarItem(
                icon = { Icon(Icons.Filled.AccountBalanceWallet, contentDescription = "Account") },
                label = { Text("Accounts") },
                selected = selectedItemIndex.value == 0,
                onClick = {
                    selectedItemIndex.value = 0
                    navController.navigate("home")
                }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = "CalendarMonth") },
                label = { Text("Records") },
                selected = selectedItemIndex.value == 1,
                onClick = {
                    selectedItemIndex.value = 1
                    navController.navigate("settings")
                }
            )
//        Spacer(modifier = Modifier.weight(1f, true)) // 用來保持中間位置
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Add, contentDescription = "add") },
                label = { Text("Stocks") },
                selected = selectedItemIndex.value == 2,
                onClick = {
                    selectedItemIndex.value = 2
                    navController.navigate("report")
                }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Analytics, contentDescription = "Analysis") },
                label = { Text("Analysis") },
                selected = selectedItemIndex.value == 3,
                onClick = {
                    selectedItemIndex.value = 3
                    navController.navigate("settings")
                }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                label = { Text(stringResource(id = R.string.tab_setting)) },
                selected = selectedItemIndex.value == 4,
                onClick = {
                    selectedItemIndex.value = 4
                    navController.navigate("settings")
                }
            )
        }
        val excludedRoutes = setOf("main_category", "icon", "sub_category/{mainCategoryId}/{mainCategoryName}")
        if (currentRoute !in excludedRoutes) {
            FloatingActionButton(
                onClick = {
                    selectedItemIndex.value = 5
                    navController.navigate("add_record")
                },
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = (-90).dp, x = (-20).dp)
                    .size(66.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Record")
            }
        }
    }
}

