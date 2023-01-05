package com.example.bledot.data

import com.google.gson.annotations.SerializedName

data class RemoteDefaultData(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("resMessage") val resMessage: String,
    @SerializedName("data") val data: RemoteUserInfo?,
)

data class RemoteUserInfo(
    @SerializedName("token") val token: String,
    @SerializedName("name") val name: String,
    @SerializedName("membershipNo") val membershipNo: String,
    @SerializedName("age") val age: Int,
)

fun RemoteUserInfo.toEntity() = UserInfoEntity (
    token = token,
    name = name,
    membershipNo = membershipNo,
    age = age
)