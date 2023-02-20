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
const val BASE_URL = "http://httpbin.org"
//const val BASE_URL = "http://192.168.1.250:8080"

var LIMIT_PERCENTAGE = 0.05

var userId = MutableLiveData("")