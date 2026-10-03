package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AuditLogEntity
import com.example.data.SandboxDatabase
import com.example.data.SandboxProfileEntity
import com.example.data.VirtualAppEntity
import com.example.virtual.SandboxRunner
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = SandboxDatabase.getDatabase(application).sandboxDao()
    private val sandboxRunner = SandboxRunner(application, dao)

    val profiles: StateFlow<List<SandboxProfileEntity>> = dao.getAllProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val virtualApps: StateFlow<List<VirtualAppEntity>> = dao.getAllVirtualApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLogEntity>> = dao.getAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Seed default sample data on startup so the PoC works out-of-the-box
        viewModelScope.launch {
            try {
                val app = application
                val apkPath = app.packageCodePath ?: app.applicationInfo.sourceDir
                dao.insertVirtualApp(
                    VirtualAppEntity(
                        id = 1L,
                        appName = "Built-in Runtime Plugin",
                        apkPath = apkPath,
                        targetSdk = 34,
                        mainActivityClass = "com.example.virtual.SamplePluginImpl",
                        version = "1.0.0",
                        isLoaded = true
                    )
                )
                dao.insertProfile(
                    SandboxProfileEntity(
                        id = 1L,
                        name = "Default Work Sandbox",
                        packageName = "com.example.sandbox.work",
                        accountName = "user_work@sandbox.internal",
                        status = "Active",
                        storagePath = app.filesDir.absolutePath + "/sandbox_profiles/profile_1"
                    )
                )
            } catch (e: Exception) {
                // Handled gracefully
            }
        }
    }

    fun addProfile(name: String, packageName: String, accountName: String) {
        viewModelScope.launch {
            dao.insertProfile(
                SandboxProfileEntity(
                    name = name,
                    packageName = packageName,
                    accountName = accountName,
                    status = "Active",
                    storagePath = getApplication<Application>().filesDir.absolutePath + "/sandbox_profiles/"
                )
            )
        }
    }

    fun deleteProfile(profile: SandboxProfileEntity) {
        viewModelScope.launch {
            dao.deleteProfile(profile)
        }
    }

    fun addVirtualApp(appName: String, apkPath: String, targetSdk: Int, mainActivityClass: String, version: String) {
        viewModelScope.launch {
            dao.insertVirtualApp(
                VirtualAppEntity(
                    appName = appName,
                    apkPath = apkPath,
                    targetSdk = targetSdk,
                    mainActivityClass = mainActivityClass,
                    version = version,
                    isLoaded = true
                )
            )
        }
    }

    fun deleteVirtualApp(app: VirtualAppEntity) {
        viewModelScope.launch {
            dao.deleteVirtualApp(app)
        }
    }

    fun runSandboxPoC(profileId: Long, profileName: String, appName: String, action: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val result = sandboxRunner.executeSandboxTest(profileId, profileName, appName, action)
            result.fold(
                onSuccess = { onResult(it) },
                onFailure = { onResult("Error: ${it.localizedMessage}") }
            )
        }
    }
}
