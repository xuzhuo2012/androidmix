package com.tictalk.tech.app.access

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

private const val TAG = "CapehornAccessibilitySe"

class TicTalkAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        Log.d(TAG, "onAccessibilityEvent: 111")
    }

    //监测是否为目标界面
    override fun onCreate() {
        Core.accessibilityService = this
    }

    override fun onInterrupt() {
    }


    // this is must, if not perform, all the access ability failed
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "onServiceConnected: ")
    }

}
