package com.balance.update.autobalanceupdate.data.services.sheets

import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.UpdateValuesResponse
import com.google.api.services.sheets.v4.model.ValueRange


class SheetsApi(credential: GoogleAccountCredential) {

    private val sheets = Sheets.Builder(AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential)
            .setApplicationName("Auto Update")
            .build()

    private val service = sheets.spreadsheets().values()

    private lateinit var sheetName: String
    private lateinit var spreadsheetId: String

    fun selectSpreadsheetId(spreadsheetId: String): SheetsApi {
        this.spreadsheetId = spreadsheetId

        return this
    }

    fun selectSheet(sheetName: String): SheetsApi {
        this.sheetName = sheetName

        return this
    }

    fun updateCell(targetCell: String, targetValue: Any): UpdateValuesResponse {
        val values = listOf(listOf(targetValue))

        val body = ValueRange().setValues(values)

        return service.update(spreadsheetId, "$sheetName!$targetCell", body)
                .setValueInputOption("RAW")
                .execute()
    }

    fun readCell(targetCell: String): String {
        val valueRange = service.get(spreadsheetId, "$sheetName!$targetCell").execute()

        val toString = valueRange.getValues().get(0).get(0).toString()

        return toString.replace(",", ".")
    }
}