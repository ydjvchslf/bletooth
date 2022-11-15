package com.example.bledot.adapter

import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bledot.data.BleDevice
import com.example.bledot.databinding.LayoutBleItemBinding
import com.example.bledot.util.BleDebugLog

class BleViewHolder(binding: LayoutBleItemBinding): RecyclerView.ViewHolder(binding.root) {

    private val logTag = BleViewHolder::class.simpleName ?: ""

    private var nameTextView = binding.name
    private var addressTextView = binding.address

    init {
        BleDebugLog.d(logTag, "init-()")
    }

    // 데이터와 뷰를 묶는다.
    fun bind(bleDevice: BleDevice) {
        BleDebugLog.i(logTag, "bind-()")
        BleDebugLog.d(logTag, "bleDevice: $bleDevice")
        nameTextView.text = bleDevice.name
        addressTextView.text = bleDevice.macAddress
    }
}