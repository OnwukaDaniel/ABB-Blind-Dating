package com.iodaniel.abbblinddating.call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.databinding.FragmentVideoBinding
import com.iodaniel.abbblinddating.viewModel.CallsViewModel

class FragmentVideo : Fragment() {
    private lateinit var binding: FragmentVideoBinding
    private val callsViewMode: CallsViewModel by activityViewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVideoBinding.inflate(inflater, container, false)
        binding.videoBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        Snackbar.make(binding.root, getString(R.string.not_yet_implemented), Snackbar.LENGTH_LONG).show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callsViewMode.personData.observe(viewLifecycleOwner) {
            Glide.with(requireContext()).load(it.image).centerCrop().into(binding.videoImage)
            binding.videoName.text = it.name
        }
    }
}