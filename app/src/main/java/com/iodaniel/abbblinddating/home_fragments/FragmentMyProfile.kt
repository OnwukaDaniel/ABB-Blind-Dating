package com.iodaniel.abbblinddating.home_fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.FragmentMyProfileBinding
import com.iodaniel.abbblinddating.liveData.ProfileLiveData
import com.iodaniel.abbblinddating.util.ChildEventTemplate
import com.iodaniel.abbblinddating.viewModel.HomeViewModel
import com.iodaniel.abbblinddating.viewModel.ProfileViewModel

class FragmentMyProfile : Fragment() {
    private lateinit var binding: FragmentMyProfileBinding
    private var ref = FirebaseDatabase.getInstance().reference
    private var auth = FirebaseAuth.getInstance().currentUser
    private lateinit var profileLiveData: ProfileLiveData
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        homeViewModel.myProfileData.observe(viewLifecycleOwner) {
            Glide.with(requireActivity()).load(it.image).centerCrop().into(binding.myProfileImage)
            binding.myProfileName.text = it.name
            binding.myProfileLocation.text = it.location
        }
        ref = ref.child(getString(R.string.profile)).child(auth!!.uid)
        profileLiveData = ProfileLiveData(ref)
        profileLiveData.observe(viewLifecycleOwner){
            when(it.second){
                ChildEventTemplate.onDataChange->{
                    val personData = it.first.getValue(PersonData::class.java)!!
                    profileViewModel.setPersonData(personData)
                    if (personData.image != "") Glide.with(requireContext()).load(Uri.parse(personData.image)).centerCrop().into(binding.myProfileImage)
                    binding.myProfileName.text = personData.name
                    binding.myProfileLocation.text = personData.location
                }
            }
        }

        profileViewModel.personDataList.observe(viewLifecycleOwner) {

        }

        val viewPagerAdapter = ViewPagerAdapter(requireActivity())
        binding.myProfileViewPager.adapter = viewPagerAdapter
        val dataNames = arrayListOf(getString(R.string.my_profile_display), getString(R.string.images_display))
        val tabLM = TabLayoutMediator(
            binding.myProfileTablayout,
            binding.myProfileViewPager
        ) { tab, position ->
            tab.text = dataNames[position]
        }
        if (!tabLM.isAttached) tabLM.attach()
        return binding.root
    }

    inner class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        var dataset: ArrayList<Fragment> = arrayListOf(FragmentMyProfileTab(), FragmentMyImagesTab())
        override fun getItemCount() = dataset.size

        override fun createFragment(position: Int) = dataset[position]
    }
}