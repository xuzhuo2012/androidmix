package com.tictalk.tech.app

import android.app.Application
import android.content.res.Configuration
import com.tictalk.tech.app.access.Core

class App : Application() {

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    override fun onCreate() {
        super.onCreate()
        Core.init(this)
    }
}
