package com.example.bledot

import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.example.bledot.util.BleDebugLog

/** Instantiate the interface and set the context  */
class WebAppInterface(private val mContext: Context) {

    private val logTag = WebAppInterface::class.java.simpleName

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun showToast(message: String) {
        BleDebugLog.i(logTag, "showToast-()")
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun getXYData(x: Double, y: Double) {
        BleDebugLog.i(logTag, "getXYData-()")
        BleDebugLog.d(logTag, "x: [$x], y: [$y]")
    }
}