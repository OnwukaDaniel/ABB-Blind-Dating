package com.iodaniel.abbblinddating.home_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.FragmentFavFavBinding
import com.iodaniel.abbblinddating.viewModel.FavTabSelector
import com.iodaniel.abbblinddating.viewModel.FavViewModel
import com.iodaniel.abbblinddating.viewModel.ProfileViewModel

class FragmentFavFav : Fragment() {
    private lateinit var binding: FragmentFavFavBinding
    private val favAdapter = FavAdapter()
    private val favViewModel = FavViewModel()
    private var dataset: ArrayList<PersonData> = arrayListOf()
    private var datasetKeys: ArrayList<PersonData> = arrayListOf()
    private var similarDataset: ArrayList<PersonData> = arrayListOf()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFavFavBinding.inflate(inflater, container, false)
        favAdapter.activity = requireActivity()
        favAdapter.dataset = dataset
        binding.favFavRv.adapter = favAdapter
        binding.favFavRv.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        favViewModel.setTab(FavTabSelector.favourite)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel.favData.observe(viewLifecycleOwner) {

            for ((index, i) in it.withIndex()) {
                if (i in dataset) continue
                dataset.add(i)
                favAdapter.notifyItemInserted(index)
            }
            favAdapter.favProfileDataset = dataset
        }
        profileViewModel.personDataList.observe(viewLifecycleOwner){ personDataList->
            similarDataset = personDataList
        }
    }
}