package com.example.bledot.util

import androidx.lifecycle.MutableLiveData

val btScanningStatus = MutableLiveData(false)
val appIsWorking = MutableLiveData(false)
// A list contains mac address and XsensDotLogger object
val mLoggerList = MutableLiveData<ArrayList<HashMap<String, Any>>>()
val isWearingOption = MutableLiveData(false)

const val KEY_ADDRESS = "KEY_ADDRESS"
const val KEY_LOGGER = "KEY_LOGGER"
const val KEY_TAG = "KEY_TAG"
const val KEY_DATA = "KEY_DATA"

//const val BASE_URL = "http://www.propriologics.com:8888"
const val BASE_URL = "http://192.168.1.53:8080"

var LIMIT_PERCENTAGE = 0.05

val REG = "\\d{4}/(0[1-9]|1[012])/(0[1-9]|[12][0-9]|3[01])"

fun getRace(race: String): Int? {
    return when (race) {
        "Hispanic" -> { 0 }
        "American Indian or Alaska Native" -> { 1 }
        "Black or African American" -> { 2 }
        "White" -> { 3 }
        "Asian" -> { 4 }
        "Unknown" -> { 5 }
        else -> { null }
    }
}

fun getPathology(path: String): Int? {
    return when (path) {
        "Multiple sclerosis (MS)" -> { 0 }
        "Multiple system atrophy (MSA)" -> { 1 }
        "Parkinson\'s disease (PD)" -> { 2 }
        "Spinocerebellar Ataxia" -> { 3 }
        "Stroke" -> { 4 }
        "Other" -> { 5 }
        else -> { null }
    }
}

fun isGoogleUser(email: String): Boolean {
    val words = email.split("@")
    if (words[1]  == "gmail.com") {
        return true
    }
    return false
}