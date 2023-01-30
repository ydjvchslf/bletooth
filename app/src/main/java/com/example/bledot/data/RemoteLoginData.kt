package com.example.bledot.data

import com.google.gson.annotations.SerializedName

data class RemoteDefaultData(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("resMessage") val resMessage: String,
    @SerializedName("data") val data: RemoteUserInfo?,
)

data class RemoteUserInfo(
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("birth") val birth: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("weight") val weight: String,
    @SerializedName("weightUnit") val weightUnit: String,
    @SerializedName("race") val race: String,
    @SerializedName("pathology") val pathology: String,
    @SerializedName("phone") val phone: Int,
    @SerializedName("address1") val address1: String,
    @SerializedName("address2") val address2: String,
    @SerializedName("address3") val address3: String,
    @SerializedName("address4") val address4: String,
    @SerializedName("country") val country: String,
)

fun RemoteUserInfo.toEntity() = UserInfoEntity (
    email = email,
    name = name,
    birth = birth,
    gender = gender,
    weight = weight,
    weightUnit = weightUnit,
    race = race,
    pathology = pathology,
    phone = phone,
    address1 = address1,
    address2 = address2,
    address3 = address3,
    address4 = address4,
    country = country
)