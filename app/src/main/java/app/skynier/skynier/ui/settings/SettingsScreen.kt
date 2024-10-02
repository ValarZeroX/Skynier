package app.skynier.skynier.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import app.skynier.skynier.R

@Composable
fun SettingsScreen(
    navController: NavHostController
){
    Scaffold(
        topBar = {
            SettingsScreenHeader(navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LazyColumn {
                item {
                    Row(modifier = Modifier.padding(start = 10.dp)) {
                        Text(
                            text = "設定",
                            fontSize = 24.sp
                        )
                    }
                    HorizontalDivider()
                }
                item {
                    ListItem(
                        headlineContent = { Text("類別") },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "類別",
                            )
                        },
                        modifier = Modifier.clickable {
                            navController.navigate("main_category")
                        }
                    )
                }
                item {
                    ListItem(
                        headlineContent = { Text(stringResource(id = R.string.account_category)) },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = stringResource(id = R.string.account_category),
                            )
                        },
                        modifier = Modifier.clickable {
                            navController.navigate("account_category")
                        }
                    )
                }
                item {
                    Row(modifier = Modifier.padding(start = 10.dp)) {
                        Text(
                            text = "偏好",
                            fontSize = 24.sp
                        )
                    }
                    HorizontalDivider()
                }
                item {
                    ListItem(
                        headlineContent = { Text("主題顏色") },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "主題顏色",
                            )
                        },
                        modifier = Modifier.clickable {
                            navController.navigate("theme")
                        }
                    )
                }
                item {
                    ListItem(
                        headlineContent = { Text("幣別匯率") },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "幣別匯率",
                            )
                        },
                        modifier = Modifier.clickable {
                            navController.navigate("currency")
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenHeader(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            Text("設定")
        },
//        navigationIcon = {
//            IconButton(onClick = { navController.popBackStack() }) {
//                Icon(
//                    imageVector = Icons.Filled.ArrowBackIosNew,
//                    contentDescription = "Back"
//                )
//            }
//
//        },
    )
}