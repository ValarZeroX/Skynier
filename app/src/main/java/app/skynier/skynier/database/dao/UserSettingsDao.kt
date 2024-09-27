package app.skynier.skynier.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.skynier.skynier.database.entities.UserSettingsEntity

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings LIMIT 1")
    suspend fun getUserSettings(): UserSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSettings(userSettings: UserSettingsEntity)

    @Update
    suspend fun updateUserSettings(userSettings: UserSettingsEntity)

    @Delete
    suspend fun deleteUserSettings(userSettings: UserSettingsEntity)

    @Query("DELETE FROM user_settings")
    suspend fun deleteAllUserSettings()
}