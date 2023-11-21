package com.tictalk.tech.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.tictalk.tech.app.access.TicTalkAccessibilityService
import com.tictalk.tech.app.utils.AppUtils
import com.tictalk.tech.app.utils.isAccessibilitySettingsOn


class AccServiceActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acc_service)
    }

    override fun onResume() {
        super.onResume()
        if (!isAccessibilitySettingsOn(TicTalkAccessibilityService::class.java)) {
            showPermissionDialog()
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.turn_on_acc_service)
            setMessage(R.string.turn_on_acc_service_msg)
            setNegativeButton(R.string.cancel) { _, _ ->
                finish()
            }
            setPositiveButton(R.string.confirm) { _, _ ->
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }.run {
            create().show()
        }
    }

    fun onOpenAppClicked(view: View) {
        val pkgName = "com.tencent.wechat.app"
        AppUtils.open(pkgName)
    }
}