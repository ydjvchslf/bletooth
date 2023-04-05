package com.example.bledot.membership

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.bledot.R
import com.example.bledot.databinding.FragmentMembershipBinding
import com.example.bledot.util.BleDebugLog

class MembershipFragment : Fragment() {

    private val logTag = MembershipFragment::class.simpleName
    private lateinit var binding: FragmentMembershipBinding
    private val membershipViewModel: MembershipViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_membership, container, false)
        with(binding) {
            viewModel = membershipViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")
        // 멤버십 조회
        checkCurrentMembership()
        // 멤버십 등록
        binding.layoutNotExist.regBtn.setOnClickListener {
            val membershipNum = binding.layoutNotExist.editTextMembership.text.toString()
            if (membershipNum.isNotEmpty()) {
                BleDebugLog.d(logTag, "사용자 입력 code: $membershipNum")
                showDialog("Membership registration", "Would you like to register for membership?", membershipNum)
            }
        }
        // 멤버십 재등록
        binding.layoutExist.reRegBtn.setOnClickListener {
            binding.layoutNotExist.layout.visibility = View.VISIBLE
            binding.layoutExist.layout.visibility = View.INVISIBLE
        }
        // back btn
        binding.backBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(MembershipFragmentDirections.actionMembershipFragmentToConfigFragment())
        }
    }

    private fun showDialog(title: String, subTitle: String, memNum: String) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(subTitle)
            setCancelable(false)
            setPositiveButton("Action") { _, _ ->
                membershipViewModel.registerMembership(memNum) { isRegistered ->
                    if (isRegistered) {
                        binding.layoutNotExist.editTextMembership.text = null
                        showDialogComplete("Complete", "Membership registration is complete.")
                    } else {
                        showDialogComplete("Please check", "Invalid membership number.\nPlease check again.")
                    }
                }
            }
            setNegativeButton("Cancel") { _, _ -> }
        }
        builder.create().show()
    }

    private fun showDialogComplete(title: String, subTitle: String,) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(subTitle)
            setCancelable(false)
            setPositiveButton("Action") { _, _ ->
                // 등록된 멤버십으로 화면 새로고침
                checkCurrentMembership()
            }
        }
        builder.create().show()
    }

    @SuppressLint("SetTextI18n")
    private fun checkCurrentMembership() {
        BleDebugLog.i(logTag, "checkCurrentMembership-()")

        membershipViewModel.checkMembership { mbsEntity ->
            when (mbsEntity?.mbsStatus) {
                "Y" -> { // Yes
                    binding.layoutExist.layout.visibility = View.VISIBLE
                    binding.layoutNotExist.layout.visibility = View.INVISIBLE
                    binding.layoutExist.editTextMembership.text = "${mbsEntity.startDate} ~ ${mbsEntity.expDate}"
                }
                "E" -> { // Expired
                    binding.layoutExist.layout.visibility = View.VISIBLE
                    binding.layoutNotExist.layout.visibility = View.INVISIBLE
                    binding.layoutExist.editTextMembership.text = "Membership expiration"
                    binding.layoutExist.reRegBtn.visibility = View.VISIBLE
                }
                "P" -> { // Pending
                    binding.layoutExist.layout.visibility = View.VISIBLE
                    binding.layoutNotExist.layout.visibility = View.INVISIBLE
                    binding.layoutExist.editTextMembership.text = "Pending"
                }
                "N" -> { // None
                    binding.layoutExist.layout.visibility = View.INVISIBLE
                    binding.layoutNotExist.layout.visibility = View.VISIBLE
                }
                else -> { }
            }
        }
    }
}