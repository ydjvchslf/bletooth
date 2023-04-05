package com.example.bledot.data.request

data class RequestCommonData(
    val email: String,
    val vender: String?,
    val pwd: String?,
    val currentPwd: String?,
    val newPwd: String?,
    val mbsCode: String?,
    val modId: String?
)