package com.balance.update.autobalanceupdate

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.balance.update.autobalanceupdate.extension.logd
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.room.LogEntity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.log_item.view.*
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.AppSettingsDialog


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    lateinit var googleServiceAuth: GoogleServiceAuth
    private val composDisposable = CompositeDisposable()

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

        App.db.logDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    recyclerView.adapter = LogAdapter(it)
                }
                .apply { composDisposable.addAll(this) }
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

    override fun onDestroy() {
        super.onDestroy()

        composDisposable.clear()
    }

}

private class LogAdapter(var data: List<LogEntity>) : RecyclerView.Adapter<LogAdapter.VH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(View.inflate(parent.context, R.layout.log_item, parent))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = data[position]

        holder.itemView.seller.text = item.seller
        holder.itemView.sender.text = item.sender
        holder.itemView.spent.text = item.spent.toString()
        holder.itemView.balance.text = item.actualBalance.toString()
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {

    }
}
