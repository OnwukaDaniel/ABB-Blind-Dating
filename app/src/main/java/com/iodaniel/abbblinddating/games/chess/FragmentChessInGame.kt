package com.iodaniel.abbblinddating.games.chess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.FragmentChessInGameBinding
import com.iodaniel.abbblinddating.dialogs.DialogQuitGame
import com.iodaniel.abbblinddating.viewModel.GeneralGameViewModel

class FragmentChessInGame : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentChessInGameBinding
    private var recipientData = PersonData()
    private val generalGameViewModel: GeneralGameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChessInGameBinding.inflate(inflater, container, false)
        generalGameViewModel.personData.observe(viewLifecycleOwner) { recipientData = it }
        binding.chessGameBack.setOnClickListener(this)
        binding.chessGameQuit.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.chess_game_back -> requireActivity().onBackPressed()
            R.id.chess_game_quit -> {
                requireActivity().supportFragmentManager.beginTransaction().addToBackStack("quit")
                    .replace(R.id.chess_in_game_root, DialogQuitGame())
                    .commit()
            }
        }
    }
}