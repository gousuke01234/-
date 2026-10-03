package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SandboxProfileEntity::class, VirtualAppEntity::class, AuditLogEntity::class], version = 1, exportSchema = false)
abstract class SandboxDatabase : RoomDatabase() {
    abstract fun sandboxDao(): SandboxDao

    companion object {
        @Volatile
        private var INSTANCE: SandboxDatabase? = null

        fun getDatabase(context: Context): SandboxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SandboxDatabase::class.java,
                    "droid_sandbox_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
