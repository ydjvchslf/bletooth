package com.example.bledot.util

import androidx.lifecycle.MutableLiveData
import com.xsens.dot.android.sdk.utils.XsensDotLogger

val btScanningStatus = MutableLiveData(false)
// A list contains mac address and XsensDotLogger object
val mLoggerList = MutableLiveData<ArrayList<HashMap<String, Any>>>()

const val KEY_ADDRESS = "KEY_ADDRESS"
const val KEY_LOGGER = "KEY_LOGGER"
const val KEY_TAG = "KEY_TAG"
const val KEY_DATA = "KEY_DATA"