package com.balance.update.autobalanceupdate

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.permission.AndroidPermissionListener
import com.balance.update.autobalanceupdate.permission.AndroidPermissionManager
import com.balance.update.autobalanceupdate.permission.PermissionApi
import com.balance.update.autobalanceupdate.permission.PermissionApiImpl
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    lateinit var permissionApi: PermissionApi

    lateinit var androidPermissionManager: AndroidPermissionManager
    lateinit var googleServiceAuth: GoogleServiceAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        googleServiceAuth = GoogleServiceAuth(this, object : GoogleServiceAuthListener {
            override fun signedIn() {

            }
        })

        permissionApi = PermissionApiImpl(this, Manifest.permission.RECEIVE_SMS)

        androidPermissionManager = AndroidPermissionManager(permissionApi, object : AndroidPermissionListener {

            override fun showRequestPermissionRationale(repeatRequestRunnable: Runnable) {
                repeatRequestRunnable.run()
            }

            override fun onPermissionGranted() {
                toast(this@MainActivity, "Permission granted")

                googleServiceAuth.request()
            }

            override fun onPermissionDenied() {
                androidPermissionManager.requestPermissions()
            }

            override fun doNotAskAgain() {
                androidPermissionManager.openSettingsApp(this@MainActivity)
            }

        })

        androidPermissionManager.requestPermissions()

//        thread {
//            val cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)
//
//            if (cursor!!.moveToFirst()) { // must check the result to prevent exception
//                do {
//                    var msgData = ""
//                    for (idx in 0 until cursor.columnCount) {
//                        msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx)
//                    }
//                    // use msgData
//                } while (cursor.moveToNext())
//            } else {
//                // empty box, no SMS
//            }
//        }
    }

    override fun onRestart() {
        super.onRestart()

        androidPermissionManager.onRestart()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissionApi.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        googleServiceAuth.onActivityResult(requestCode, resultCode, data)
    }

}
