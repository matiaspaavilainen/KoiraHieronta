package com.example.koirahieronta.viewmodels

data class SettingsState(
    val createReport: Boolean = true,
    val createReceipt: Boolean = false,
    val sendReportEmail: Boolean = false,
    val paymentMethods: List<String> = listOf(
        "MobilePay", "Käteinen", "Tilisiirto"
    )
)
