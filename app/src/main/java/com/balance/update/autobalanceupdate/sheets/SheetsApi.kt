package com.balance.update.autobalanceupdate.sheets

import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.UpdateValuesResponse
import com.google.api.services.sheets.v4.model.ValueRange


class SheetsApi {

    companion object {
        const val HALVA_CELL = "C6"
        const val PRIOR_CELL = "C5"
        const val BALANCE_SPREADSHEET = "15NfMZvT2qDM8Xja1GnqumkNd8sIEgDM2XbMNaWkJocQ"
        const val BALANCE_SHEET = "Sheet_1"
    }

    fun updateSheet(credential: GoogleAccountCredential, halvaValue: Double): UpdateValuesResponse? {
        val sheets = Sheets.Builder(AndroidHttp.newCompatibleTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Auto Update")
                .build()

        val service = sheets.spreadsheets().values()

        val values = listOf(
                listOf(halvaValue)
        )

        val body = ValueRange()
                .setValues(values)

        return service.update(BALANCE_SPREADSHEET, "$BALANCE_SHEET!$HALVA_CELL", body)
                .setValueInputOption("RAW")
                .execute()
    }
}