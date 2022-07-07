package com.iodaniel.abbblinddating.games.cards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.ActivityCardsBinding
import com.iodaniel.abbblinddating.viewModel.GeneralGameViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityCards : AppCompatActivity() {
    private val binding by lazy { ActivityCardsBinding.inflate(layoutInflater) }
    private var recipientData = PersonData()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val generalGameViewModel: GeneralGameViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Snackbar.make(binding.root, getString(R.string.not_yet_implemented), Snackbar.LENGTH_LONG).show()
        binding.cardBack.setOnClickListener { onBackPressed() }
        binding.cardPlay.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .addToBackStack("in game")
                .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left, R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                .replace(R.id.cards_root, FragmentCardInGame())
                .commit()
        }
        getProfileData()
    }

    private fun getProfileData() = scope.launch{
        if (intent.hasExtra("profile")) {
            val profileJson = intent.getStringExtra(getString(R.string.profile))
            recipientData = Gson().fromJson(profileJson, PersonData::class.java)
            runOnUiThread{ generalGameViewModel.setPersonData(recipientData) }
        }
    }
}