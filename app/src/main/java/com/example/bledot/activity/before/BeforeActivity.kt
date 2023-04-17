package com.example.bledot.activity.before

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import com.example.bledot.R
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
    private val requiredPermissionArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            // Android 12+ 필요
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
        )
    }

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
        // 권한
        checkPermissions(requiredPermissionArray) // 12 이하일때는 권한 체크 안하도록 했지만 스캔부터 안됨
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

        when (requestCode) {
            PERMISSION_REQUEST -> {
//                grantResults.forEach {
//                    BleDebugLog.d(logTag, "각 권한 허용 여부: $it") // 0 허용, -1 거부
//                }
                if (grantResults.contains(PackageManager.PERMISSION_DENIED)) {
                    showDialogComplete("Warning", "The service is not available because required access has been denied.\n" +
                            "Please change the required permission setting to On and run it again.")
                }

                BleDebugLog.d(logTag, "권한요청 모두 허용")
            }
        }
    }

    private fun showDialogComplete(title: String, subTitle: String) {
        val builder = AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(subTitle)
            setCancelable(false)
            setPositiveButton("Go to Settings") { _, _ ->
                toAppDetailSetting(this.context)
                exitProcess(0)
            }
        }
        builder.create().show()
    }

    private fun toAppDetailSetting(context: Context) {
        try {
            //Open the specific App Info page:
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("package:" + context.packageName)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            //Open the generic Apps page:
            val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}