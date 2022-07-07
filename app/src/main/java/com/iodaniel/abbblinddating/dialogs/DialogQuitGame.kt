package com.iodaniel.abbblinddating.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.chat.ActivityChat
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.DialogQuitGameBinding
import com.iodaniel.abbblinddating.viewModel.GeneralGameViewModel

class DialogQuitGame : Fragment() {
    private lateinit var binding: DialogQuitGameBinding
    private var recipientData = PersonData()
    private val generalGameViewModel: GeneralGameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogQuitGameBinding.inflate(inflater, container, false)
        generalGameViewModel.personData.observe(viewLifecycleOwner) { recipientData = it }
        binding.dialogNegative.setOnClickListener { requireActivity().onBackPressed() }
        binding.dialogPositive.setOnClickListener {
            val intent = Intent(requireContext(), ActivityChat::class.java)
            val json = Gson().toJson(recipientData)
            intent.putExtra(getString(R.string.profile), json)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
        }
        return binding.root
    }
}