package com.iodaniel.abbblinddating.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.databinding.MessageDialogBinding
import com.iodaniel.abbblinddating.viewModel.MessageDialogViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MessageDialog : Fragment() {
    private lateinit var binding: MessageDialogBinding
    private val messageDialogViewModel: MessageDialogViewModel by activityViewModels()
    private val scope = CoroutineScope(Dispatchers.IO)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = MessageDialogBinding.inflate(inflater, container, false)
        messageDialogViewModel.message.observe(viewLifecycleOwner) { binding.messageDialogMsg.text = it }
        messageDialogViewModel.subMessage.observe(viewLifecycleOwner) { binding.messageDialogSubMsg.text = it }

        messageDialogViewModel.positiveAction.observe(viewLifecycleOwner) {
            binding.messageDialogOk.setOnClickListener { _ ->
                it.run()
            }
        }
        binding.messageDialogCancel.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }
        messageDialogViewModel.inflateAction.observe(viewLifecycleOwner) {
            binding.messageDialogOk.setOnClickListener { _ ->
                requireActivity().supportFragmentManager.beginTransaction()
                    .addToBackStack("next")
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left, R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                    .replace(R.id.landing_root, it).commit()
                scope.launch {
                    delay(500)
                    requireActivity().runOnUiThread { requireActivity().supportFragmentManager.beginTransaction().remove(this@MessageDialog).commit() }
                }
            }
        }
        return binding.root
    }
}