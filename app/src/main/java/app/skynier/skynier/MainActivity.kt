package app.skynier.skynier

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import app.skynier.skynier.api.RetrofitInstance
import app.skynier.skynier.csv.importDataFromCSV
import app.skynier.skynier.database.AppDatabase
import app.skynier.skynier.repository.AccountCategoryRepository
import app.skynier.skynier.repository.AccountRepository
import app.skynier.skynier.repository.CategoryRepository
import app.skynier.skynier.repository.CurrencyApiRepository
import app.skynier.skynier.repository.CurrencyRepository
import app.skynier.skynier.repository.MainCategoryRepository
import app.skynier.skynier.repository.RecordRepository
import app.skynier.skynier.repository.SubCategoryRepository
import app.skynier.skynier.repository.UserSettingsRepository
import app.skynier.skynier.ui.theme.SkynierTheme
import app.skynier.skynier.viewmodels.AccountCategoryViewModel
import app.skynier.skynier.viewmodels.AccountCategoryViewModelFactory
import app.skynier.skynier.viewmodels.AccountViewModel
import app.skynier.skynier.viewmodels.AccountViewModelFactory
import app.skynier.skynier.viewmodels.CategoryViewModel
import app.skynier.skynier.viewmodels.CategoryViewModelFactory
import app.skynier.skynier.viewmodels.CurrencyApiViewModel
import app.skynier.skynier.viewmodels.CurrencyApiViewModelFactory
import app.skynier.skynier.viewmodels.CurrencyViewModel
import app.skynier.skynier.viewmodels.CurrencyViewModelFactory
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.MainCategoryViewModelFactory
import app.skynier.skynier.viewmodels.RecordViewModel
import app.skynier.skynier.viewmodels.RecordViewModelFactory
import app.skynier.skynier.viewmodels.SkynierViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModelFactory
import app.skynier.skynier.viewmodels.UserSettingsViewModel
import app.skynier.skynier.viewmodels.UserSettingsViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var mainCategoryViewModel: MainCategoryViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var subCategoryViewModel: SubCategoryViewModel
    private lateinit var accountViewModel: AccountViewModel
    private lateinit var currencyViewModel: CurrencyViewModel
    private lateinit var accountCategoryViewModel: AccountCategoryViewModel
    private lateinit var currencyApiViewModel: CurrencyApiViewModel
    private lateinit var userSettingsViewModel: UserSettingsViewModel
    private lateinit var recordViewModel: RecordViewModel
    private lateinit var csvImportLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        csvImportLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    lifecycleScope.launch {
                        importDataFromCSV(applicationContext, uri)
                        // 在这里调用你的CSV导入函数，比如 importStockRecordsFromCSV(applicationContext, uri)
                    }
                }
            }
        }

        // 数据库和 Repository 的初始化
        val database = AppDatabase.getDatabase(this)
        val categoryRepository = CategoryRepository(database.categoryDao())
        val mainCategoryRepository = MainCategoryRepository(database.mainCategoryDao())
        val subCategoryRepository = SubCategoryRepository(database.subCategoryDao())
        val accountRepository = AccountRepository(database.accountDao())
        val currencyRepository = CurrencyRepository(database.currencyDao())
        val accountCategoryRepository = AccountCategoryRepository(database.accountCategoryDao())
        val currencyApiRepository = CurrencyApiRepository(RetrofitInstance.currencyApi)
        val userSettingsRepository = UserSettingsRepository(database.userSettingsDao())
        val recordRepository = RecordRepository(database.recordDao())

        // ViewModel 的初始化 (注意：應該在 `setContent` 之前進行)
        categoryViewModel = ViewModelProvider(
            this,
            CategoryViewModelFactory(categoryRepository)
        )[CategoryViewModel::class.java]
        mainCategoryViewModel = ViewModelProvider(
            this,
            MainCategoryViewModelFactory(mainCategoryRepository)
        )[MainCategoryViewModel::class.java]
        subCategoryViewModel = ViewModelProvider(
            this,
            SubCategoryViewModelFactory(subCategoryRepository)
        )[SubCategoryViewModel::class.java]
        accountViewModel = ViewModelProvider(
            this,
            AccountViewModelFactory(accountRepository)
        )[AccountViewModel::class.java]
        currencyViewModel = ViewModelProvider(
            this,
            CurrencyViewModelFactory(currencyRepository)
        )[CurrencyViewModel::class.java]
        accountCategoryViewModel = ViewModelProvider(
            this,
            AccountCategoryViewModelFactory(accountCategoryRepository)
        )[AccountCategoryViewModel::class.java]
        currencyApiViewModel = ViewModelProvider(
            this,
            CurrencyApiViewModelFactory(currencyApiRepository)
        )[CurrencyApiViewModel::class.java]
        userSettingsViewModel = ViewModelProvider(
            this,
            UserSettingsViewModelFactory(userSettingsRepository)
        )[UserSettingsViewModel::class.java]
        recordViewModel = ViewModelProvider(
            this,
            RecordViewModelFactory(recordRepository)
        )[RecordViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            SkynierTheme(darkTheme = true, dynamicColor = false) {
                val skynierViewModel: SkynierViewModel = viewModel()
                MainScreen(
                    skynierViewModel,
                    categoryViewModel,
                    mainCategoryViewModel,
                    subCategoryViewModel,
                    accountViewModel,
                    currencyViewModel,
                    accountCategoryViewModel,
                    currencyApiViewModel,
                    userSettingsViewModel,
                    recordViewModel,
                    csvImportLauncher,
                )
            }
        }
    }
}
