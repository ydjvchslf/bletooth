package com.example.bledot.mydata

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bledot.App
import com.example.bledot.R
import com.example.bledot.databinding.FragmentDataBinding
import com.example.bledot.util.BASE_URL
import com.example.bledot.util.BleDebugLog


class MydataFragment: Fragment() {

    private val logTag = MydataFragment::class.simpleName
    private lateinit var binding: FragmentDataBinding
    private val mydataViewModel: MydataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_data, container, false)
        with(binding) {
            viewModel = mydataViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")

        val headers = HashMap<String, String>()
        headers["Authorization"] = App.prefs.getString("token", "no token")

        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = true
            settings.supportZoom()
            settings.displayZoomControls = false
            loadUrl("$BASE_URL/test/activityListWebView", headers)
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }
}