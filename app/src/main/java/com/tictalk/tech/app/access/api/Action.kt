package com.tictalk.tech.app.access.api

import android.view.accessibility.AccessibilityNodeInfo
import com.tictalk.tech.app.access.Core

fun getNodesByText(text: String): MutableList<AccessibilityNodeInfo> {
    Core.getAcWindow()?.findAccessibilityNodeInfosByText(text)?.let {
        return it
    }
    return ArrayList()
}

fun getNodeById(viewId: String): MutableList<AccessibilityNodeInfo> {
    Core.getAcWindow()?.findAccessibilityNodeInfosByViewId(viewId)?.let {
        return it
    }
    return ArrayList()
}
