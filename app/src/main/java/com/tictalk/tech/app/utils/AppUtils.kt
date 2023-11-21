package com.tictalk.tech.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.tictalk.tech.app.access.Core

class AppUtils {
    companion object {

        fun openApp(pacName: String) {
            startApp(pacName, Core.context)
        }

        fun open(schema: String) {
            try {
                val intent = Intent();
                intent.data = Uri.parse(schema);
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                Core.context?.let { ContextCompat.startActivity(it, intent, null) };
            } catch (e: Exception) {

            }
        }

        private fun startApp(pkg: String?, ctx: Context?) {
            if (ctx == null) {
                return
            }
            val manager = ctx.packageManager
            val launchIntent = manager.getLaunchIntentForPackage(pkg!!) ?: return
            ctx.startActivity(launchIntent)
        }


        fun normalSleep(ms: Long) {
            try {
                Thread.sleep(ms)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}
