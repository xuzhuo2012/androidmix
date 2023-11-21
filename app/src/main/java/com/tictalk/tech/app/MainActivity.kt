package com.tictalk.tech.app

import android.content.Intent
import android.os.Bundle
import android.view.View

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onStartPreviewClicked(view: View) {
        startActivity(Intent(this, PreviewActivity::class.java))
    }

    fun onStartAccessibilityServiceClicked(view: View) {
        startActivity(Intent(this, AccServiceActivity::class.java))
    }
}