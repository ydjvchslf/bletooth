package com.example.bledot.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.bledot.R
import com.example.bledot.databinding.FragmentListBinding
import com.example.bledot.mydata.MydataFragment
import com.example.bledot.notupload.NotuploadFragment
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.myWebViewData
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class ListFragment : Fragment() {

    private val logTag = ListFragment::class.simpleName
    private lateinit var binding: FragmentListBinding
    private val listViewModel: ListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        with(binding) {
            viewModel = listViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")

        val activity = activity as AppCompatActivity?

        activity?.setSupportActionBar(binding.toolbar) // toolbar
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false) // actionbar

        val tabs = binding.tabs
        tabs.addTab(tabs.newTab().setText("My Data"))
        tabs.addTab(tabs.newTab().setText("Not Uploaded"))

        if (tabs.selectedTabPosition == 0) {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.container, MydataFragment())?.commit()
        }

        tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            // 탭 버튼이 선택되면 자동으로 실행
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                var selected: Fragment? = null

                if (position == 0) {
                    selected = MydataFragment()
                } else if (position == 1) {
                    selected = NotuploadFragment()
                }

                selected?.let {
                    activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.container, it)?.commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        myWebViewData.observe(viewLifecycleOwner) { webViewData ->
            BleDebugLog.d(logTag, "MWM 생성 이벤트 클릭: $webViewData")
            webViewData?.let {
                //onDestroyView()
                Navigation.findNavController(binding.root).navigate(ListFragmentDirections.actionListFragmentToRealtimeFragment(webViewData))
            }
        }
    }

    override fun onDestroyView() {
        BleDebugLog.i(logTag, "onDestroyView-()")

        val currentFragment = activity?.supportFragmentManager?.findFragmentById(R.id.container)

        currentFragment?.let {
            activity?.supportFragmentManager?.beginTransaction()?.remove(it)?.commit()
        }
        super.onDestroyView()
    }
}