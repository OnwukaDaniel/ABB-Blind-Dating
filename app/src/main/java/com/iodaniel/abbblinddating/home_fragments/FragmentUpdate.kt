package com.iodaniel.abbblinddating.home_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.databinding.FragmentUpdateBinding

class FragmentUpdate : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentUpdateBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUpdateBinding.inflate(inflater, container, false)
        binding.updateBack.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.update_back-> requireActivity().onBackPressed()
        }
    }
}