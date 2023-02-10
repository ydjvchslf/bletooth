package com.example.bledot.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.bledot.App
import com.example.bledot.R
import com.example.bledot.databinding.FragmentHomeBinding
import com.example.bledot.util.BleDebugLog
import java.io.File


class HomeFragment : Fragment() {

    private val logTag = HomeFragment::class.simpleName
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        with(binding) {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")
        // 버튼 > 네비게이션
        binding.listBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(HomeFragmentDirections.actionHomeFragmentToListFragment())
        }
        binding.deviceBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(HomeFragmentDirections.actionHomeFragmentToBleFragment())
        }
        binding.recordBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(HomeFragmentDirections.actionHomeFragmentToRealtimeFragment())
        }
        binding.settingBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(HomeFragmentDirections.actionHomeFragmentToConfigFragment())
        }

        // 미전송 데이터 확인
        isExistData()
    }

    private fun isExistData() {
        BleDebugLog.i(logTag, "isExistData-()")
        val dir: File? = App.context().getExternalFilesDir(null)
        val path = dir?.absolutePath + File.separator

        BleDebugLog.d(logTag, "path: $path")

        val directory = File(path)
        val files = directory.listFiles()

        val filesNameList = ArrayList<String>()

        files?.forEach { file ->
            filesNameList.add(file.name)
        }

        val dataNum = filesNameList.size
        BleDebugLog.d(logTag, "dataNum: $dataNum")

        if (0 < dataNum) {
            // 파일이 1개 이상 존재 시, 다이얼로그 띄워줌
            showDialog("Data not uploaded",
                "$dataNum data found\n" +
                        "Do you want to re-upload?\n" +
                        "If you cancel, all data will be cleared.")
        }
    }

    private fun showDialog(title: String, subTitle: String) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(subTitle)
            setPositiveButton("Upload") { _, _ ->
                // TODO:: 데이터 하나씩 업로드
            }
            setNegativeButton("Cancel") { _, _ ->
                // TODO:: 데이터 모두 삭제
            }
        }
        builder.create().show()
    }
}