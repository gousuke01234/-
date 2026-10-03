package com.example.virtual

import android.app.Activity
import android.os.Bundle

/**
 * Interface definition for plugin activities loaded dynamically via StubActivity.
 */
interface IPluginActivity {
    fun attach(activity: Activity)
    fun onCreate(savedInstanceState: Bundle?)
    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()
    fun onDestroy()
}
