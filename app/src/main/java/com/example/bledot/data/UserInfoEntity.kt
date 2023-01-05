package com.example.bledot.data

data class UserInfoEntity(
    val token: String,
    val name: String,
    val membershipNo: String,
    val age: Int,
) {
    override fun toString(): String {
        return "token: $token, name: $name, membershipNo: $membershipNo, age: $age"
    }
}