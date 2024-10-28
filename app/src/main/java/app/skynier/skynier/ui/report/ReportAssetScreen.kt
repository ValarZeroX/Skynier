package app.skynier.skynier.ui.report

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import app.skynier.skynier.database.entities.AccountEntity
import app.skynier.skynier.database.entities.CurrencyEntity
import app.skynier.skynier.database.entities.RecordEntity
import app.skynier.skynier.database.entities.UserSettingsEntity
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.RecordViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel

@Composable
fun ReportAssetScreen(
    recordTotal: List<RecordEntity>,
    mainCategoryViewModel: MainCategoryViewModel,
    subCategoryViewModel: SubCategoryViewModel,
    userSettings: UserSettingsEntity?,
    currencyList: List<CurrencyEntity>,
    navController: NavHostController,
    accounts: List<AccountEntity>,
    recordViewModel: RecordViewModel,
) {
    Column {

    }
}