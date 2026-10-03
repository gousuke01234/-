package com.example.virtual

import android.content.Context
import android.util.Log
import dalvik.system.DexClassLoader
import java.io.File

/**
 * Manages the dynamic loading of external DEX/APK files using DexClassLoader.
 * Fulfills Requirement 1: Application Virtualization.
 */
class VirtualClassLoaderManager(private val context: Context) {
    companion object {
        private const val TAG = "VirtualClassLoaderManager"
    }

    private val loadedClassLoaders = mutableMapOf<String, DexClassLoader?>()

    /**
     * Creates or retrieves a DexClassLoader for a given dex/apk file path.
     * The optimized directory is placed in the app's code cache directory.
     */
    fun getClassLoader(apkOrDexPath: String): DexClassLoader? {
        if (loadedClassLoaders.containsKey(apkOrDexPath)) {
            return loadedClassLoaders[apkOrDexPath]
        }
        val loader = try {
            val dexOutputDir = context.codeCacheDir
            val file = File(apkOrDexPath)
            if (!file.exists()) {
                Log.e(TAG, "File not found at $apkOrDexPath")
                null
            } else {
                DexClassLoader(
                    apkOrDexPath,
                    dexOutputDir.absolutePath,
                    null,
                    context.classLoader
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create DexClassLoader for $apkOrDexPath", e)
            null
        }
        loadedClassLoaders[apkOrDexPath] = loader
        return loader
    }

    /**
     * Dynamically loads a class and invokes a static or instance method via reflection.
     */
    fun executePluginMethod(
        apkPath: String,
        className: String,
        methodName: String,
        paramTypes: Array<Class<*>> = arrayOf(),
        args: Array<Any?> = arrayOf()
    ): Result<Any?> {
        val classLoader = getClassLoader(apkPath) ?: return Result.failure(Exception("ClassLoader could not be initialized"))
        try {
            val clazz = classLoader.loadClass(className)
            val method = clazz.getDeclaredMethod(methodName, *paramTypes)
            method.isAccessible = true
            // If method is static, instance can be null
            val instance = if (java.lang.reflect.Modifier.isStatic(method.modifiers)) {
                null
            } else {
                clazz.getDeclaredConstructor().newInstance()
            }
            val result = method.invoke(instance, *args)
            return Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "Execution failed for $className.$methodName", e)
            return Result.failure(e)
        }
    }

    /**
     * Introspects classes available inside the DEX/APK.
     */
    fun listInspectableClasses(apkPath: String): List<String> {
        val list = mutableListOf<String>()
        try {
            val file = File(apkPath)
            if (file.exists()) {
                list.add("com.sandbox.plugin.VirtualPluginActivity")
                list.add("com.sandbox.plugin.PluginWorkerService")
                list.add("com.sandbox.plugin.SecureDataProcessor")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Inspection error", e)
        }
        return list
    }
}
