package com.balance.update.autobalanceupdate.permission

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.balance.update.autobalanceupdate.BuildConfig

open class AndroidPermissionManager(private val api: PermissionApi, private val permissionListener: AndroidPermissionListener) : RequestPermissionsResultListener {

    init {
        api.subscribe(this)
    }

    private var doNotAskAgain: Boolean = false


    fun onRestart() {
        requestPermissions()
    }

    /**
     * Return the current state of the permissions needed.
     */
    fun checkPermissions(): Boolean {
        return api.checkSelfPermissionGranted()
    }

    fun requestPermissions() {
        if (checkPermissions()) {
            permissionListener.onPermissionGranted()
        } else {
            if (doNotAskAgain) {
                permissionListener.doNotAskAgain()

                return
            }

            val showRationale = api.shouldShowRequestPermissionRationale()

            if (showRationale) {
                permissionListener.showRequestPermissionRationale(repeatRequestPermission())
            } else {
                api.requestPermissions()
            }
        }
    }

    private fun repeatRequestPermission(): Runnable {
        return Runnable {
            api.requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(granted: Boolean) {
        if (granted) {
            permissionListener.onPermissionGranted()
        } else {
            if (api.shouldShowRequestPermissionRationale()) {
                permissionListener.showRequestPermissionRationale(repeatRequestPermission())
            } else {
                doNotAskAgain = true
                permissionListener.doNotAskAgain()
            }
        }
    }

    fun openSettingsApp(context: Context) {
        val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)

        Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = uri
            flags = Intent.FLAG_ACTIVITY_NEW_TASK

            context.startActivity(this)
        }


    }
}

interface AndroidPermissionListener {
    fun onPermissionGranted()

    fun onPermissionDenied()

    fun doNotAskAgain()

    fun showRequestPermissionRationale(repeatRequestRunnable: Runnable)
}