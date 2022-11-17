package com.example.bledot.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bledot.R
import com.example.bledot.data.BleDevice
import com.example.bledot.databinding.LayoutBleItemBinding
import com.example.bledot.util.BleDebugLog


class BleAdapter: RecyclerView.Adapter<BleViewHolder>() {

    private val logTag = BleAdapter::class.simpleName
    private var deviceList = arrayListOf<BleDevice>()

    var listener : ((BleDevice) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleViewHolder {
        // 연결할 레이아웃 설정
        val item = LayoutInflater.from(parent.context).inflate(R.layout.layout_ble_item, parent, false)
        val bleViewHolder = BleViewHolder(LayoutBleItemBinding.bind(item))

        // 리스너 콜백 등록
        item.setOnClickListener {
            val position = bleViewHolder.absoluteAdapterPosition
            BleDebugLog.i(logTag, "position: $position")
            listener?.invoke(deviceList[position])
        }

        return bleViewHolder
    }

    override fun onBindViewHolder(holder: BleViewHolder, position: Int) {
        BleDebugLog.i(logTag, "onBindViewHolder-()")
        holder.bind(this.deviceList[position])
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    // 외부데이터 넘기기 -> adapter에서 갖고 있는 deviceList 와 외부에서 들어온 deviceList 를 연결해주는 함수
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(deviceList: ArrayList<BleDevice>){
        this.deviceList = deviceList // 외부데이터를 adapter 데이터로 할당
        BleDebugLog.d(logTag, "this.deviceList: ${this.deviceList}")
        notifyDataSetChanged()
    }

}