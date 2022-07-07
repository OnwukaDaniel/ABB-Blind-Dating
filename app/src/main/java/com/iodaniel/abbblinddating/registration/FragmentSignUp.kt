package com.iodaniel.abbblinddating.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.databinding.FragmentSignUpBinding
import com.iodaniel.abbblinddating.dialogs.MessageDialog
import com.iodaniel.abbblinddating.util.Keyboard.hideKeyboard
import com.iodaniel.abbblinddating.viewModel.MessageDialogViewModel
import com.iodaniel.abbblinddating.viewModel.SignUpViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FragmentSignUp : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentSignUpBinding
    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private var success: Boolean = false
    private val messageDialogViewModel: MessageDialogViewModel by activityViewModels()
    private val scope = CoroutineScope(Dispatchers.IO)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding.signupBtn.setOnClickListener(this)
        binding.signupBack.setOnClickListener(this)
        binding.signupSigninText.setOnClickListener(this)
        return binding.root
    }

    private fun createUser() {
        hideKeyboard()
        val name = binding.signUpName.text.trim().toString()
        val phone = binding.signUpPhone.text.trim().toString()
        val password = binding.signUpPassword.text.trim().toString()
        val rePassword = binding.signUpRePassword.text.trim().toString()
        val email = binding.signUpEmail.text.trim().toString()

        if (name == "") {
            Snackbar.make(binding.root, getString(R.string.name_cant_be_empty), Snackbar.LENGTH_LONG).show()
            return
        }

        if (phone == "") {
            Snackbar.make(binding.root, getString(R.string.enter_phone_number), Snackbar.LENGTH_LONG).show()
            return
        }

        if (email == "") {
            Snackbar.make(binding.root, getString(R.string.enter_email), Snackbar.LENGTH_LONG).show()
            return
        }

        if (password == "") {
            Snackbar.make(binding.root, getString(R.string.enter_password), Snackbar.LENGTH_LONG).show()
            return
        }

        if (rePassword == "") {
            Snackbar.make(binding.root, "Re-enter Password", Snackbar.LENGTH_LONG).show()
            return
        }

        if (rePassword != password) {
            Snackbar.make(binding.root, getString(R.string.passwords_dont_match), Snackbar.LENGTH_LONG).show()
            return
        }

        signUpViewModel.setEmailPasswordPair(mapOf("email" to email))
        signUpViewModel.setName(name)
        signUpViewModel.setPhone(phone)

        binding.signupContinueText.visibility = View.INVISIBLE
        binding.signupProgress.visibility = View.VISIBLE

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                sendVerificationEmail()
                binding.signupContinueText.visibility = View.VISIBLE
                binding.signupProgress.visibility = View.INVISIBLE
            }.addOnFailureListener {
                it.printStackTrace()
                binding.signupContinueText.visibility = View.VISIBLE
                binding.signupProgress.visibility = View.INVISIBLE
                Snackbar.make(binding.root, "${it.localizedMessage}", Snackbar.LENGTH_LONG).show()
            }

        scope.launch {
            delay(10_000)
            if (!success) {
                if (!this@FragmentSignUp.isDetached)
                    requireActivity().runOnUiThread {
                        binding.signupContinueText.visibility = View.VISIBLE
                        binding.signupProgress.visibility = View.INVISIBLE
                    }
            }
        }
    }

    private fun sendVerificationEmail() {
        val auth = FirebaseAuth.getInstance().currentUser
        auth!!.sendEmailVerification()
            .addOnCompleteListener {
                getString(R.string.profile)
                if (it.isSuccessful) {
                    hideKeyboard()
                    success = true
                    binding.signupContinueText.visibility = View.VISIBLE
                    binding.signupProgress.visibility = View.INVISIBLE
                    messageDialogViewModel.setInflateAction(FragmentSignProfile1())
                    val subMsg = getString(R.string.hint_check_your_spam_folder)
                    val msg1 = getString(R.string.successful_display)
                    val msg2 = getString(R.string.verify_info)

                    messageDialogViewModel.setMessage("$msg1\n\n${getString(R.string.check_your_email_for_verification_link)}\n$msg2")
                    messageDialogViewModel.setSubMessage(subMsg)
                    requireActivity().supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.signup_root, MessageDialog())
                        .commit()
                }
            }.addOnFailureListener {
                binding.signupContinueText.visibility = View.VISIBLE
                binding.signupProgress.visibility = View.INVISIBLE
                it.printStackTrace()
                Snackbar.make(binding.root, "${it.localizedMessage}", Snackbar.LENGTH_LONG).show()
            }
    }

    private suspend fun setTimer() {
        val timer = (0..120)
            .asSequence()
            .asFlow()
            .onEach { delay(1_000) }
        timer.collect {
            requireActivity().runOnUiThread { signUpViewModel.setResendTimer(it.toString()) }
            if (it == 20) requireActivity().runOnUiThread { signUpViewModel.setActivateResend(true) }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.signup_signin_text -> {
                hideKeyboard()
                requireActivity().supportFragmentManager.beginTransaction().addToBackStack("sign_in")
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .replace(R.id.signup_root, FragmentSignIn())
                    .commit()
            }
            R.id.signup_btn -> createUser()
            R.id.signup_back -> requireActivity().onBackPressed()
        }
    }
}