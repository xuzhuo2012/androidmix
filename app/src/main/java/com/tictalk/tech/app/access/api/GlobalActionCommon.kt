package com.tictalk.tech.app.access.api

import android.accessibilityservice.AccessibilityService
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import com.tictalk.tech.app.utils.AppUtils
import com.tictalk.tech.app.access.Core

fun goHome() {
    Core.getAcService()?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
}

fun goRecent() {
    Core.getAcService()?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
}

fun doubleRecent() {
    Core.getAcService()?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
    AppUtils.normalSleep(200)
    Core.getAcService()?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
}

fun goNotifications() {
    Core.getAcService()?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
}

fun goQuickSettings() {
    Core.getAcService()?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS)
}

fun goPowerDialog() {
    Core.getAcService()?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG)
}

@RequiresApi(VERSION_CODES.N)
fun splitScreen() {
    Core.getAcService()?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
}

@RequiresApi(VERSION_CODES.P)
fun lockScreen() {
    Core.getAcService()?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
}

@RequiresApi(VERSION_CODES.P)
fun takeScreenshot() {
    Core.getAcService()?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
}

