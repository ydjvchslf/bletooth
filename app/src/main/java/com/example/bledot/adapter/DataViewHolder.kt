package com.example.bledot.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.bledot.R
import com.example.bledot.data.CSVData
import com.example.bledot.databinding.LayoutDataItemBinding
import com.example.bledot.util.BleDebugLog

class DataViewHolder(binding: LayoutDataItemBinding, private val adapter: DataAdapter): RecyclerView.ViewHolder(binding.root) {

    private val logTag = DataViewHolder::class.simpleName ?: ""

    var listItem = binding.listItem
    private var filename = binding.textViewFilename
    var checkbox = binding.checkbox

    init {
        BleDebugLog.d(logTag, "init-()")
    }
    // 데이터와 뷰를 묶는다.
    fun bind(csvData: CSVData) {
        BleDebugLog.i(logTag, "bind-()")
        filename.text = csvData.name
        if (csvData.isChecked) {
            checkbox.setBackgroundResource(R.drawable.ic_checkbox_active)
        } else {
            checkbox.setBackgroundResource(R.drawable.ic_checkbox_inactive)
        }
        //checkbox.isChecked = csvData.isChecked
        BleDebugLog.d(logTag, "csvData: $csvData")

//        checkbox.setOnClickListener {
//            checkbox.isChecked = !csvData.isChecked // 체크박스 클릭이 이상햄,,,,,,
//            BleDebugLog.d(logTag, "csvData: $csvData")
//        }
    }
}