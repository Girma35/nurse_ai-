package com.nursyai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nursyai.data.local.dao.HealthDao
import com.nursyai.data.local.entity.DailyCheckInEntity
import com.nursyai.data.local.entity.EmergencyContactEntity
import com.nursyai.data.local.entity.MedicationDoseEventEntity
import com.nursyai.data.local.entity.MedicationEntity
import com.nursyai.data.local.entity.ProfileEntity
import com.nursyai.data.local.entity.SymptomEntity

@Database(
    entities = [
        DailyCheckInEntity::class,
        SymptomEntity::class,
        MedicationEntity::class,
        ProfileEntity::class,
        EmergencyContactEntity::class,
        MedicationDoseEventEntity::class
    ],
    version = 2
)
abstract class NursyDatabase : RoomDatabase() {
    abstract fun healthDao(): HealthDao

    companion object {
        @Volatile
        private var INSTANCE: NursyDatabase? = null

        fun getInstance(context: Context): NursyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NursyDatabase::class.java,
                    "nursy_ai_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
