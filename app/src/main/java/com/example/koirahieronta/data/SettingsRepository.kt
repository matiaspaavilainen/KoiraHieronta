package com.example.koirahieronta.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class Settings(
    val userName: String = "",
    val userEmail: String = "",
    val userPhoneNum: String = "",
    val userYCode: String = "",
    val saveDirectory: String = "",
    val unitPrice: Double = 0.0,
    val additionalInfo: String = "",
    val instructions: String = "",
    val emailSubject: String = "",
    val emailBody: String = "",
    val createReport: Boolean = true,
    val createReceipt: Boolean = false,
    val sendReport: Boolean = false,
    val sendReceipt: Boolean = false
)

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val userName = stringPreferencesKey("userName")
    private val userEmail = stringPreferencesKey("userEmail")
    private val userPhoneNum = stringPreferencesKey("userPhoneNum")
    private val userYCode = stringPreferencesKey("userYCode")
    private val saveDirectory = stringPreferencesKey("saveDirectory")
    private val unitPrice = doublePreferencesKey("unitPrice")
    private val additionalInfo = stringPreferencesKey("additionalInfo")
    private val instructions = stringPreferencesKey("instructions")
    private val emailSubject = stringPreferencesKey("emailSubject")
    private val emailBody = stringPreferencesKey("emailBody")
    private val createReportPDF = booleanPreferencesKey("createReportPDF")
    private val createReceiptPDF = booleanPreferencesKey("createReceiptPDF")
    private val sendReport = booleanPreferencesKey("sendReport")

    val settings: Flow<Settings> = dataStore.data.map { preferences ->
        Settings(
            userName = preferences[userName] ?: "",
            userEmail = preferences[userEmail] ?: "",
            userPhoneNum = preferences[userPhoneNum] ?: "",
            userYCode = preferences[userYCode] ?: "",
            saveDirectory = preferences[saveDirectory] ?: "",

            unitPrice = preferences[unitPrice] ?: 0.0,
            additionalInfo = preferences[additionalInfo] ?: "",
            instructions = preferences[instructions] ?: "",
            emailSubject = preferences[emailSubject] ?: "",
            emailBody = preferences[emailBody] ?: "",

            createReport = preferences[createReportPDF] ?: true,
            createReceipt = preferences[createReceiptPDF] ?: false,
            sendReport = preferences[sendReport] ?: false,
        )
    }

    suspend fun updateSettings(
        userName: String,
        userEmail: String,
        userPhoneNum: String,
        userYCode: String,
        saveDirectory: String,

        unitPrice: Double,
        additionalInfo: String,
        instructions: String,
        emailSubject: String,
        emailBody: String,

        createReport: Boolean,
        createReceipt: Boolean,
        sendReport: Boolean,
    ) {
        dataStore.edit { preferences ->
            preferences[this.userName] = userName
            preferences[this.userEmail] = userEmail
            preferences[this.userPhoneNum] = userPhoneNum
            preferences[this.userYCode] = userYCode
            preferences[this.saveDirectory] = saveDirectory

            preferences[this.unitPrice] = unitPrice
            preferences[this.additionalInfo] = additionalInfo
            preferences[this.instructions] = instructions
            preferences[this.emailSubject] = emailSubject
            preferences[this.emailBody] = emailBody

            preferences[this.createReportPDF] = createReport
            preferences[this.createReceiptPDF] = createReceipt
            preferences[this.sendReport] = sendReport
        }
    }
}