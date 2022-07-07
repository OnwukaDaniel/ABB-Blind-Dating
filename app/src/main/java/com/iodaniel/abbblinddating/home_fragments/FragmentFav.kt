package com.iodaniel.abbblinddating.home_fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.FragmentFavBinding
import com.iodaniel.abbblinddating.viewModel.FavTabSelector
import com.iodaniel.abbblinddating.viewModel.FavViewModel
import com.iodaniel.abbblinddating.viewModel.ProfileViewModel

class FragmentFav : Fragment() {
    private lateinit var binding: FragmentFavBinding
    private val favViewModel = FavViewModel()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFavBinding.inflate(inflater, container, false)
        val viewPagerAdapter = ViewPagerAdapter(requireActivity())
        binding.favViewpager.adapter = viewPagerAdapter
        favViewModel.setTab(FavTabSelector.favourite)
        val dataNames = arrayListOf(getString(R.string.favourite_display), getString(R.string.matches_display))
        val tabLM = TabLayoutMediator(
            binding.favTabLayout,
            binding.favViewpager
        ) { tab, position ->
            tab.text = dataNames[position]
        }
        if (!tabLM.isAttached) tabLM.attach()
        profileViewModel.personDataList.observe(viewLifecycleOwner) {
        }
        return binding.root
    }

    inner class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        private var dataset: ArrayList<Fragment> = arrayListOf(FragmentFavFav(), FragmentFavMatches())
        override fun getItemCount() = dataset.size

        override fun createFragment(position: Int) = dataset[position]
    }
}

class FavAdapter : RecyclerView.Adapter<FavAdapter.ViewHolder>() {
    private lateinit var pref: SharedPreferences
    var dataset: ArrayList<PersonData> = arrayListOf()
    var favProfileDataset: ArrayList<PersonData> = arrayListOf()
    private var refFav = FirebaseDatabase.getInstance().reference
    private var similarDataset: ArrayList<PersonData> = arrayListOf()
    private lateinit var context: Context
    lateinit var activity: Activity

    init {
        dataset.shuffle()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.fav_name)
        val about: TextView = itemView.findViewById(R.id.fav_about)
        val image: ImageView = itemView.findViewById(R.id.fav_image)
        val fab: FloatingActionButton = itemView.findViewById(R.id.fav_row_fab)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        pref = context.getSharedPreferences(context.getString(R.string.ALL_PREF), Context.MODE_PRIVATE)
        val view = LayoutInflater.from(context).inflate(R.layout.fav_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = dataset[position]
        Glide.with(context).load(datum.image).centerCrop().into(holder.image)
        holder.name.text = datum.name
        holder.about.text = datum.about
        holder.itemView.setOnClickListener {
            val json = Gson().toJson(datum)
            val intent = Intent(context, ActivityOtherProfile::class.java)
            intent.putExtra(context.getString(R.string.profile), json)
            context.startActivity(intent)
            activity.overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
        }

        holder.fab.setOnClickListener {
            val d = dataset[holder.adapterPosition]
            if (!favProfileDataset.contains(d)) {
                favProfileDataset.add(d)
                refFav.setValue(favProfileDataset).addOnSuccessListener {
                    Toast.makeText(context, context.getString(R.string.added_to_favorite), Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    Toast.makeText(context, context.getString(R.string.network_error_connect), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun getItemCount() = dataset.size
}