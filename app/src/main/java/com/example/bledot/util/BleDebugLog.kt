package com.example.bledot.util

import android.util.Log
import com.example.bledot.BuildConfig
import org.json.JSONArray
import org.json.JSONObject

object BleDebugLog {
    private const val TAG = "BLE_DEBUG"

    @JvmStatic
    fun w(tag: String?, log: String?) {
        if (BuildConfig.DEBUG && log != null && tag != null) {
            Log.w(TAG, "[$tag] $log")
        }
    }

    @JvmStatic
    fun i(tag: String?, log: String?) {
        if (BuildConfig.DEBUG && log != null && tag != null) {
            Log.i(TAG, "[$tag] $log")
        }
    }

    @JvmStatic
    fun d(tag: String?, log: String?) {
        if (BuildConfig.DEBUG && log != null && tag != null) {
            Log.d(TAG, "[$tag] $log")
        }
    }

    @JvmStatic
    fun v(log: String?) {
        if (BuildConfig.DEBUG && log != null) {
            Log.v(TAG, "  -> $log")
        }
    }

    @JvmStatic
    fun l(tag: String?, log: String?) {
        if (BuildConfig.DEBUG && log != null && tag != null) {
            Log.v(TAG, " => $log in [$tag]")
        }
    }

    @JvmStatic
    fun e(log: String?) {
        if (BuildConfig.DEBUG && log != null) {
            Log.e(TAG, log)
        }
    }

    @JvmStatic
    fun send(obj: JSONObject) {
        json("Send", obj)
    }

    @JvmStatic
    fun receive(obj: JSONObject) {
        json("Receive", obj)
    }

    fun httpResponse(message: String) {
        try {
            when {
                message.startsWith("-->") || message.startsWith("<--") ->
                    Log.d(TAG, message)
                message.startsWith("{") || message.startsWith("[") ->
                    json("  data  ", message)
                else ->
                    Log.d("OkHttp", message)
            }
        } catch (e: Exception) {
            d(TAG, "binary data!!")
        }
    }

    private fun json(title: String, data: String) {

        fun parsingToObject(): JSONObject? = try {
            JSONObject(data)
        } catch (e: Exception) {
            null
        }

        fun parsingToArray(): JSONArray? = try {
            JSONArray(data)
        } catch (e: Exception) {
            null
        }

        parsingToObject()?.run {
            json(title, this)
            return
        }

        parsingToArray()?.run {
            json(title, this)
            return
        }
    }

    private fun json(title: String, data: Any) {
        if (!BuildConfig.DEBUG) {
            return
        }

        fun handleObject(obj: JSONObject?, blank: String = "") {
            obj ?: return

            obj.keys().forEach { key ->
                obj[key].let { value->
                    when (value) {
                        is JSONObject -> {
                            Log.v(TAG, "|$blank $key: {")
                            handleObject(value, "$blank  ")
                            Log.v(TAG, "|$blank }")
                        }
                        is JSONArray -> {
                            if (value[0] is JSONObject) {
                                Log.v(TAG, "|$blank $key: [")
                                for (i in 0 until value.length()) {
                                    Log.v(TAG, "|$blank   {")
                                    handleObject(value[i] as JSONObject, "$blank    ")
                                    Log.v(TAG, "|$blank   }")
                                }
                                Log.v(TAG, "|$blank ]")
                            } else {
                                Log.v(TAG, "|$blank $key: $value")
                            }
                        }
                        else -> {
                            Log.v(TAG, "|$blank $key: $value")
                        }
                    }
                }
            }

        }

        fun handleArray(array: JSONArray, blank: String = "") {
            Log.v(TAG, "|$blank[")
            for (i in 0 until array.length()) {
                Log.v(TAG, "|  $blank{")
                handleObject(array[i] as? JSONObject, "$blank   ")
                Log.v(TAG, "|  $blank}")
            }
            Log.v(TAG, "|]")
        }

        Log.v(TAG, " ------- $title -------------------------------------------------")
        when (data) {
            is JSONObject -> {
                Log.v(TAG, "{")
                handleObject(data, "  ")
                Log.v(TAG, "}")
            }

            is JSONArray ->
                handleArray(data)
        }
        Log.v(TAG, " ------------------------------------------------------------------")
    }
}