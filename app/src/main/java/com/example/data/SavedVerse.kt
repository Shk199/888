package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_verses")
data class SavedVerse(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val verse: String,
    val meterName: String,
    val date: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface SavedVerseDao {
    @Query("SELECT * FROM saved_verses ORDER BY timestamp DESC")
    fun getAllVerses(): Flow<List<SavedVerse>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerse(verse: SavedVerse): Long

    @Query("DELETE FROM saved_verses WHERE id = :id")
    suspend fun deleteVerse(id: Long)

    @Query("DELETE FROM saved_verses")
    suspend fun clearAll()
}

@Database(entities = [SavedVerse::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedVerseDao(): SavedVerseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mizan_database"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
