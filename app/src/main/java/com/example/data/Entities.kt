package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sandbox_profiles")
data class SandboxProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val packageName: String,
    val accountName: String,
    val status: String, // "Active", "Suspended", "Isolated"
    val storagePath: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "virtual_apps")
data class VirtualAppEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val appName: String,
    val apkPath: String,
    val targetSdk: Int,
    val mainActivityClass: String,
    val version: String,
    val isLoaded: Boolean = false,
    val importedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val profileId: Long,
    val action: String,
    val details: String
)
