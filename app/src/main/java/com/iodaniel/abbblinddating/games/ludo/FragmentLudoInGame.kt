package com.iodaniel.abbblinddating.games.ludo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.FragmentLudoInGameBinding
import com.iodaniel.abbblinddating.dialogs.DialogQuitGame
import com.iodaniel.abbblinddating.viewModel.GeneralGameViewModel

class FragmentLudoInGame : Fragment() {
    private lateinit var binding: FragmentLudoInGameBinding
    private var recipientData = PersonData()
    private val generalGameViewModel: GeneralGameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLudoInGameBinding.inflate(inflater, container, false)
        generalGameViewModel.personData.observe(viewLifecycleOwner) {
            recipientData = it
        }
        binding.ludoGameQuit.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().addToBackStack("quit")
                .replace(R.id.ludo_in_game_root, DialogQuitGame())
                .commit()
        }
        binding.ludoGameBack.setOnClickListener { requireActivity().onBackPressed() }
        return binding.root
    }
}