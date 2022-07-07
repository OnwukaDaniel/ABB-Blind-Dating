package com.iodaniel.abbblinddating.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.databinding.FragmentSignProfile2Binding
import com.iodaniel.abbblinddating.util.Keyboard.hideKeyboard
import com.iodaniel.abbblinddating.viewModel.SignUpViewModel

class FragmentSignProfile2 : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentSignProfile2Binding
    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private var maritalStatusChoice: String = ""
    private var bodyStyleChoice: String = ""
    private var dateSmokerChoice: String = ""
    private var bodyStyle: ArrayList<String> = arrayListOf()
    private var maritalStatus: ArrayList<String> = arrayListOf()
    private var dateSmoker: ArrayList<String> = arrayListOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignProfile2Binding.inflate(inflater, container, false)
        bodyStyle = arrayListOf(getString(R.string.slim), getString(R.string.exercise), getString(R.string.average),
                getString(R.string.tall), getString(R.string.attractive), getString(R.string.stallion_status),
                getString(R.string.don_t_say))
        maritalStatus = arrayListOf(getString(R.string.single), getString(R.string.married), getString(R.string.divorced),
                getString(R.string.separated), getString(R.string.long_relationship))
        dateSmoker = arrayListOf(getString(R.string.yes), getString(R.string.no))
        binding.profile2Continue.setOnClickListener(this)
        binding.profile2Single.setOnClickListener(this)
        binding.profile2Married.setOnClickListener(this)
        binding.profile2Divorced.setOnClickListener(this)
        binding.profile2Separated.setOnClickListener(this)
        binding.profile2Long.setOnClickListener(this)

        binding.profile2Slim.setOnClickListener(this)
        binding.profile2Exercise.setOnClickListener(this)
        binding.profile2Average.setOnClickListener(this)
        binding.profile2Tall.setOnClickListener(this)
        binding.profile2Attractive.setOnClickListener(this)
        binding.profile2Stallion.setOnClickListener(this)
        binding.profile2DontSay.setOnClickListener(this)

        binding.profile2SmokerNo.setOnClickListener(this)
        binding.profile2SmokerYes.setOnClickListener(this)

        signUpViewModel.maritalStatus.observe(viewLifecycleOwner) {
            maritalStatusChoice = it
            clearMaritalStatus()
            when (it) {
                maritalStatus[0] -> binding.profile2Single.setBackgroundResource(R.drawable.rounded_selected)
                maritalStatus[1] -> binding.profile2Married.setBackgroundResource(R.drawable.rounded_selected)
                maritalStatus[2] -> binding.profile2Divorced.setBackgroundResource(R.drawable.rounded_selected)
                maritalStatus[3] -> binding.profile2Separated.setBackgroundResource(R.drawable.rounded_selected)
                maritalStatus[4] -> binding.profile2Long.setBackgroundResource(R.drawable.rounded_selected)
            }
        }
        signUpViewModel.bodyStyle.observe(viewLifecycleOwner) {
            bodyStyleChoice = it
            clearBodyStyle()
            when (it) {
                bodyStyle[0] -> binding.profile2Slim.setBackgroundResource(R.drawable.rounded_selected)
                bodyStyle[1] -> binding.profile2Exercise.setBackgroundResource(R.drawable.rounded_selected)
                bodyStyle[2] -> binding.profile2Average.setBackgroundResource(R.drawable.rounded_selected)
                bodyStyle[3] -> binding.profile2Tall.setBackgroundResource(R.drawable.rounded_selected)
                bodyStyle[4] -> binding.profile2Attractive.setBackgroundResource(R.drawable.rounded_selected)
                bodyStyle[5] -> binding.profile2Stallion.setBackgroundResource(R.drawable.rounded_selected)
                bodyStyle[6] -> binding.profile2DontSay.setBackgroundResource(R.drawable.rounded_selected)
            }
        }
        signUpViewModel.dateSmoker.observe(viewLifecycleOwner) {
            dateSmokerChoice = it
            clearDateSmoker()
            when (it) {
                dateSmoker[0] -> binding.profile2SmokerYes.setBackgroundResource(R.drawable.rounded_selected)
                dateSmoker[1] -> binding.profile2SmokerNo.setBackgroundResource(R.drawable.rounded_selected)
            }
        }
        return binding.root
    }

    private fun clearMaritalStatus() {
        binding.profile2Single.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile2Married.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile2Divorced.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile2Separated.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile2Long.setBackgroundResource(R.drawable.rounded_unselected)
    }

    private fun clearBodyStyle(){
        binding.profile2Slim.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile2Exercise.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile2Average.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile2Tall.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile2Attractive.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile2Stallion.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile2DontSay.setBackgroundResource(R.drawable.rounded_unselected)
    }

    private fun clearDateSmoker() {
        binding.profile2SmokerYes.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile2SmokerNo.setBackgroundResource(R.drawable.rounded_unselected)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signUpViewModel.setMaritalStatus(maritalStatus[0])
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.profile2_single -> signUpViewModel.setMaritalStatus(maritalStatus[0])
            R.id.profile2_married -> signUpViewModel.setMaritalStatus(maritalStatus[1])
            R.id.profile2_divorced -> signUpViewModel.setMaritalStatus(maritalStatus[2])
            R.id.profile2_separated -> signUpViewModel.setMaritalStatus(maritalStatus[3])
            R.id.profile2_long -> signUpViewModel.setMaritalStatus(maritalStatus[4])
            // --------------------------- Body style -----------------------------------
            R.id.profile2_slim -> signUpViewModel.setBodyStyle(bodyStyle[0])
            R.id.profile2_exercise -> signUpViewModel.setBodyStyle(bodyStyle[1])
            R.id.profile2_average -> signUpViewModel.setBodyStyle(bodyStyle[2])
            R.id.profile2_tall -> signUpViewModel.setBodyStyle(bodyStyle[3])
            R.id.profile2_attractive -> signUpViewModel.setBodyStyle(bodyStyle[4])
            R.id.profile2_stallion -> signUpViewModel.setBodyStyle(bodyStyle[5])
            R.id.profile2_dont_say -> signUpViewModel.setBodyStyle(bodyStyle[6])
            // --------------------------- Date Smoker -----------------------------------
            R.id.profile2_smoker_yes -> signUpViewModel.setDateSmoker(dateSmoker[0])
            R.id.profile2_smoker_no -> signUpViewModel.setDateSmoker(dateSmoker[1])
            // --------------------------- Body style -----------------------------------
            R.id.profile2_back -> requireActivity().onBackPressed()
            R.id.profile2_continue -> {
                hideKeyboard()
                if(maritalStatusChoice == ""){
                    Snackbar.make(binding.root, getString(R.string.select_your_marital_status), Snackbar.LENGTH_LONG).show()
                    return
                }
                if(bodyStyleChoice == ""){
                    Snackbar.make(binding.root, getString(R.string.select_your_body_style), Snackbar.LENGTH_LONG).show()
                    return
                }
                if(dateSmokerChoice == ""){
                    signUpViewModel.setDateSmoker(dateSmoker[1])
                }

                requireActivity().supportFragmentManager.beginTransaction().addToBackStack("sign_up")
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .replace(R.id.profile2_root, FragmentSignProfile3())
                    .commit()
            }
        }
    }
}