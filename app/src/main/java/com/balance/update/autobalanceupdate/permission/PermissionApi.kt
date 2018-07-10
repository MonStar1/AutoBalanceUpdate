package com.balance.update.autobalanceupdate.permission

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat


class PermissionApiImpl @JvmOverloads constructor(val activity: Activity, private val permissionManifest: String, private val requestPermissionCode: Int = REQUEST_PERMISSIONS_REQUEST_CODE) : PermissionApi {

    companion object {
        /**
         * Code used in requesting runtime permissions.
         */
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

    private val subscribers: MutableSet<RequestPermissionsResultListener> = mutableSetOf()

    override fun subscribe(resultListener: RequestPermissionsResultListener) {
        subscribers.add(resultListener)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestPermissionCode != requestCode) {
            return;
        }

        fun sendToSubscribers(granted: Boolean) {
            subscribers.forEach {
                it.onRequestPermissionsResult(granted)
            }
        }

        when {
            grantResults.isEmpty() -> {
                sendToSubscribers(false)
            }

            grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                sendToSubscribers(true)
            }

            else -> {
                sendToSubscribers(false)
            }
        }
    }


    override fun shouldShowRequestPermissionRationale(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity,
                permissionManifest)
    }

    override fun checkSelfPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(activity,
                permissionManifest) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestPermissions() {
        ActivityCompat.requestPermissions(activity,
                arrayOf(permissionManifest),
                requestPermissionCode)
    }

}

interface PermissionApi : ActivityCompat.OnRequestPermissionsResultCallback {
    fun shouldShowRequestPermissionRationale(): Boolean

    fun checkSelfPermissionGranted(): Boolean

    fun requestPermissions()

    fun subscribe(resultListener: RequestPermissionsResultListener)
}

interface RequestPermissionsResultListener {
    fun onRequestPermissionsResult(granted: Boolean)
}