package com.example.bledot.data.request

data class RequestRegData(
    val email: String,
    val vender: String,
    val pwd: String?, // google 회원가입 경우 null
    val userNm: String,
    val birth: String,
    val gender: String,
    val weight: String,
    val unit: String,
    val race: String,
    val patho: String,
    val telNum: String,
    val addr: String,
    val zipCd: String,
    val ctCd: String,
)