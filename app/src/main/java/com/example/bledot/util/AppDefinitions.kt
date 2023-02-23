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

//const val BASE_URL = "https://fakestoreapi.com"
//const val BASE_URL = "http://httpbin.org"
//const val BASE_URL = "http://192.168.1.250:8080"
//const val BASE_URL = "http://192.168.1.201:8991"
const val BASE_URL = "http://192.168.1.53:8080"

var LIMIT_PERCENTAGE = 0.05

var userId = MutableLiveData("")

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