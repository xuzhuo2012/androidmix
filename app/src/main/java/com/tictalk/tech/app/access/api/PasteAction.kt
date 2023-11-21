package com.tictalk.tech.app.access.api

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo

fun paste(node: AccessibilityNodeInfo?, text: String) {
    if (node == null) {
        return
    }
    val bundle = Bundle()
    bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
    node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
    node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle)
}
