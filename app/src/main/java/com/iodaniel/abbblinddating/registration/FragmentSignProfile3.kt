package com.iodaniel.abbblinddating.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.databinding.FragmentSignProfile3Binding
import com.iodaniel.abbblinddating.util.Keyboard.hideKeyboard
import com.iodaniel.abbblinddating.viewModel.SignUpViewModel

class FragmentSignProfile3 : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentSignProfile3Binding
    private var friendshipTypeChoice: String = ""
    private var about: String = ""
    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private var friendshipType: ArrayList<String> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignProfile3Binding.inflate(inflater, container, false)
        friendshipType = arrayListOf(getString(R.string.friendship), getString(R.string.date), getString(R.string.serious_relationship),
            getString(R.string.marriage), getString(R.string.conversation), getString(R.string.long_term))
        binding.profile3Back.setOnClickListener(this)
        binding.profile3Continue.setOnClickListener(this)

        binding.profile3Friendship.setOnClickListener(this)
        binding.profile3Date.setOnClickListener(this)
        binding.profile3Serious.setOnClickListener(this)
        binding.profile3Marriage.setOnClickListener(this)
        binding.profile3Conversation.setOnClickListener(this)
        binding.profile3LongTerm.setOnClickListener(this)

        signUpViewModel.friendship.observe(viewLifecycleOwner) {
            friendshipTypeChoice = it
            clearBodyStyle()
            when (it) {
                friendshipType[0] -> binding.profile3Friendship.setBackgroundResource(R.drawable.rounded_selected)
                friendshipType[1] -> binding.profile3Date.setBackgroundResource(R.drawable.rounded_selected)
                friendshipType[2] -> binding.profile3Serious.setBackgroundResource(R.drawable.rounded_selected)
                friendshipType[3] -> binding.profile3Marriage.setBackgroundResource(R.drawable.rounded_selected)
                friendshipType[4] -> binding.profile3Conversation.setBackgroundResource(R.drawable.rounded_selected)
                friendshipType[5] -> binding.profile3LongTerm.setBackgroundResource(R.drawable.rounded_selected)
            }
        }

        return binding.root
    }

    private fun clearBodyStyle() {
        binding.profile3Friendship.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile3Date.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile3Serious.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile3Marriage.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile3Conversation.setBackgroundResource(R.drawable.rounded_unselected)
        binding.profile3LongTerm.setBackgroundResource(R.drawable.rounded_unselected)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.profile3_back -> requireActivity().onBackPressed()
            R.id.profile3_continue -> {
                hideKeyboard()
                about = binding.profile3About.text.trim().toString()
                signUpViewModel.setAbout(about)
                if (friendshipTypeChoice == "") {
                    Snackbar.make(binding.root, getString(R.string.select_your_preferred_friendship_choice), Snackbar.LENGTH_LONG).show()
                    return
                }
                if (about == "") {
                    Snackbar.make(binding.root, getString(R.string.write_about_yourself), Snackbar.LENGTH_LONG).show()
                    return
                }
                requireActivity().supportFragmentManager.beginTransaction().addToBackStack("sign_up")
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .replace(R.id.profile3_root, FragmentSignProfile4())
                    .commit()
            }
            R.id.profile3_friendship -> signUpViewModel.setFriendship(friendshipType[0])
            R.id.profile3_date -> signUpViewModel.setFriendship(friendshipType[1])
            R.id.profile3_serious -> signUpViewModel.setFriendship(friendshipType[2])
            R.id.profile3_marriage -> signUpViewModel.setFriendship(friendshipType[3])
            R.id.profile3_conversation -> signUpViewModel.setFriendship(friendshipType[4])
            R.id.profile3_long_term -> signUpViewModel.setFriendship(friendshipType[5])
        }
    }
}