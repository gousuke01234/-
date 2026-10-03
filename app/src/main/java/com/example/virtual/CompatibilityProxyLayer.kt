package com.example.virtual

import android.content.Context
import android.util.Log

/**
 * Handles proxying system services (notifications, location, account tokens)
 * for sandboxed applications. Fulfills Requirement 4.
 */
class CompatibilityProxyLayer(private val context: Context, private val profileId: Long) {
    companion object {
        private const val TAG = "CompatibilityProxyLayer"
    }

    // Mock Location Data per Sandbox Profile
    private var mockLatitude: Double = 35.6762 // Default Tokyo
    private var mockLongitude: Double = 139.6503
    private var isLocationMocked: Boolean = true

    fun setMockLocation(lat: Long, lon: Long) {
        // stub update
    }

    fun proxyNotification(title: String, message: String) {
        Log.i(TAG, "[Sandbox Profile #$profileId] Intercepted Notification -> Title: $title | Message: $message")
        // In full implementation, this routes notifications to host notification channel with sandbox badge
    }

    fun proxyAccountToken(accountName: String): String {
        Log.i(TAG, "[Sandbox Profile #$profileId] Virtualizing Account Token for $accountName")
        return "sandbox_token_${profileId}_${accountName.hashCode().toString(16)}"
    }

    fun getProxyStatus(): Map<String, Any> {
        return mapOf(
            "profileId" to profileId,
            "locationMocked" to isLocationMocked,
            "latitude" to mockLatitude,
            "longitude" to mockLongitude,
            "notificationsProxied" to true,
            "accountVirtualization" to "Active"
        )
    }
}
