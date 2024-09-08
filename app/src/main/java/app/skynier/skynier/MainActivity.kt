package app.skynier.skynier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import app.skynier.skynier.database.AppDatabase
import app.skynier.skynier.repository.MainCategoryRepository
import app.skynier.skynier.ui.theme.SkynierTheme
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import app.skynier.skynier.viewmodels.MainCategoryViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var mainCategoryViewModel: MainCategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 数据库和 Repository 的初始化
        val database = AppDatabase.getDatabase(this)
        val mainCategoryRepository = MainCategoryRepository(database.mainCategoryDao())

        // ViewModel 的初始化 (注意：應該在 `setContent` 之前進行)
        mainCategoryViewModel = ViewModelProvider(
            this,
            MainCategoryViewModelFactory(mainCategoryRepository)
        )[MainCategoryViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            SkynierTheme(dynamicColor = false) {
                MainScreen(mainCategoryViewModel)
            }
        }
    }
}
