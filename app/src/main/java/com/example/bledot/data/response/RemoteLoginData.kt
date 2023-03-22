package com.example.bledot.data.response

import com.example.bledot.data.UserInfoEntity
import com.google.gson.annotations.SerializedName

data class RemoteDefaultData(
    @SerializedName("resultCode") val resultCode: Int,
    @SerializedName("resultMessage") val resultMessage: String,
    @SerializedName("Authorization") val token: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("birth") val birth: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("weight") val weight: String?,
    @SerializedName("unit") val weightUnit: String?,
    @SerializedName("race") val race: String?,
    @SerializedName("patho") val pathology: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("street") val address1: String?,
    @SerializedName("city") val address2: String?,
    @SerializedName("state") val address3: String?,
    @SerializedName("postcode") val zipCode: String?,
    @SerializedName("country") val country: String?,
)

fun RemoteDefaultData.toEntity() = UserInfoEntity (
    email = email.toString(),
    vender = null,
    pwd = null,
    name = name.toString(),
    birth = birth.toString(),
    gender = gender.toString(),
    weight = weight.toString(),
    weightUnit = weightUnit.toString(),
    race = race.toString(),
    pathology = pathology.toString(),
    phone = phone.toString(),
    address1 = address1.toString(),
    address2 = address2.toString(),
    address3 = address3.toString(),
    zipCode = zipCode.toString(),
    country = country.toString(),
    membership = null
)