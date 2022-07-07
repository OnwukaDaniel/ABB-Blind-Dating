package com.iodaniel.abbblinddating.registration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.databinding.ActivityLandingBinding

class ActivityLanding : AppCompatActivity() {
    private val binding by lazy { ActivityLandingBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        binding.landingNext.setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack("sign in")
                .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                .replace(R.id.landing_root, FragmentSignIn())
                .commit()
        }
    }
}