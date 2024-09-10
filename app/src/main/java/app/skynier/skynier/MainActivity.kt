package app.skynier.skynier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import app.skynier.skynier.database.AppDatabase
import app.skynier.skynier.repository.CategoryRepository
import app.skynier.skynier.repository.MainCategoryRepository
import app.skynier.skynier.repository.SubCategoryRepository
import app.skynier.skynier.ui.theme.SkynierTheme
import app.skynier.skynier.viewmodels.CategoryViewModel
import app.skynier.skynier.viewmodels.CategoryViewModelFactory
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.MainCategoryViewModelFactory
import app.skynier.skynier.viewmodels.SkynierViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModel
import app.skynier.skynier.viewmodels.SubCategoryViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var mainCategoryViewModel: MainCategoryViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var subCategoryViewModel: SubCategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 数据库和 Repository 的初始化
        val database = AppDatabase.getDatabase(this)
        val categoryRepository = CategoryRepository(database.categoryDao())
        val mainCategoryRepository = MainCategoryRepository(database.mainCategoryDao())
        val subCategoryRepository = SubCategoryRepository(database.subCategoryDao())

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



        enableEdgeToEdge()
        setContent {
            SkynierTheme(darkTheme = true, dynamicColor = false) {
                val skynierViewModel: SkynierViewModel = viewModel()
                MainScreen(skynierViewModel, categoryViewModel, mainCategoryViewModel, subCategoryViewModel)
            }
        }
    }
}
