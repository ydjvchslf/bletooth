package com.example.bledot

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.bledot.Util.BleDebugLog
import com.example.bledot.databinding.ActivityMainBinding
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private val logTag = MainActivity::class.simpleName ?: ""
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val PERMISSION_REQUEST = 200
    }

    // 권한
    @RequiresApi(Build.VERSION_CODES.S)
    private val requiredPermissionArray = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN
    )

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 네비게이션
        val bottomNavView = binding.bottomNav
        val navController = (supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment).findNavController()
        bottomNavView.setupWithNavController(navController)

        // 권한
        checkPermissions(requiredPermissionArray)
    }

    private fun checkPermissions(permissions: Array<String>){
        BleDebugLog.d(logTag, "checkPermission-()")
        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST)
                return
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            PERMISSION_REQUEST -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    BleDebugLog.d(logTag, "권한요청 승인")
                }else{
                    exitProcess(0)
                }
            }
        }
    }
}