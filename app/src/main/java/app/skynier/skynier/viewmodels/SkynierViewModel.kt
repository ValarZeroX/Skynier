package app.skynier.skynier.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.skynier.skynier.database.entities.AccountEntity
import app.skynier.skynier.database.entities.MainCategoryEntity
import app.skynier.skynier.database.entities.SubCategoryEntity
import app.skynier.skynier.library.CategoryIcon

class SkynierViewModel: ViewModel() {
    // 用於儲存選擇的圖標
    private val _selectedIcon = MutableLiveData<CategoryIcon?>()
    val selectedIcon: LiveData<CategoryIcon?> get() = _selectedIcon

    // 設定選中的圖標
    fun setSelectedIcon(icon: CategoryIcon) {
        _selectedIcon.value = icon
    }

    fun updateSelectedIcon(newIcon: CategoryIcon) {
        _selectedIcon.value = newIcon
    }

    var selectedMainCategoryToEdit: MainCategoryEntity? by mutableStateOf(null)

    fun updateSelectedCategory(category: MainCategoryEntity) {
        selectedMainCategoryToEdit = category
    }

    var selectedSubCategoryToEdit: SubCategoryEntity? by mutableStateOf(null)

    fun updateSelectedCategory(category: SubCategoryEntity) {
        selectedSubCategoryToEdit = category
    }

    // 用於儲存選擇的帳戶
    var selectedAccountToEdit: AccountEntity? by mutableStateOf(null)

    // 設定選中的帳戶
    fun updateSelectedAccount(account: AccountEntity) {
        selectedAccountToEdit = account
    }
}

