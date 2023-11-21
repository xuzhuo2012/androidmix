package com.tictalk.tech.app.access.api

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK
import com.tictalk.tech.app.access.Core

//模拟点击事件
//判空的放到最底层即可
fun AccessibilityNodeInfo?.click() {
    this?.apply {
        if (isClickable) {
            performAction(ACTION_CLICK)
            return
        } else {
            parent.click()
        }
    }
}

//  既然可以这样，曲线应该也是可以实现的
fun click(x: Int, y: Int): Boolean {
    val _x = if (x < 0) {
        0
    } else x
    val _y = if (y < 0) {
        0
    } else y
    if (Build.VERSION.SDK_INT >= 24) {
        val builder = GestureDescription.Builder()
        val path = Path()
        path.moveTo(_x.toFloat(), _y.toFloat())
        val gestureDescription = builder
            .addStroke(GestureDescription.StrokeDescription(path, 100, 50))
            .build()
        Core.getAcService()?.dispatchGesture(
            gestureDescription,
            object : AccessibilityService.GestureResultCallback() {},
            null
        )
    }
    return false
}
