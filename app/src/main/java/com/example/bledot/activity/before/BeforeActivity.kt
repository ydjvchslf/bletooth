package com.example.bledot.activity.before

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.bledot.R
import com.example.bledot.activity.main.MainActivity
import com.example.bledot.databinding.ActivityBeforeBinding
import com.example.bledot.util.BleDebugLog
import kotlin.system.exitProcess

class BeforeActivity : AppCompatActivity() {

    private val logTag = BeforeActivity::class.simpleName ?: ""
    private lateinit var binding: ActivityBeforeBinding
    private val beforeViewModel = BeforeViewModel()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

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
        installSplashScreen().apply {
            setKeepOnScreenCondition { beforeViewModel.isLoading.value }
        }
        super.onCreate(savedInstanceState)
        BleDebugLog.d(logTag, "onCreate-()")
        binding = ActivityBeforeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = Navigation.findNavController(this, R.id.before_nav_host)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // 권한
        checkPermissions(requiredPermissionArray)
    }

    private fun checkPermissions(permissions: Array<String>){
        BleDebugLog.d(logTag, "checkPermission-()")
        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions,
                    PERMISSION_REQUEST
                )
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
                    Toast.makeText(this, "권한요청 미승인 시 앱 종료됩니다", Toast.LENGTH_SHORT).show()
                    exitProcess(0)
                }
            }
        }
    }
}