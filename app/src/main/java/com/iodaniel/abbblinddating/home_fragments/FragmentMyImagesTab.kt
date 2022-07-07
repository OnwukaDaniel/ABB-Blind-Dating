package com.iodaniel.abbblinddating.home_fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.databinding.FragmentMyImagesTabBinding
import com.iodaniel.abbblinddating.viewModel.FavTabSelector
import com.iodaniel.abbblinddating.viewModel.FavViewModel
import com.iodaniel.abbblinddating.viewModel.ProfileViewModel

class FragmentMyImagesTab : Fragment() {
    private lateinit var binding: FragmentMyImagesTabBinding
    private val mMyImageAdapter = MyImageAdapter()
    private val favViewModel = FavViewModel()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMyImagesTabBinding.inflate(inflater, container, false)
        mMyImageAdapter.activity = requireActivity()
        binding.myImagesRv.adapter = mMyImageAdapter
        binding.myImagesRv.layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        favViewModel.setTab(FavTabSelector.favourite)
        profileViewModel.personData.observe(viewLifecycleOwner){
            mMyImageAdapter.dataset = it.profileImages
        }
        return binding.root
    }
}

class MyImageAdapter : RecyclerView.Adapter<MyImageAdapter.ViewHolder>() {
    var dataset: ArrayList<Map<String, String>> = arrayListOf()
    private lateinit var context: Context
    lateinit var activity: Activity

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.my_images)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.my_images_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = dataset[position]
        Glide.with(context).load(datum.values.first()).centerCrop().into(holder.image)
        holder.itemView.setOnClickListener {
        }
    }

    override fun getItemCount() = dataset.size
}