package app.skynier.skynier.repository

import app.skynier.skynier.database.dao.UserSettingsDao
import app.skynier.skynier.database.entities.UserSettingsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserSettingsRepository(private val userSettingsDao: UserSettingsDao) {

    suspend fun getUserSettings(): UserSettingsEntity? {
        return withContext(Dispatchers.IO) {
            userSettingsDao.getUserSettings()
        }
    }

    suspend fun insertUserSettings(userSettings: UserSettingsEntity) {
        withContext(Dispatchers.IO) {
            userSettingsDao.insertUserSettings(userSettings)
        }
    }

    suspend fun updateUserSettings(userSettings: UserSettingsEntity) {
        withContext(Dispatchers.IO) {
            userSettingsDao.updateUserSettings(userSettings)
        }
    }

    suspend fun deleteUserSettings(userSettings: UserSettingsEntity) {
        withContext(Dispatchers.IO) {
            userSettingsDao.deleteUserSettings(userSettings)
        }
    }

    suspend fun deleteAllUserSettings() {
        withContext(Dispatchers.IO) {
            userSettingsDao.deleteAllUserSettings()
        }
    }
}