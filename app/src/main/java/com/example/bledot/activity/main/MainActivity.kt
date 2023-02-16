package com.example.bledot.activity.main

import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bledot.R
import com.example.bledot.databinding.ActivityMainBinding
import com.example.bledot.util.appIsWorking
import com.xsens.dot.android.sdk.XsensDotSdk
import land.sungbin.systemuicontroller.setStatusBarColor

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
        // 네비게이션 메뉴 재선택 UI 오류 수정
        navController.addOnDestinationChangedListener { controller, destination, _ ->
            if (destination.id != bottomNavView.selectedItemId) {
                controller.backQueue.asReversed().drop(1).forEach { entry ->
                    bottomNavView.menu.forEach { item ->
                        if (entry.destination.id == item.itemId) {
                            item.isChecked = true
                            return@addOnDestinationChangedListener
                        }
                    }
                }
            }
        }

        // Bottom menu 아이콘이 테마색으로 변경되는 것을 막기위해서는 Tint 를 초기화
        bottomNavView.itemIconTintList = null
        // 상태바 색상 변경
        this.setStatusBarColor(Color.parseColor("#FFFFFFFF"), true)
        // 앱 작업 중 네비 메뉴 조작 Disable
        appIsWorking.observe(this) { isWorking ->
            binding.bottomNav.menu.forEach { menu -> menu.isEnabled = !isWorking }
        }
        // 센서 재연결 시 SDK 자동 재시작
        XsensDotSdk.setReconnectEnabled(true)
    }
}