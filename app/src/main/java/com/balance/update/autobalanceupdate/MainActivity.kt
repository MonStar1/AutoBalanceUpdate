package com.balance.update.autobalanceupdate

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.balance.update.autobalanceupdate.extension.logd
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.room.LogEntity
import com.balance.update.autobalanceupdate.sms.SmsResolver
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.progressBar
import kotlinx.android.synthetic.main.content_main.mtbank
import kotlinx.android.synthetic.main.content_main.openBattery
import kotlinx.android.synthetic.main.content_main.prior
import kotlinx.android.synthetic.main.content_main.recyclerView
import kotlinx.android.synthetic.main.content_main.textView
import kotlinx.android.synthetic.main.log_item.view.balance
import kotlinx.android.synthetic.main.log_item.view.balanceCategory
import kotlinx.android.synthetic.main.log_item.view.category
import kotlinx.android.synthetic.main.log_item.view.sellerName
import kotlinx.android.synthetic.main.log_item.view.sender
import kotlinx.android.synthetic.main.log_item.view.spent
import kotlinx.android.synthetic.main.log_item.view.time
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.Date

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    lateinit var googleServiceAuth: GoogleServiceAuth
    private val composDisposable = CompositeDisposable()

    private val logAdapter by lazy {
        LogAdapter(
            emptyList(),
            supportFragmentManager,
            application as App,
            progressBar
        )
    }

    companion object {

        const val SMS_PERMISSION = Manifest.permission.RECEIVE_SMS
        const val RC_RECEIVE_SMS = 111
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        recyclerView.adapter = logAdapter

        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
            setDrawable(getDrawable(R.drawable.divider))
        })

        openBattery.setOnClickListener {
            val intentBatteryUsage = Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
            startActivity(intentBatteryUsage)
        }

        googleServiceAuth = GoogleServiceAuth(this, object : GoogleServiceAuthListener {
            override fun signedIn(account: GoogleSignInAccount) {
                textView.text = "Name: ${account.displayName}"
            }
        })

        if (EasyPermissions.hasPermissions(this, SMS_PERMISSION)) {
            onPermissionsGranted(RC_RECEIVE_SMS, mutableListOf(SMS_PERMISSION))
        } else {
            requestSmsPermission()
        }

        val app = (application as App)
        app.datastore.data.onEach {
            GlobalScope.launch(Dispatchers.Main) {
                prior.text = "Приор ${it[app.priorBalance]}"
                mtbank.text = "Мтбанк ${it[app.mtbankBalance]}"
            }
        }.launchIn(GlobalScope)

        App.db.logDao().getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { progressBar.visibility = View.VISIBLE }
            .doOnNext { progressBar.visibility = View.GONE }
            .subscribe {
                logAdapter.update(it)
            }
            .apply { composDisposable.addAll(this) }
    }

    private fun requestSmsPermission() {
        EasyPermissions.requestPermissions(this, "Rationale", RC_RECEIVE_SMS, Manifest.permission.RECEIVE_SMS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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

class LogAdapter(
    var data: List<LogEntity>,
    val fragmentManager: FragmentManager,
    val app: App,
    private val progressBar: ProgressBar
) :
    RecyclerView.Adapter<LogAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.log_item, parent, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = data[position]

        holder.itemView.setOnLongClickListener {
            if (item.isSellerResolved) {
                false
            } else {
                BottomSheet {
                    item.seller = it.name
                    item.isSellerResolved = true

                    Completable.fromAction { SmsResolver(app).resolveSeller(item.spent, it, true) }
                        .andThen { obs -> App.db.logDao().update(item).subscribe(obs) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { progressBar.visibility = View.VISIBLE }
                        .doFinally { progressBar.visibility = View.GONE }
                        .subscribe {}

                }.show(fragmentManager, "")
                true
            }
        }

        holder.itemView.category.text = item.seller
        holder.itemView.sender.text = item.sender
        holder.itemView.spent.text = item.spent.toString()
        holder.itemView.balance.text = item.actualBalance.toString()
        holder.itemView.balanceCategory.text = item.categoryBalance.toString()
        holder.itemView.sellerName.text = item.sellerText
        item.timeInMillis?.let {
            holder.itemView.time.text = DateFormat.getLongDateFormat(holder.itemView.context)
                .format(Date(it)) + " " + DateFormat.getTimeFormat(holder.itemView.context).format(Date(it))
        }

        holder.itemView.setBackgroundResource(if (item.isSellerResolved) R.color.green_alpha else R.color.gray)
    }

    fun update(list: List<LogEntity>) {
        data = list
        notifyDataSetChanged()
//        val oldList = data.toList()
//        DiffUtil.calculateDiff(object : Callback() {
//            override fun getOldListSize(): Int {
//                return oldList.size
//            }
//
//            override fun getNewListSize(): Int {
//                return list.size
//            }
//
//            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//                return oldList[oldItemPosition].id == list[newItemPosition].id
//            }
//
//            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//                return oldList[oldItemPosition] == list[newItemPosition]
//            }
//        }).apply {
//            data = list
//            dispatchUpdatesTo(this@LogAdapter)
//        }
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)
}