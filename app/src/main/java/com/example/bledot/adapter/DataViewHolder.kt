package com.example.bledot.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.bledot.data.CSVData
import com.example.bledot.databinding.LayoutDataItemBinding
import com.example.bledot.util.BleDebugLog

class DataViewHolder(binding: LayoutDataItemBinding): RecyclerView.ViewHolder(binding.root) {

    private val logTag = DataViewHolder::class.simpleName ?: ""

    private var filename = binding.textViewFilename

    init {
        BleDebugLog.d(logTag, "init-()")
    }
    // 데이터와 뷰를 묶는다.
    fun bind(csvData: CSVData) {
        BleDebugLog.i(logTag, "bind-()")
        filename.text = csvData.name
    }
}