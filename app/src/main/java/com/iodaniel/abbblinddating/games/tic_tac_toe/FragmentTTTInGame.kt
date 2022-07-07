package com.iodaniel.abbblinddating.games.tic_tac_toe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.FragmentTttInGameBinding
import com.iodaniel.abbblinddating.dialogs.DialogQuitGame
import com.iodaniel.abbblinddating.viewModel.GeneralGameViewModel

class FragmentTTTInGame : Fragment() {
    private lateinit var binding: FragmentTttInGameBinding
    private var recipientData = PersonData()
    private val generalGameViewModel: GeneralGameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTttInGameBinding.inflate(inflater, container, false)
        generalGameViewModel.personData.observe(viewLifecycleOwner) {
            recipientData = it
        }
        binding.tttGameBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().addToBackStack("quit")
                .replace(R.id.ttt_in_game_root, DialogQuitGame())
                .commit()
        }
        binding.tttGameQuit.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().addToBackStack("quit")
                .replace(R.id.ttt_in_game_root, DialogQuitGame())
                .commit()
        }
        return binding.root
    }
}