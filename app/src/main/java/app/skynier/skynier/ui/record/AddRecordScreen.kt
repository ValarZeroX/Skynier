package app.skynier.skynier.ui.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import app.skynier.skynier.R
import app.skynier.skynier.ui.settings.MainCategoryScreenHeader

@Composable
fun AddRecordScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            AddRecordScreenHeader(navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column {
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreenHeader(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            Text("新增記錄")
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

            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }

        }
    )
}