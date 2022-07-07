package com.iodaniel.abbblinddating.registration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.iodaniel.abbblinddating.ActivityHome
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.databinding.FragmentSuccessRegBinding
import com.iodaniel.abbblinddating.viewModel.SignUpViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentSuccessReg : Fragment() {
    private lateinit var binding: FragmentSuccessRegBinding
    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private val scope = CoroutineScope(Dispatchers.IO)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSuccessRegBinding.inflate(inflater, container, false)
        binding.goToHome.setOnClickListener {
            val intent = Intent(requireContext(), ActivityHome::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
            scope.launch {
                delay(1200)
                requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
        return binding.root
    }
}