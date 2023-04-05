package com.example.bledot

import android.content.Context
import android.webkit.JavascriptInterface
import com.example.bledot.data.WebViewData
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.myWebViewData
import com.google.gson.Gson

/** Instantiate the interface and set the context  */
class WebAppInterface(private val mContext: Context) {

    private val logTag = WebAppInterface::class.java.simpleName

    @JavascriptInterface
    fun getXYData(x: Double, y: Double) {
        BleDebugLog.i(logTag, "getXYData-()")
        BleDebugLog.d(logTag, "x: [$x], y: [$y]")
    }

    @JavascriptInterface
    fun receiveFromWebView(data: String) {
        BleDebugLog.i(logTag, "receiveFromWebView-()")
        val myData = Gson().fromJson(data, WebViewData::class.java)
        BleDebugLog.d(logTag, "myData: ${myData.meaId}, ${myData.daId}, ${myData.spId}")
        myWebViewData.postValue(myData)
    }
}