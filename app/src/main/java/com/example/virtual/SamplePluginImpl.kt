package com.example.virtual

/**
 * A sample plugin class bundled in the app package that can be dynamically loaded
 * via DexClassLoader(context.packageCodePath, ...) to prove the virtualization PoC.
 */
class SamplePluginImpl {
    fun runTask(input: String): String {
        return "SUCCESS: Virtualized Plugin executed successfully! Input: '$input', Timestamp: ${System.currentTimeMillis()}"
    }

    companion object {
        @JvmStatic
        fun staticRun(input: String): String {
            return "SUCCESS: Static Plugin method invoked with: '$input'"
        }
    }
}
