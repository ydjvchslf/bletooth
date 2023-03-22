package com.example.bledot.data

import android.os.Parcelable
import com.example.bledot.data.request.RequestRegData
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfoEntity(
    val email: String,
    val vender: String?,
    val pwd: String?,
    val name: String,
    val birth: String,
    val gender: String,
    val weight: String,
    val weightUnit: String,
    val race: String,
    val pathology: String,
    val phone: String,
    val address1: String,
    val address2: String,
    val address3: String,
    val zipCode: String,
    val country: String,
    val membership: String?
): Parcelable {

    companion object {
        fun fromUserEntityToReqData(userInfo: UserInfoEntity): RequestRegData {
            return RequestRegData(
                email = userInfo.email,
                vender = userInfo.vender.toString(),
                pwd = userInfo.pwd,
                userNm = userInfo.name,
                birth = userInfo.birth,
                gender = userInfo.gender,
                weight = userInfo.weight,
                unit = userInfo.weightUnit,
                race = userInfo.race,
                patho = userInfo.pathology,
                telNum = userInfo.phone,
                addr = userInfo.address1+", "+userInfo.address2+", "+userInfo.address3,
                zipCd = userInfo.zipCode,
                ctCd = userInfo.country,
            )
        }
    }

    override fun toString(): String {
        return "email: $email, name: $name, birth: $birth, gender: $gender, " +
                "weight: $weight, weightUnit: $weightUnit, race: $race, pathology: $pathology, " +
                "phone: $phone, address1: $address1, address2: $address2, address3: $address3, " +
                "zipCode: $zipCode, country: $country, membership: $membership"
    }
}
