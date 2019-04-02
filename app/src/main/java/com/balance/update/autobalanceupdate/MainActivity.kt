package com.balance.update.autobalanceupdate

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.balance.update.autobalanceupdate.db.TestData
import com.balance.update.autobalanceupdate.extension.logd
import com.balance.update.autobalanceupdate.extension.toast
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.content_main.*
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.AppSettingsDialog
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    lateinit var googleServiceAuth: GoogleServiceAuth;

    companion object {
        const val SMS_PERMISSION = Manifest.permission.RECEIVE_SMS
        const val RC_RECEIVE_SMS = 111;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        openBattery.setOnClickListener {
            val intentBatteryUsage = Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
            startActivity(intentBatteryUsage)

            thread {
                App.db.getTestDao().insert(TestData(value = "String test"))

                toast(this@MainActivity, App.db.getTestDao().loadAll().joinToString())
            }
        }

        googleServiceAuth = GoogleServiceAuth(this, object : GoogleServiceAuthListener {
            override fun signedIn(account: GoogleSignInAccount) {
                textView.setText("Name: ${account.displayName}")
            }
        })

        if (EasyPermissions.hasPermissions(this, SMS_PERMISSION)) {
            onPermissionsGranted(RC_RECEIVE_SMS, mutableListOf(SMS_PERMISSION))
        } else {
            requestSmsPermission()
        }

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


    private fun requestSmsPermission() {
        EasyPermissions.requestPermissions(this, "Rationale", RC_RECEIVE_SMS, Manifest.permission.RECEIVE_SMS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestSmsPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        toast(this@MainActivity, "Permission granted")

        googleServiceAuth.request()
    }

    override fun onRationaleDenied(requestCode: Int) {
        logd("onRationaleDenied")
        requestSmsPermission()
    }

    override fun onRationaleAccepted(requestCode: Int) {
        logd("onRationaleAccepted")
        requestSmsPermission()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            onPermissionsGranted(RC_RECEIVE_SMS, mutableListOf(SMS_PERMISSION))
        }

        googleServiceAuth.onActivityResult(requestCode, resultCode, data)
    }

}
