package com.example.bledot.util

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.xsens.dot.android.sdk.utils.XsensDotLogger

val btScanningStatus = MutableLiveData(false)
val appIsWorking = MutableLiveData(false)
// A list contains mac address and XsensDotLogger object
val mLoggerList = MutableLiveData<ArrayList<HashMap<String, Any>>>()

const val KEY_ADDRESS = "KEY_ADDRESS"
const val KEY_LOGGER = "KEY_LOGGER"
const val KEY_TAG = "KEY_TAG"
const val KEY_DATA = "KEY_DATA"

const val BASE_URL = "http://www.propriologics.com:8888"

var LIMIT_PERCENTAGE = 0.05

var userId = MutableLiveData("") // TODO:: Preference 쓰면서 이거 안써도 될 듯함

fun getRace(race: String): Int {
    return when (race) {
        "Asian" -> { 0 }
        "Hispanic" -> { 1 }
        "American Indian or Alaska Native" -> { 2 }
        "Black or African American" -> { 3 }
        "White" -> { 4 }
        "unknown" -> { 5 }
        else -> { 6 }
    }
}

fun getPathology(path: String): Int {
    return when (path) {
        "Ataxia" -> { 0 }
        "Multiple sclerosis (MS)" -> { 1 }
        "Multiple system atrophy (MSA)" -> { 2 }
        "Parkinson\'s disease (PD)" -> { 3 }
        "Stroke" -> { 4 }
        "Other" -> { 5 }
        else -> { 6 }
    }
}

fun getCountry(country: String): Int {
    return when (country) {
        "Korea" -> { 0 }
        "America" -> { 1 }
        "Japan" -> { 2 }
        "Chinese" -> { 3 }
        "English" -> { 4 }
        else -> { 5 }
    }
}

fun isGoogleUser(email: String): Boolean {
    val words = email.split("@")
    if (words[1]  == "gmail.com") {
        return true
    }
    return false
}