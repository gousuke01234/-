package com.example.virtual

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.graphics.Color

/**
 * Requirement 1 & 2: Stub (身代わり) Activity implementation.
 * Acts as the host container and proxy for loading and executing external
 * APK/DEX activities without requiring pre-registration in AndroidManifest.xml.
 * Delegates lifecycle callbacks (onCreate, onStart, onResume, etc.) to the target plugin activity.
 */
class StubActivity : Activity() {
    companion object {
        private const val TAG = "StubActivity"
        const val EXTRA_APK_PATH = "extra_apk_path"
        const val EXTRA_CLASS_NAME = "extra_class_name"
    }

    private var pluginActivityInstance: Any? = null
    private var pluginInterface: IPluginActivity? = null
    private val classLoaderManager by lazy { VirtualClassLoaderManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apkPath = intent.getStringExtra(EXTRA_APK_PATH) ?: (packageCodePath ?: applicationInfo.sourceDir)
        val className = intent.getStringExtra(EXTRA_CLASS_NAME) ?: "com.example.virtual.SamplePluginActivity"

        Log.i(TAG, "StubActivity starting dynamic plugin activity: $className from $apkPath")

        try {
            val classLoader = classLoaderManager.getClassLoader(apkPath)
            if (classLoader != null) {
                val clazz = classLoader.loadClass(className)
                val instance = clazz.getDeclaredConstructor().newInstance()
                pluginActivityInstance = instance

                if (instance is IPluginActivity) {
                    pluginInterface = instance
                    instance.attach(this)
                    instance.onCreate(savedInstanceState)
                } else {
                    // Reflection fallback for standard classes
                    invokeLifecycleMethod(clazz, instance, "onCreate", arrayOf(Bundle::class.java), arrayOf(savedInstanceState))
                }
            } else {
                showErrorView("ClassLoader initialization failed for path: $apkPath")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load plugin activity: $className", e)
            showErrorView("Failed to load plugin: ${e.message}")
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            pluginInterface?.onStart() ?: invokeLifecycleMethod(pluginActivityInstance?.javaClass, pluginActivityInstance, "onStart", arrayOf(), arrayOf())
        } catch (e: Exception) {
            Log.e(TAG, "onStart delegation error", e)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            pluginInterface?.onResume() ?: invokeLifecycleMethod(pluginActivityInstance?.javaClass, pluginActivityInstance, "onResume", arrayOf(), arrayOf())
        } catch (e: Exception) {
            Log.e(TAG, "onResume delegation error", e)
        }
    }

    override fun onPause() {
        try {
            pluginInterface?.onPause() ?: invokeLifecycleMethod(pluginActivityInstance?.javaClass, pluginActivityInstance, "onPause", arrayOf(), arrayOf())
        } catch (e: Exception) {
            Log.e(TAG, "onPause delegation error", e)
        }
        super.onPause()
    }

    override fun onStop() {
        try {
            pluginInterface?.onStop() ?: invokeLifecycleMethod(pluginActivityInstance?.javaClass, pluginActivityInstance, "onStop", arrayOf(), arrayOf())
        } catch (e: Exception) {
            Log.e(TAG, "onStop delegation error", e)
        }
        super.onStop()
    }

    override fun onDestroy() {
        try {
            pluginInterface?.onDestroy() ?: invokeLifecycleMethod(pluginActivityInstance?.javaClass, pluginActivityInstance, "onDestroy", arrayOf(), arrayOf())
        } catch (e: Exception) {
            Log.e(TAG, "onDestroy delegation error", e)
        }
        super.onDestroy()
    }

    private fun invokeLifecycleMethod(clazz: Class<*>?, instance: Any?, methodName: String, paramTypes: Array<Class<*>>, args: Array<Any?>) {
        if (clazz == null || instance == null) return
        try {
            val method = clazz.getMethod(methodName, *paramTypes)
            method.isAccessible = true
            method.invoke(instance, *args)
        } catch (e: NoSuchMethodException) {
            // Optional lifecycle method, ignore
        } catch (e: Exception) {
            Log.e(TAG, "Error invoking lifecycle method $methodName", e)
        }
    }

    private fun showErrorView(msg: String) {
        val tv = TextView(this).apply {
            text = "StubActivity Error:\n$msg"
            setTextColor(Color.RED)
            textSize = 16f
            setPadding(48, 48, 48, 48)
        }
        setContentView(tv)
    }
}
