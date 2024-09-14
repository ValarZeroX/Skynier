package app.skynier.skynier.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import app.skynier.skynier.R
import app.skynier.skynier.library.SharedOptions
import app.skynier.skynier.ui.theme.Gray
import app.skynier.skynier.viewmodels.AccountViewModel
import app.skynier.skynier.viewmodels.CategoryViewModel
import app.skynier.skynier.viewmodels.CurrencyViewModel
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.SkynierViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel

@Composable
fun AccountAddScreen(
    navController: NavHostController,
    skynierViewModel: SkynierViewModel,
    categoryViewModel: CategoryViewModel,
    mainCategoryViewModel: MainCategoryViewModel,
    subCategoryViewModel: SubCategoryViewModel,
    accountViewModel: AccountViewModel,
    currencyViewModel: CurrencyViewModel
) {
    val selectedIcon by skynierViewModel.selectedIcon.observeAsState()
    val displayIcon = selectedIcon ?: SharedOptions.iconMap["Restaurant"] // 使用預設
    var hexCode by rememberSaveable {
        mutableStateOf("009EEC")
    }

    var name by rememberSaveable { mutableStateOf("") }
    val untitled = stringResource(id = R.string.untitled)
    Scaffold(
        topBar = {
            AccountAddScreenHeader(navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center // Center the label
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp) // Set the size of the circular background
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
                                modifier = Modifier.size(58.dp),
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
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountAddScreenHeader(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            Text("新增帳戶")
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
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }
        }
    )
}