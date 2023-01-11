package com.example.bledot.activity.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bledot.R
import com.example.bledot.databinding.ActivityMainBinding
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.toolbarName
import com.xsens.dot.android.sdk.XsensDotSdk
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private val logTag = MainActivity::class.simpleName ?: ""
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 네비게이션
        val bottomNavView = binding.bottomNav
        val navController = (supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment).findNavController()
        bottomNavView.setupWithNavController(navController)
        // Bottom menu 아이콘이 테마색으로 변경되는 것을 막기위해서는 Tint 를 초기화
        bottomNavView.itemIconTintList = null

        //툴바 이름
        toolbarName.observe(this) { toolbarName ->
            binding.toolbarTextView.text = toolbarName
        }

        // 센서 재연결 시 SDK 자동 재시작
        XsensDotSdk.setReconnectEnabled(true)
    }
}