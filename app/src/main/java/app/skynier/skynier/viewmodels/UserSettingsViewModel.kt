package app.skynier.skynier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.skynier.skynier.database.entities.UserSettingsEntity
import app.skynier.skynier.repository.UserSettingsRepository
import kotlinx.coroutines.launch

class UserSettingsViewModel(private val repository: UserSettingsRepository) : ViewModel() {

    private val _userSettings = MutableLiveData<UserSettingsEntity?>()
    val userSettings: LiveData<UserSettingsEntity?> get() = _userSettings

    fun loadUserSettings() {
        viewModelScope.launch {
            val settings = repository.getUserSettings()
            _userSettings.value = settings
        }
    }

    fun saveUserSettings(userSettings: UserSettingsEntity) {
        viewModelScope.launch {
            repository.insertUserSettings(userSettings)
            _userSettings.value = userSettings
        }
    }

    fun updateUserSettings(userSettings: UserSettingsEntity) {
        viewModelScope.launch {
            repository.updateUserSettings(userSettings)
            _userSettings.value = userSettings
        }
    }

    fun deleteUserSettings(userSettings: UserSettingsEntity) {
        viewModelScope.launch {
            repository.deleteUserSettings(userSettings)
            _userSettings.value = null
        }
    }

    fun deleteAllUserSettings() {
        viewModelScope.launch {
            repository.deleteAllUserSettings()
            _userSettings.value = null
        }
    }
}

class UserSettingsViewModelFactory(private val repository: UserSettingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserSettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}