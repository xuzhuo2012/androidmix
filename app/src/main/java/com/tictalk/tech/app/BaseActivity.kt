package com.tictalk.tech.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

open class BaseActivity : AppCompatActivity() {
    private val permissionList = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    private var applyPermissionList = mutableListOf<String>()

    companion object {
        const val PERMISSION_REQUEST_CODE = 1;
    }

    override fun onResume() {
        super.onResume()
        applyAllPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && isPermissionGranted(grantResults)) {
                    // every things good
                } else {
                    Toast.makeText(this, R.string.permission_not_granted_toast, Toast.LENGTH_LONG)
                        .show()
                }
                return
            }

            else -> {
                // do nothing
            }
        }
    }

    private fun applyAllPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissionList) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    applyPermissionList.add(permission)
                }
            }
            if (applyPermissionList.isNotEmpty()) {
                requestPermissions(applyPermissionList.toTypedArray(), PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun isPermissionGranted(grantResults: IntArray): Boolean {
        for (ret in grantResults) {
            if (ret != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}