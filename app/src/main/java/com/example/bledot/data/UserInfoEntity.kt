package com.example.bledot.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfoEntity(
    val email: String,
    val name: String,
    val birth: String,
    val gender: String,
    val weight: String,
    val weightUnit: String,
    val race: String,
    val pathology: String,
    val phone: Int,
    val address1: String,
    val address2: String,
    val address3: String,
    val address4: String,
    val country: String,
): Parcelable {
    override fun toString(): String {
        return "email: $email, name: $name, phone: $phone"
    }
}