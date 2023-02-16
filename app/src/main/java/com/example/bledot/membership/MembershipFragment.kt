package com.example.bledot.membership

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
    // Config 에서 넘어온 userInfo
    private val arg: MembershipFragmentArgs by navArgs()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")

        arg.userInfo.membership.let {
            BleDebugLog.d(logTag, "arg.membershipDate: $it")
            if (it == null) { // 멤버십 미등록
                membershipViewModel.membershipDate.value = ""
            } else { // 멤버십 등록
                membershipViewModel.membershipDate.value = it
                membershipViewModel.checkMembershipDate()
            }
        }
        // 멤버십 등록/미등록 판별
        membershipViewModel.membershipDate.observe(viewLifecycleOwner) { date ->
            if (date.isNotEmpty()) {
                binding.layoutExist.layout.visibility = View.VISIBLE
                binding.layoutNotExist.layout.visibility = View.INVISIBLE
            } else {
                binding.layoutExist.layout.visibility = View.INVISIBLE
                binding.layoutNotExist.layout.visibility = View.VISIBLE
            }
        }
        // 유효기간 유효/만료 판별
        membershipViewModel.isValid.observe(viewLifecycleOwner) { isValid ->
            if (isValid == true) {
                binding.layoutExist.editTextMembership.visibility = View.VISIBLE
                binding.layoutExist.editTextMembership.text = membershipViewModel.membershipDate.value
                binding.layoutExist.textViewExpired.visibility = View.INVISIBLE
            } else {
                binding.layoutExist.editTextMembership.visibility = View.INVISIBLE
                binding.layoutExist.textViewExpired.visibility = View.VISIBLE
            }
        }

        binding.backBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(MembershipFragmentDirections.actionMembershipFragmentToConfigFragment())
        }
        // 멤버십 첫 등록
        binding.layoutNotExist.regBtn.setOnClickListener {
            val membershipNum = binding.layoutNotExist.editTextMembership.text.toString()
            if (membershipNum.isNotEmpty()) {
                BleDebugLog.d(logTag, "membershipNum: $membershipNum")
                showDialog("Membership registration", "Would you like to register for membership?", membershipNum)
            }
        }
        // 멤버십 재등록
        binding.layoutExist.regBtn.setOnClickListener {
            membershipViewModel.membershipDate.value = ""
            membershipViewModel.isValid.value = false
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
                refreshView()
            }
        }
        builder.create().show()
    }

    private fun refreshView() {
        BleDebugLog.i(logTag, "refreshView-()")
        membershipViewModel.getUserInfo(arg.userInfo.email)
    }
}