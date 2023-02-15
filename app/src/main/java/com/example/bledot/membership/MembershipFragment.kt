package com.example.bledot.membership

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
    // Config 에서 넘어온 userInfo.membershipDate
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

        arg.membershipDate.let {
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
                binding.layoutExist.editTextMembership.text = membershipViewModel.membershipDate.value
            } else {
                binding.layoutExist.editTextMembership.visibility = View.INVISIBLE
                binding.layoutExist.textViewExpired.visibility = View.VISIBLE
            }
        }

        binding.backBtn.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(MembershipFragmentDirections.actionMembershipFragmentToConfigFragment())
        }
    }
}