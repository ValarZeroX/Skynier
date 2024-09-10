package app.skynier.skynier.ui.settings

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import app.skynier.skynier.library.SharedOptions
import app.skynier.skynier.viewmodels.SkynierViewModel

@Composable
fun IconScreen(
    navController: NavHostController,
    skynierViewModel: SkynierViewModel,
) {
    var selectedTabIconIndex by remember { mutableIntStateOf(0) }
    val categories = SharedOptions.iconMap.values.distinctBy { it.category }.map { it.category }

    Scaffold(
        topBar = {
            IconScreenHeader(navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column {
                // 顯示分類的 Tab
                ScrollableTabRow(selectedTabIndex = selectedTabIconIndex) {
                    categories.forEachIndexed { index, category ->
                        val context = LocalContext.current
                        val resourceId =
                            context.resources.getIdentifier(category, "string", context.packageName)
                        val displayName = if (resourceId != 0) {
                            context.getString(resourceId) // 如果語系字串存在，顯示語系的值
                        } else {
                            category // 如果語系字串不存在，顯示原始值
                        }
                        Tab(
                            selected = selectedTabIconIndex == index,
                            onClick = { selectedTabIconIndex = index },
                            text = { Text(displayName) }
                        )
                    }
                }

                // 根據選擇的分類顯示對應圖標
                val selectedCategory = categories[selectedTabIconIndex]
                val iconsForSelectedCategory =
                    SharedOptions.iconMap.filterValues { it.category == selectedCategory }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp), // 調整水平間距
                    verticalArrangement = Arrangement.spacedBy(14.dp) // 調整垂直間距
                ) {
                    items(iconsForSelectedCategory.values.toList()) { categoryIcon ->
                        // 這裡處理顯示圖標的邏輯
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(
                                        Color(android.graphics.Color.parseColor("#454545")),
                                        CircleShape
                                    ).clickable {
                                        skynierViewModel.setSelectedIcon(categoryIcon)
                                        navController.popBackStack()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                // Display the icon from the CategoryIcon object
                                Icon(
                                    imageVector = categoryIcon.icon, // Access the icon from CategoryIcon
                                    contentDescription = categoryIcon.category,
                                    modifier = Modifier.size(24.dp), // Set icon size
                                    tint = Color(android.graphics.Color.parseColor("#FFFFFF")) // Set icon color
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
fun IconScreenHeader(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            Text("選擇圖示")
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Back"
                )
            }
        },
    )
}