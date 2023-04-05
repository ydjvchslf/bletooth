package com.example.bledot.notupload

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.bledot.R
import com.example.bledot.adapter.DataAdapter
import com.example.bledot.databinding.FragmentNotUploadedBinding
import com.example.bledot.util.BleDebugLog

class NotuploadFragment: Fragment() {

    private val logTag = NotuploadFragment::class.simpleName
    private lateinit var binding: FragmentNotUploadedBinding
    private val notuploadViewModel: NotuploadViewModel by viewModels()
    // 사용할 리사이클러뷰 생성
    var dataAdapter = DataAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_not_uploaded, container, false)
        with(binding) {
            viewModel = notuploadViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")

        notuploadViewModel.isExistData()

        binding.recyclerView.apply {
            adapter = dataAdapter
            dataAdapter.submitList(notuploadViewModel.localFileList)
        }

        dataAdapter.clickListener = { csvData, status ->
            binding.allCheckBox.isChecked = status
        }

        // select all 버튼
        binding.allCheckBox.setOnClickListener {
            val isChecked = binding.allCheckBox.isChecked
            BleDebugLog.d(logTag, "isChecked: $isChecked")

            val status = checkItemStatus()
            when (status) {
                0 -> { // 리스트 아이템이 모두 선택 상태
                    changeItemStatus(false)
                }
                1 -> { // 리스트 아이템이 모두 해제 상태
                    changeItemStatus(true)
                }
                2 -> { // 일부만 선택 상태
                    changeItemStatus(true)
                }
            }
            //dataAdapter.checkboxListener = isChecked
        }
        // select all 문구
        binding.textViewSelectAll.setOnClickListener {

        }
        // delete 버튼
        binding.deleteBtn.setOnClickListener {
            val dataList = dataAdapter.csvDataList
            notuploadViewModel.checkSelectedData(dataList, false) { }
        }
        // upload 버튼
        binding.uploadBtn.setOnClickListener {
            val dataList = dataAdapter.csvDataList
            notuploadViewModel.checkSelectedData(dataList, true) { result ->
                if (result) {
                    showCompleteDialog("Complete", "The data uploaded to the server.")
                } else {
                    showCompleteDialog("Warning", "The data failed to upload to the server.")
                }
            }
        }
        // delete or upload 후 리사이클러뷰 갱신
        notuploadViewModel.isUpdate.observe(viewLifecycleOwner) { isUpdated ->
            if (isUpdated) {
                dataAdapter.submitList(notuploadViewModel.localFileList)
                notuploadViewModel.isUpdate.value = false
            }
        }
        // delete or upload 후 선택칸 갱신
        notuploadViewModel.localFileListSize.observe(viewLifecycleOwner) { listSize ->
            if (listSize == 0) {
                binding.layoutUpper.visibility = View.GONE
                binding.layoutNoData.visibility = View.VISIBLE
            } else if (listSize != 0 && listSize != null) {
                binding.layoutUpper.visibility = View.VISIBLE
                binding.layoutNoData.visibility = View.GONE
            }
        }
    }

    private fun checkItemStatus(): Int {
        BleDebugLog.i(logTag, "checkItemStatus-()")
        val foundCheckedItem = dataAdapter.csvDataList.all {
            it.isChecked
        }
        return if (foundCheckedItem) 0 else 1
    }

    private fun changeItemStatus(status: Boolean) {
        BleDebugLog.i(logTag, "changeItemStatus-()")
        dataAdapter.csvDataList.replaceAll {
            it.isChecked = status
            it
        }

        dataAdapter.submitList(dataAdapter.csvDataList)
    }

    private fun showCompleteDialog(title: String, subTitle: String) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(subTitle)
            setCancelable(false)
            setPositiveButton("Action") { _, _ -> }
        }
        builder.create().show()
    }

    override fun onDestroyView() {
        BleDebugLog.i(logTag, "onDestroyView-()")
        super.onDestroyView()
        viewModelStore.clear()
    }
}