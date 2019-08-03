package com.balance.update.autobalanceupdate

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.balance.update.autobalanceupdate.data.services.GoogleServiceAuth
import com.balance.update.autobalanceupdate.data.services.GoogleServiceAuthListener
import com.balance.update.autobalanceupdate.extension.logd
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.presentation.filters.FiltersActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.content_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    lateinit var googleServiceAuth: GoogleServiceAuth

    companion object {
        const val SMS_PERMISSION = Manifest.permission.RECEIVE_SMS
        const val RC_RECEIVE_SMS = 111
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        openBattery.setOnClickListener {
            val intentBatteryUsage = Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
            startActivity(intentBatteryUsage)

        }

        googleServiceAuth = GoogleServiceAuth(this, object : GoogleServiceAuthListener {
            override fun signedIn(account: GoogleSignInAccount) {
                textView.text = "Name: ${account.displayName}"
                startActivity(Intent(applicationContext, FiltersActivity::class.java))
                finish()
            }
        })

        if (EasyPermissions.hasPermissions(this, SMS_PERMISSION)) {
            onPermissionsGranted(RC_RECEIVE_SMS, mutableListOf(SMS_PERMISSION))
        } else {
            requestSmsPermission()
        }

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

//        startActivity(Intent(this, FiltersActivity::class.java))
//        finish()
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

        googleServiceAuth.onActivityResult(requestCode, data)
    }

}
