package com.iodaniel.abbblinddating.home_fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.chat.ActivityChat
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.FragmentSearchBinding
import com.iodaniel.abbblinddating.liveData.ProfileLiveData
import com.iodaniel.abbblinddating.util.ChildEventTemplate

class FragmentSearch : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentSearchBinding
    private val searchResultAdapter = SearchResultAdapter()
    private var personDataset: ArrayList<PersonData> = arrayListOf()
    private var personSearchDataset: ArrayList<PersonData> = arrayListOf()
    private var ref = FirebaseDatabase.getInstance().reference
    private var auth = FirebaseAuth.getInstance().currentUser
    private lateinit var profileLiveData: ProfileLiveData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.searchBody.setOnClickListener(this)
        binding.searchHeader.setOnClickListener(this)
        binding.searchBack.setOnClickListener(this)
        binding.searchSettings.setOnClickListener(this)
        binding.searchResultRv.adapter = searchResultAdapter
        binding.searchResultRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        ref = ref.child(getString(R.string.profile))
        profileLiveData = ProfileLiveData(ref)
        profileLiveData.observe(viewLifecycleOwner) {
            when (it.second) {
                ChildEventTemplate.onDataChange -> {
                    personDataset.clear()
                    val d: ArrayList<PersonData> = arrayListOf()
                    for ((index, i) in it.first.children.withIndex()) {
                        val personData = i.getValue(PersonData::class.java)!!
                        if (personData.emailPasswordPair["email"].toString().trim() == auth!!.email.toString().trim()) continue
                        d.add(personData)
                    }
                    personDataset = d
                }
            }
        }
        binding.notificationInput.addTextChangedListener(InputValidator())
        return binding.root
    }

    inner class InputValidator : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun afterTextChanged(s: Editable?) {
            if (personDataset.isEmpty()) {
                Snackbar.make(binding.root, getString(R.string.getting_data), Snackbar.LENGTH_LONG).show()
                return
            }
            if (s.toString().trim() == "") return
            personSearchDataset.clear()
            binding.searchResultRv.adapter = searchResultAdapter
            searchResultAdapter.dataset = personSearchDataset
            for (person in personDataset) {
                if (person.name.trim().lowercase().contains(s.toString().trim().lowercase())) {
                    personSearchDataset.add(person)
                    searchResultAdapter.notifyItemInserted(personSearchDataset.size)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.search_body -> {}
            R.id.search_header -> requireActivity().onBackPressed()
            R.id.search_back -> requireActivity().onBackPressed()
            R.id.search_settings -> {}
        }
    }

    inner class SearchResultAdapter : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {
        var dataset: ArrayList<PersonData> = arrayListOf()
        private lateinit var context: Context

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val notificationInsert: ImageView = itemView.findViewById(R.id.search_insert)
            val notificationText: TextView = itemView.findViewById(R.id.search_text)
            val notificationRemove: ImageView = itemView.findViewById(R.id.search_remove)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            context = parent.context
            val view = LayoutInflater.from(context).inflate(R.layout.search_result_row, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val datum = dataset[position]
            holder.notificationText.text = datum.name
            holder.notificationInsert.setOnClickListener {
                binding.notificationInput.setText(datum.name)
            }
            holder.notificationRemove.setOnClickListener {
                dataset.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)
            }
            holder.itemView.setOnClickListener {
                val jsonProfile = Gson().toJson(datum)
                val intent = Intent(context, ActivityChat::class.java)
                intent.putExtra(context.getString(R.string.profile), jsonProfile)
                context.startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
            }
        }

        override fun getItemCount() = dataset.size
    }
}