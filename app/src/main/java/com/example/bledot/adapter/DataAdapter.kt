package com.example.bledot.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bledot.R
import com.example.bledot.data.CSVData
import com.example.bledot.databinding.LayoutDataItemBinding
import com.example.bledot.util.BleDebugLog
import java.util.*


class DataAdapter: RecyclerView.Adapter<DataViewHolder>() {

    private val logTag = DataAdapter::class.simpleName
    private var csvDataList = ArrayList<CSVData>()

    var clickListener : ((CSVData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        // 연결할 레이아웃 설정
        val item = LayoutInflater.from(parent.context).inflate(R.layout.layout_data_item, parent, false)
        val dataViewHolder = DataViewHolder(LayoutDataItemBinding.bind(item), this)

        // 리스너 콜백 등록
        item.setOnClickListener {
            dataViewHolder.checkbox.setOnCheckedChangeListener(null)

            val position = dataViewHolder.absoluteAdapterPosition
            BleDebugLog.i(logTag, "클릭된 item name: ${csvDataList[position].name}, isChecked: ${csvDataList[position].isChecked}")
            clickListener?.invoke(csvDataList[position])

            dataViewHolder.checkbox.isChecked = !csvDataList[position].isChecked
        }

        return dataViewHolder
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        BleDebugLog.i(logTag, "onBindViewHolder-()")
        holder.bind(this.csvDataList[position])
    }

    override fun getItemCount(): Int {
        return csvDataList.size
    }

    // 외부데이터 넘기기 -> adapter에서 갖고 있는 deviceList 와 외부에서 들어온 deviceList 를 연결해주는 함수
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(csvDataList: ArrayList<CSVData>){
        this.csvDataList = csvDataList // 외부데이터를 adapter 데이터로 할당
        //BleDebugLog.d(logTag, "this.csvDataList (orig): ${this.csvDataList}")
        sortArray(csvDataList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        csvDataList.clear()
        notifyDataSetChanged()
    }

    private fun sortArray(csvDataList: ArrayList<CSVData>) {
        csvDataList.sortWith { o1, o2 ->
            o1.name.compareTo(o2.name)
        }
        //BleDebugLog.d(logTag, "this.csvDataList (sorting): ${this.csvDataList}")
        csvDataList.reverse()
        //BleDebugLog.d(logTag, "this.csvDataList (reverse): ${this.csvDataList}")
    }
}