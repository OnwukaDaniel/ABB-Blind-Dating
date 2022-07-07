package com.iodaniel.abbblinddating.registration

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.iodaniel.abbblinddating.ActivityHome
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.databinding.FragmentSignInBinding
import com.iodaniel.abbblinddating.util.Keyboard.hideKeyboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentSignIn : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentSignInBinding
    private val auth = FirebaseAuth.getInstance().currentUser
    private lateinit var pref: SharedPreferences
    private var success: Boolean = false
    private val scope = CoroutineScope(Dispatchers.IO)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        pref = requireActivity().getSharedPreferences(getString(R.string.ALL_PREF), Context.MODE_PRIVATE)
        binding.signinBack.setOnClickListener(this)
        binding.signinLogIn.setOnClickListener(this)
        binding.signinSignUpText.setOnClickListener(this)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val window = requireActivity().window;
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.statusBarColor = Color.parseColor("#1DA2D8")
        val remember = pref.getBoolean(getString(R.string.remember_sign_in), false)
        if (remember) {
            binding.checkBox.isChecked = remember
            binding.signinEmail.setText(pref.getString(getString(R.string.sign_in_email), ""))
            binding.signinPassword.setText(pref.getString(getString(R.string.sign_in_password), ""))
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.signin_back -> requireActivity().onBackPressed()
            R.id.signin_log_in -> {
                hideKeyboard()
                val email = binding.signinEmail.text.trim().toString()
                val password = binding.signinPassword.text.trim().toString()
                if (email == "" || password == "") {
                    Snackbar.make(binding.root, getString(R.string.empty_email_password), Snackbar.LENGTH_LONG).show()
                    return
                }

                scope.launch {
                    delay(10_000)
                    if (!success) {
                        binding.signinContinueText.visibility = View.VISIBLE
                        binding.signinProgress.visibility = View.INVISIBLE
                    }
                }

                binding.signinContinueText.visibility = View.INVISIBLE
                binding.signinProgress.visibility = View.VISIBLE

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        if (binding.checkBox.isChecked) {
                            pref.edit().putBoolean(getString(R.string.remember_sign_in), binding.checkBox.isChecked).apply()
                            pref.edit().putString(getString(R.string.sign_in_email), email).apply()
                            pref.edit().putString(getString(R.string.sign_in_password), password).apply()
                        }
                        success = true
                        binding.signinContinueText.visibility = View.VISIBLE
                        binding.signinProgress.visibility = View.INVISIBLE
                        val intent = Intent(requireContext(), ActivityHome::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        requireActivity().overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    }.addOnFailureListener {
                        binding.signinContinueText.visibility = View.VISIBLE
                        binding.signinProgress.visibility = View.INVISIBLE
                        Snackbar.make(binding.root, it.localizedMessage!!, Snackbar.LENGTH_LONG).show()
                    }
            }
            R.id.signin_sign_up_text -> {
                requireActivity().supportFragmentManager.beginTransaction().addToBackStack("sign_up")
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .replace(R.id.signin_root, FragmentSignUp())
                    .commit()
            }
        }
    }
}