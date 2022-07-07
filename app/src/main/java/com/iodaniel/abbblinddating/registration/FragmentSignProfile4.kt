package com.iodaniel.abbblinddating.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.databinding.FragmentSignProfile4Binding
import com.iodaniel.abbblinddating.util.Keyboard.hideKeyboard
import com.iodaniel.abbblinddating.viewModel.SignUpViewModel

class FragmentSignProfile4 : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentSignProfile4Binding
    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private var educationChoice: String = ""
    private var education: ArrayList<String> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignProfile4Binding.inflate(inflater, container, false)
        education = arrayListOf(getString(R.string.bachelor), getString(R.string.master), getString(R.string.graduate),
            getString(R.string.none), getString(R.string.doctor), getString(R.string.some_universities), getString(R.string.no_college))
        binding.profile4Back.setOnClickListener(this)
        binding.profile4Continue.setOnClickListener(this)

        binding.profile4Bachelor.setOnClickListener(this)
        binding.profile4Master.setOnClickListener(this)
        binding.profile4Graduate.setOnClickListener(this)
        binding.profile4Do.setOnClickListener(this)
        binding.profile4Doctor.setOnClickListener(this)
        binding.profile4SomeUniversity.setOnClickListener(this)
        binding.profile4NoCollege.setOnClickListener(this)

        signUpViewModel.education.observe(viewLifecycleOwner) {
            educationChoice = it
            clearEducation()
            when (it) {
                education[0] -> binding.profile4Bachelor.setBackgroundResource(R.drawable.rounded_selected)
                education[1] -> binding.profile4Master.setBackgroundResource(R.drawable.rounded_selected)
                education[2] -> binding.profile4Graduate.setBackgroundResource(R.drawable.rounded_selected)
                education[3] -> binding.profile4Do.setBackgroundResource(R.drawable.rounded_selected)
                education[4] -> binding.profile4Doctor.setBackgroundResource(R.drawable.rounded_selected)
                education[5] -> binding.profile4SomeUniversity.setBackgroundResource(R.drawable.rounded_selected)
                education[6] -> binding.profile4NoCollege.setBackgroundResource(R.drawable.rounded_selected)
            }
        }

        return binding.root
    }

    private fun clearEducation() {
        binding.profile4Bachelor.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile4Master.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile4Graduate.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile4Do.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile4Doctor.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile4SomeUniversity.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile4NoCollege.setBackgroundResource(R.drawable.rounded_unselected)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.profile4_back -> requireActivity().onBackPressed()
            R.id.profile4_continue -> {
                hideKeyboard()
                val profile4University = binding.profile4University.text.trim().toString()
                val profile4HighSchool = binding.profile4HighSchool.text.trim().toString()
                if (profile4University == "") {
                    Snackbar.make(binding.root, getString(R.string.enter_your_university_name_or_null), Snackbar.LENGTH_LONG).show()
                    return
                }
                if (profile4HighSchool == "") {
                    Snackbar.make(binding.root, getString(R.string.choose_your_high_school_name_or_null), Snackbar.LENGTH_LONG).show()
                    return
                }
                if (educationChoice == "") {
                    Snackbar.make(binding.root, getString(R.string.choose_your_education_level_type), Snackbar.LENGTH_LONG).show()
                    return
                }
                signUpViewModel.setUniversity(profile4University)
                signUpViewModel.setHighSchool(profile4HighSchool)
                requireActivity().supportFragmentManager.beginTransaction().addToBackStack("sign_up")
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .replace(R.id.profile4_root, FragmentSignProfile5())
                    .commit()
            }
            R.id.profile4_bachelor -> signUpViewModel.setEducation(education[0])
            R.id.profile4_master -> signUpViewModel.setEducation(education[1])
            R.id.profile4_graduate -> signUpViewModel.setEducation(education[2])
            R.id.profile4_do -> signUpViewModel.setEducation(education[3])
            R.id.profile4_doctor -> signUpViewModel.setEducation(education[4])
            R.id.profile4_some_university -> signUpViewModel.setEducation(education[5])
            R.id.profile4_no_college -> signUpViewModel.setEducation(education[6])

        }
    }
}