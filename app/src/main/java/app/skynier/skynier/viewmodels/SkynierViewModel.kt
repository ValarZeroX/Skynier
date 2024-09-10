package app.skynier.skynier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.skynier.skynier.library.CategoryIcon

class SkynierViewModel: ViewModel() {
    // 用於儲存選擇的圖標
    private val _selectedIcon = MutableLiveData<CategoryIcon?>()
    val selectedIcon: LiveData<CategoryIcon?> get() = _selectedIcon

    // 設定選中的圖標
    fun setSelectedIcon(icon: CategoryIcon) {
        _selectedIcon.value = icon
    }
}

