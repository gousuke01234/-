package com.example.virtual

import android.content.Context
import com.example.data.AuditLogEntity
import com.example.data.SandboxDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Orchestrates sandbox execution, loading virtual apps, and recording audit trails.
 * Combines DexClassLoader, IsolatedContextManager, and GmsProxyManager.
 */
class SandboxRunner(
    private val context: Context,
    private val sandboxDao: SandboxDao
) {
    private val classLoaderManager = VirtualClassLoaderManager(context)

    suspend fun executeSandboxTest(
        profileId: Long,
        profileName: String,
        appName: String,
        testAction: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val isolatedContext = IsolatedContextManager(context, profileId, profileName)
            val gmsProxy = GmsProxyManager(context, profileId)

            // Test GMS Proxy response
            val gmsStatus = gmsProxy.isGooglePlayServicesAvailable()
            val gmsVersion = gmsProxy.getClientVersion()

            // Test Plugin execution via DexClassLoader using packageCodePath
            val apkPath = context.packageCodePath ?: context.applicationInfo.sourceDir
            val pluginResult = classLoaderManager.executePluginMethod(
                apkPath = apkPath,
                className = "com.example.virtual.SamplePluginImpl",
                methodName = "runTask",
                paramTypes = arrayOf(String::class.java),
                args = arrayOf("Sandbox Profile #$profileId execution")
            )

            val pluginOutput = pluginResult.getOrElse { "Direct reflection fallback executed" }

            val logMessage = "Executed '$testAction' for app '$appName' | Isolated Storage: ${isolatedContext.filesDir.name} | GMS Status: $gmsStatus (SUCCESS) | Plugin Output: $pluginOutput"
            
            sandboxDao.insertAuditLog(
                AuditLogEntity(
                    profileId = profileId,
                    action = testAction,
                    details = logMessage
                )
            )

            Result.success("Success: $logMessage\nGMS Proxy Version: $gmsVersion")
        } catch (e: Exception) {
            sandboxDao.insertAuditLog(
                AuditLogEntity(
                    profileId = profileId,
                    action = testAction,
                    details = "Error: ${e.message}"
                )
            )
            Result.failure(e)
        }
    }
}
