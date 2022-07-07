package com.iodaniel.abbblinddating.home_fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.databinding.FragmentMyProfileTabBinding
import com.iodaniel.abbblinddating.viewModel.ProfileViewModel

class FragmentMyProfileTab : Fragment() {
    private lateinit var binding: FragmentMyProfileTabBinding
    private val myInterestAdapter = MyInterestAdapter()
    private var dataset: ArrayList<String> = arrayListOf()
    private var listMonths: ArrayList<String> = arrayListOf()
    private val profileViewModel: ProfileViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMyProfileTabBinding.inflate(inflater, container, false)
        listMonths = arrayListOf(getString(R.string.january), getString(R.string.february), getString(R.string.march), getString(R.string.april),
            getString(R.string.may), getString(R.string.june), getString(R.string.july), getString(R.string.august),
            getString(R.string.september), getString(R.string.october), getString(R.string.november), getString(R.string.december))
        profileViewModel.personData.observe(viewLifecycleOwner) {
            binding.myProfileAbout.text = it.about
            val date = "${it.birthday["day"]}-${listMonths[it.birthday["month"]!!]}-${it.birthday["year"]}"
            binding.myProfileBirthday.text = date
            binding.myProfileBodyStyle.text = it.bodyStyle
            binding.myProfileEducation.text = it.education
            var langs = ""
            if (it.language.size > 1) {
                for (i in it.language) langs += "$i\n"
                langs.removeRange(langs.length - 2, langs.length)
            } else langs = it.language.first()
            binding.myProfileLanguages.text = langs
            binding.myProfileUniversity.text = it.university
            binding.myProfileMaritalStatus.text = it.maritalStatus
            binding.myProfileSex.text = it.genderChoice
            dataset = it.interests
            myInterestAdapter.dataset = dataset
            binding.myProfileInterestRv.adapter = myInterestAdapter
            binding.myProfileInterestRv.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
            myInterestAdapter.notifyDataSetChanged()
        }
        return binding.root
    }
}

class MyInterestAdapter : RecyclerView.Adapter<MyInterestAdapter.ViewHolder>() {
    var dataset: ArrayList<String> = arrayListOf()
    private lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt: TextView = itemView.findViewById(R.id.text_chip_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.text_chip_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = dataset[position]
        holder.txt.setBackgroundColor(Color.WHITE)
        holder.txt.text = datum
    }

    override fun getItemCount() = dataset.size
}