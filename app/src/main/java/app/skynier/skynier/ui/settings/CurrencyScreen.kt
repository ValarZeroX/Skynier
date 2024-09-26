package app.skynier.skynier.ui.settings

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import app.skynier.skynier.R
import app.skynier.skynier.ui.record.RecordAddScreenHeader
import app.skynier.skynier.viewmodels.CurrencyApiViewModel
import app.skynier.skynier.viewmodels.CurrencyViewModel
import app.skynier.skynier.viewmodels.SkynierViewModel

@Composable
fun CurrencyScreen(
    navController: NavHostController,
    skynierViewModel: SkynierViewModel,
    currencyViewModel: CurrencyViewModel,
    currencyApiViewModel: CurrencyApiViewModel
) {
    val currencyList = currencyViewModel.currencies.observeAsState(emptyList())
    val currencyRates by currencyApiViewModel.currencyRates.observeAsState()
    LaunchedEffect(Unit) {
        currencyApiViewModel.fetchCurrencyRates()
        currencyViewModel.loadAllCurrencies()
    }
    Scaffold(
        topBar = {
            CurrencyScreenHeader(
                navController,
                onAddClick = { }
                )
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
fun CurrencyScreenHeader(navController: NavHostController, onAddClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text("幣別匯率")
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
                onAddClick()
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }

        }
    )
}