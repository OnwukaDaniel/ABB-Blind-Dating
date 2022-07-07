package com.iodaniel.abbblinddating.games.truth_or_dare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.ActivityTruthOrDareBinding
import com.iodaniel.abbblinddating.viewModel.GeneralGameViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityTruthOrDare : AppCompatActivity() {
    private val binding by lazy { ActivityTruthOrDareBinding.inflate(layoutInflater) }
    private var recipientData = PersonData()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val generalGameViewModel: GeneralGameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Snackbar.make(binding.root, getString(R.string.not_yet_implemented), Snackbar.LENGTH_LONG).show()
        binding.totPickDare.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left, R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                .replace(R.id.tod_root, FragmentTODInGame())
                .commit()
        }
        binding.totPickTruth.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left, R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                .replace(R.id.tod_root, FragmentTODInGame())
                .commit()
        }
        binding.totBack.setOnClickListener { onBackPressed() }
        getProfileData()
    }

    private fun getProfileData() = scope.launch{
        if (intent.hasExtra(getString(R.string.profile))) {
            val profileJson = intent.getStringExtra(getString(R.string.profile))
            recipientData = Gson().fromJson(profileJson, PersonData::class.java)
            runOnUiThread{ generalGameViewModel.setPersonData(recipientData) }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0,0)
    }
}