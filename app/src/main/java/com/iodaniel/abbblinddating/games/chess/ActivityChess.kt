package com.iodaniel.abbblinddating.games.chess

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.ActivityChessBinding
import com.iodaniel.abbblinddating.viewModel.GeneralGameViewModel

class ActivityChess : AppCompatActivity() {
    private val binding by lazy { ActivityChessBinding.inflate(layoutInflater) }
    private var recipientData = PersonData()
    private val generalGameViewModel: GeneralGameViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getProfileData()
        Snackbar.make(binding.root, getString(R.string.not_yet_implemented), Snackbar.LENGTH_LONG).show()
        binding.chessBack.setOnClickListener { onBackPressed() }
    }

    private fun getProfileData() {
        if (intent.hasExtra(getString(R.string.profile))) {
            val profileJson = intent.getStringExtra(getString(R.string.profile))
            recipientData = Gson().fromJson(profileJson, PersonData::class.java)
            generalGameViewModel.setPersonData(recipientData)
            binding.chessPlay.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .addToBackStack("in game")
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left, R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                    .replace(R.id.chess_root, FragmentChessInGame())
                    .commit()
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}