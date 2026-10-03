package com.example.virtual

import android.content.Context
import android.util.Log

/**
 * Requirement 2: GMS (Google Mobile Services) Lightweight Proxy.
 * Mocks GoogleApiAvailability and GmsCore responses to prevent crashes
 * when virtual apps query Google Play Services.
 */
class GmsProxyManager(private val context: Context, private val profileId: Long) {
    companion object {
        private const val TAG = "GmsProxyManager"
        const val SUCCESS = 0
        const val SERVICE_MISSING = 1
        const val SERVICE_VERSION_UPDATE_REQUIRED = 2
    }

    fun isGooglePlayServicesAvailable(): Int {
        Log.i(TAG, "[Sandbox Profile #$profileId] GMS Proxy: Intercepted isGooglePlayServicesAvailable() request -> Returning SUCCESS (0)")
        return SUCCESS
    }

    fun getClientVersion(): String {
        return "24.00.00 (Virtual GMS Proxy)"
    }

    fun proxyServiceConnection(serviceAction: String): Map<String, Any> {
        Log.i(TAG, "[Sandbox Profile #$profileId] GMS Proxy: Intercepted service connection for $serviceAction")
        return mapOf(
            "status" to "CONNECTED",
            "proxyProvider" to "DroidSandbox GMS Shim",
            "timestamp" to System.currentTimeMillis()
        )
    }
}
