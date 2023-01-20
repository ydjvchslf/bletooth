package com.example.bledot

import android.app.Application
import android.content.Context

class App: Application() {
    // context를 singleton으로 생성
    companion object {
        lateinit var instance: App
            private set

        lateinit var prefs: PreferenceUtil

        fun context(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = PreferenceUtil(applicationContext)
    }
}