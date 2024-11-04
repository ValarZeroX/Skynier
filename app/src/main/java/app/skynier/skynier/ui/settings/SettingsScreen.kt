package app.skynier.skynier.ui.settings

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import app.skynier.skynier.R
import kotlinx.coroutines.launch
import app.skynier.skynier.csv.exportCSV
import java.io.File

@Composable
fun SettingsScreen(
    navController: NavHostController,
    csvImportLauncher: ActivityResultLauncher<Intent>,
){
    val coroutineScope = rememberCoroutineScope()
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
                            text = stringResource(id = R.string.tab_setting),
                            fontSize = 24.sp
                        )
                    }
                    HorizontalDivider()
                }
                item {
                    ListItem(
                        headlineContent = { Text(stringResource(id = R.string.category)) },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = stringResource(id = R.string.category),
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
                            text = stringResource(R.string.settings_preferences),
                            fontSize = 24.sp
                        )
                    }
                    HorizontalDivider()
                }
//                item {
//                    ListItem(
//                        headlineContent = { Text("主題顏色") },
//                        trailingContent = {
//                            Icon(
//                                imageVector = Icons.Filled.ChevronRight,
//                                contentDescription = "主題顏色",
//                            )
//                        },
//                        modifier = Modifier.clickable {
//                            navController.navigate("theme")
//                        }
//                    )
//                }
                item {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_currency)) },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = stringResource(R.string.settings_currency),
                            )
                        },
                        modifier = Modifier.clickable {
                            navController.navigate("currency")
                        }
                    )
                }
                item {
                    Row(modifier = Modifier.padding(start = 10.dp)) {
                        Text(
                            text = stringResource(id = R.string.backup),
                            fontSize = 24.sp
                        )
                    }
                    HorizontalDivider()
                }
                item{
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_export_csv)) },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = stringResource(R.string.settings_export_csv),
                            )
                        },
                        modifier = Modifier.clickable {
                            coroutineScope.launch {
                                val csvFile = exportCSV(navController.context)
                                if (csvFile.exists()) {
                                    shareCSVFile(navController.context, csvFile)
                                }
                            }
                        }
                    )
                }
                item{
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.settings_import_csv)) },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = stringResource(R.string.settings_import_csv),
                            )
                        },
                        modifier = Modifier.clickable {
                            coroutineScope.launch {
                                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                    addCategory(Intent.CATEGORY_OPENABLE)
                                    type = "text/csv"
                                }
                                csvImportLauncher.launch(intent)
                            }
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
            Text(stringResource(R.string.tab_setting))
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

fun shareCSVFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share CSV via"))
}
