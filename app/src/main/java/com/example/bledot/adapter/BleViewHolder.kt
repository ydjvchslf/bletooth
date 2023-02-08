package com.example.bledot.adapter

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat.getColor
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.Resource
import com.example.bledot.R
import com.example.bledot.data.BleDevice
import com.example.bledot.databinding.LayoutBleItemBinding
import com.example.bledot.util.BleDebugLog

class BleViewHolder(binding: LayoutBleItemBinding): RecyclerView.ViewHolder(binding.root) {

    private val logTag = BleViewHolder::class.simpleName ?: ""

    private var nameTextView = binding.name
    private var addressTextView = binding.address
    private var battery = binding.battery
    private var connectCircle = binding.icConnectCircle

    init {
        BleDebugLog.d(logTag, "init-()")
    }

    // 데이터와 뷰를 묶는다.
    @SuppressLint("SetTextI18n")
    fun bind(bleDevice: BleDevice) {
        BleDebugLog.i(logTag, "bind-()")
        BleDebugLog.d(logTag, "bleDevice: $bleDevice")
        nameTextView.text = bleDevice.name
        addressTextView.text = bleDevice.macAddress

        if(bleDevice.batteryPercent == -1) {
            //battery.text = "연결하기"
            //battery.setTextColor(Color.parseColor("#28A7E1"))
            battery.visibility = View.INVISIBLE
            connectCircle.visibility = View.INVISIBLE
            return
        }

        battery.text = bleDevice.batteryPercent.toString()+"%"
        battery.visibility = View.VISIBLE
        connectCircle.visibility = View.VISIBLE
    }
}