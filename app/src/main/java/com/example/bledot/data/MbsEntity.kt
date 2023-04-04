package com.example.bledot.data

data class MbsEntity (
    val mbsCode: String?,
    val mbsStatus: String?,
    val startDate: String?,
    val expDate: String?,
) {
    override fun toString(): String {
        return "[MbmEntity] mbsCode: $mbsCode, mbsStatus: $mbsStatus, startDate: $startDate, expDate: $expDate"
    }
}