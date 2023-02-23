package com.example.bledot.withdrawal

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.bledot.R
import com.example.bledot.ble.BleState
import com.example.bledot.databinding.FragmentHomeBinding
import com.example.bledot.databinding.FragmentWithdrawalBinding
import com.example.bledot.editinfo.EditInfoFragmentDirections
import com.example.bledot.util.BleDebugLog
import com.example.bledot.util.btScanningStatus
import com.example.bledot.util.userId


class WithdrawalFragment : Fragment() {

    private val logTag = WithdrawalFragment::class.simpleName
    private lateinit var binding: FragmentWithdrawalBinding
    private val withdrawalViewModel: WithdrawalViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_withdrawal, container, false)
        with(binding) {
            viewModel = withdrawalViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")

        binding.backBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(WithdrawalFragmentDirections.actionWithdrawalFragmentToConfigFragment())
        }

        binding.withdrawalBtn.setOnClickListener {
            if (binding.checkbox.isChecked) {
                showDialog("Delete Account", "Upon withdrawal, all data will be deleted. Do you still want to leave?")
            } else {
                showDialogComplete("Please check", "Please check the withdrawal agreement.")
            }
        }
    }

    private fun showDialog(title: String, subTitle: String) {
        btScanningStatus.value = false
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(subTitle)
            setCancelable(false)
            setPositiveButton("Action") { _, _ ->
                // 탈퇴처리
                withdrawalViewModel.deleteAccount(userId.value.toString()) {
                    if (it) {
                        // TODO :: 탈퇴처리 후 화면 전환 처리
                    }
                }
            }
            setNegativeButton("Cancel") { _, _ -> }
        }
        builder.create().show()
    }

    private fun showDialogComplete(title: String, subTitle: String) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(subTitle)
            setCancelable(false)
            setPositiveButton("Action") { _, _ -> }
        }
        builder.create().show()
    }
}