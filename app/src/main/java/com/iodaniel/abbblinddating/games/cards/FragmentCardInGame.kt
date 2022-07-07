package com.iodaniel.abbblinddating.games.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.FragmentCardInGameBinding
import com.iodaniel.abbblinddating.dialogs.DialogQuitGame
import com.iodaniel.abbblinddating.viewModel.GeneralGameViewModel

class FragmentCardInGame : Fragment() {
    private lateinit var binding: FragmentCardInGameBinding
    private var recipientData = PersonData()
    private val generalGameViewModel: GeneralGameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCardInGameBinding.inflate(inflater, container, false)
        generalGameViewModel.personData.observe(viewLifecycleOwner) {
            recipientData = it
        }
        binding.cardGameBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().addToBackStack("quit")
                .replace(R.id.card_in_game_root, DialogQuitGame())
                .commit()
        }
        binding.cardGameQuit.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().addToBackStack("quit")
                .replace(R.id.card_in_game_root, DialogQuitGame())
                .commit()
        }
        return binding.root
    }
}