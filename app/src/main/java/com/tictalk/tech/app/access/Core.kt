package com.tictalk.tech.app.access

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo

private const val TAG = "AccessCore"

@SuppressLint("StaticFieldLeak")

/**
 * inject- the access instance into this, provide ability to control it.
 */
object Core {

    var accessibilityService: AccessibilityService? = null
        get() {
            if (field == null) {
                return null
            }
            return field
        }

    @SuppressLint("StaticFieldLeak")
    var context: Context? = null
        private set

    fun init(appCtx: Context) {
        context = appCtx
    }

    fun getAcService(): AccessibilityService? {
        return accessibilityService
    }

    fun getAcWindow(): AccessibilityNodeInfo? {
        return accessibilityService?.rootInActiveWindow
    }


    fun checkContext(): Boolean {
        return context != null
    }


    fun checkAccessConnect(): Boolean {
        return getAcService() != null
    }
}
