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
            membershipViewModel.membershipDate = it
            BleDebugLog.d(logTag, "membershipViewModel.membershipDate: ${membershipViewModel.membershipDate}")
        }

//        binding.backBtn.setOnClickListener {
//            Navigation.findNavController(binding.root).navigate(MembershipFragmentDirections.actionMembershipFragmentToConfigFragment())
//        }
    }
}