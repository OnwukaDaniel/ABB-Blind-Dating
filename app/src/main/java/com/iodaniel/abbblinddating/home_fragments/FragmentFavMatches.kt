package com.iodaniel.abbblinddating.home_fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.FragmentFavMatchesBinding
import com.iodaniel.abbblinddating.viewModel.FavTabSelector
import com.iodaniel.abbblinddating.viewModel.FavViewModel
import com.iodaniel.abbblinddating.viewModel.ProfileViewModel

class FragmentFavMatches : Fragment() {
    private lateinit var binding: FragmentFavMatchesBinding
    private val favAdapter = FavAdapter()
    private lateinit var pref: SharedPreferences
    private var favProfileDataset: ArrayList<PersonData> = arrayListOf()
    private var dataset: ArrayList<PersonData> = arrayListOf()
    private val favViewModel = FavViewModel()
    private var myProfileData = PersonData()
    private var myInterests: ArrayList<String> = arrayListOf()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFavMatchesBinding.inflate(inflater, container, false)
        pref = requireActivity().getSharedPreferences(getString(R.string.ALL_PREF), Context.MODE_PRIVATE)
        favAdapter.activity = requireActivity()
        favAdapter.dataset = dataset
        favAdapter.favProfileDataset = favProfileDataset
        binding.favMatchesRv.adapter = favAdapter
        binding.favMatchesRv.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        favViewModel.setTab(FavTabSelector.favourite)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel.personData.observe(viewLifecycleOwner) {
            myProfileData = it
            myInterests = it.interests
        }
        profileViewModel.favData.observe(viewLifecycleOwner) {
            if (dataset.size == dataset.size) dataset.clear()
            for ((index, i) in it.withIndex()) {
                if (i in dataset) continue
                dataset.add(i)
                favAdapter.notifyItemInserted(index)
            }
            favAdapter.favProfileDataset = dataset
        }
        profileViewModel.personDataList.observe(viewLifecycleOwner) {
            it.shuffle()
            for ((index, data) in it.withIndex()) {
                if (data in dataset) break
                for (i in data.interests) {
                    if (i in myInterests) {
                        dataset.add(data)
                        if (dataset.size > 25) dataset = dataset.subList(0, 25) as ArrayList<PersonData> else dataset = it
                        favAdapter.notifyItemInserted(index)
                        break
                    }
                }
            }
        }
    }
}