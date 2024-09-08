package app.skynier.skynier.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import app.skynier.skynier.viewmodels.MainCategoryViewModel

@Composable
fun SettingsScreen(
    navController: NavHostController
){
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
    }
}