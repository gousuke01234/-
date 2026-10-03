package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SandboxDao {
    @Query("SELECT * FROM sandbox_profiles ORDER BY lastActiveAt DESC")
    fun getAllProfiles(): Flow<List<SandboxProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: SandboxProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: SandboxProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: SandboxProfileEntity)

    @Query("SELECT * FROM virtual_apps ORDER BY importedAt DESC")
    fun getAllVirtualApps(): Flow<List<VirtualAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVirtualApp(app: VirtualAppEntity): Long

    @Delete
    suspend fun deleteVirtualApp(app: VirtualAppEntity)

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)
}
