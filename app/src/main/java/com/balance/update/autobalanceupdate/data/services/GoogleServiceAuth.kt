package com.balance.update.autobalanceupdate.data.services

import android.app.Activity
import android.content.Intent
import com.balance.update.autobalanceupdate.extension.loge
import com.balance.update.autobalanceupdate.extension.toast
import com.balance.update.autobalanceupdate.presentation.filters.FiltersActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.sheets.v4.SheetsScopes

class GoogleServiceAuth(val activity: Activity, val listener: GoogleServiceAuthListener) {

    companion object {
        val SCOPES = listOf(SheetsScopes.SPREADSHEETS_READONLY)
        const val RQ_GOOGLE_SIGN_IN = 999
    }

    val googleSignInClient: GoogleSignInClient

    init {
        val signInOptions: GoogleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Scope(SheetsScopes.SPREADSHEETS_READONLY), Scope(SheetsScopes.SPREADSHEETS))
                        .requestEmail()
//                        .requestIdToken("622957320654-02leij1hlsb155v5nout6ija8roq406t.apps.googleusercontent.com")
                        .build()

        googleSignInClient = GoogleSignIn.getClient(activity, signInOptions)
    }

    fun request() {
        activity.startActivityForResult(googleSignInClient.signInIntent, RQ_GOOGLE_SIGN_IN)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RQ_GOOGLE_SIGN_IN) {
            val signedInAccountFromIntent = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = signedInAccountFromIntent.getResult(ApiException::class.java)

                toast(activity, "account: ${account!!.displayName}")

                listener.signedIn(account)
            } catch (ex: ApiException) {
                loge(ex)
                toast(activity, "statusCode: ${ex.statusCode}")
            }

            activity.startActivity(Intent(activity, FiltersActivity::class.java))
        }
    }
}

interface GoogleServiceAuthListener {
    fun signedIn(account: GoogleSignInAccount)
}