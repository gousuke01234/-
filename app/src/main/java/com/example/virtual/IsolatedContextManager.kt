package com.example.virtual

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import java.io.File

/**
 * Provides an isolated Context, Storage (files/cache/prefs), and Resources wrapper
 * for each virtual app profile. Fulfills Requirements 2 & 3.
 */
class IsolatedContextManager(
    base: Context,
    private val profileId: Long,
    private val profileName: String
) : ContextWrapper(base) {

    private val isolatedRootDir: File by lazy {
        File(baseContext.filesDir, "sandbox_profiles/profile_$profileId").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    private val isolatedFilesDir: File by lazy {
        File(isolatedRootDir, "files").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    private val isolatedCacheDir: File by lazy {
        File(isolatedRootDir, "cache").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    private val isolatedPrefsDir: File by lazy {
        File(isolatedRootDir, "shared_prefs").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    override fun getFilesDir(): File {
        return isolatedFilesDir
    }

    override fun getCacheDir(): File {
        return isolatedCacheDir
    }

    override fun getDir(name: String, mode: Int): File {
        val dir = File(isolatedRootDir, name)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
        // Direct shared preferences to the isolated directory or prefix name
        val isolatedName = "sandbox_${profileId}_$name"
        return baseContext.getSharedPreferences(isolatedName, Context.MODE_PRIVATE)
    }

    override fun getApplicationContext(): Context {
        return this
    }

    val profileSummary: String
        get() = "Sandbox Profile: $profileName (ID: $profileId) [Storage: ${isolatedRootDir.absolutePath}]"
}
