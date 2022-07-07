package com.iodaniel.abbblinddating.home_fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.FragmentOtherProfileBinding
import com.iodaniel.abbblinddating.liveData.ProfileLiveData
import com.iodaniel.abbblinddating.util.ChildEventTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ActivityOtherProfile : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { FragmentOtherProfileBinding.inflate(layoutInflater) }
    private var recipientData = PersonData()
    private val othersAdapter = OthersAdapter()
    private val othersInterestAdapter = OthersInterestAdapter()
    private var auth = FirebaseAuth.getInstance().currentUser
    private var ref = FirebaseDatabase.getInstance().reference
    private var refLikesData = FirebaseDatabase.getInstance().reference
    private var similarDataset: ArrayList<PersonData> = arrayListOf()
    private var similarKey: ArrayList<String> = arrayListOf()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var refMyProfile = FirebaseDatabase.getInstance().reference
    private var personData = PersonData()

    private var refSimilarProfile = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getProfileData()
        refMyProfile = refMyProfile.child(getString(R.string.profile)).child(auth!!.uid)
        val profileLiveData = ProfileLiveData(refMyProfile)
        profileLiveData.observe(this){
            when(it.second){
                ChildEventTemplate.onDataChange->{
                    personData = it.first.getValue(PersonData::class.java)!!
                }
            }
        }
        refSimilarProfile = refSimilarProfile.child(getString(R.string.profile))
        val similarLiveData = ProfileLiveData(refSimilarProfile)
        similarLiveData.observe(this){
            when (it.second) {
                ChildEventTemplate.onDataChange -> {
                    val d: ArrayList<PersonData> = arrayListOf()
                    for ((index, i) in it.first.children.withIndex()) {
                        val personData = i.getValue(PersonData::class.java)!!
                        if (personData.emailPasswordPair["email"].toString().trim() == auth!!.email.toString().trim() ||
                            recipientData.emailPasswordPair["email"].toString().trim() == personData.emailPasswordPair["email"].toString().trim()) {
                            continue
                        }
                        d.add(personData)
                    }
                    for((index, data) in d.withIndex()){
                        if (similarKey.contains(data.auth)) continue
                        if (similarDataset.size > 20) break
                        for (i in data.interests){
                            if (i in personData.interests){
                                similarDataset.add(data)
                                similarKey.add(data.auth)
                                othersAdapter.notifyItemInserted(index)
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getProfileData() {
        if (intent.hasExtra(getString(R.string.profile))) {
            val profileJson = intent.getStringExtra(getString(R.string.profile))
            recipientData = Gson().fromJson(profileJson, PersonData::class.java)
            binding.otherProfileAbout.text = recipientData.about
            binding.otherProfileCancel.setOnClickListener(this)
            binding.otherProfileFavorite.setOnClickListener(this)
            binding.otherProfileChat.setOnClickListener(this)
            binding.otherProfileName.text = recipientData.name
            binding.otherProfileLocation.text = recipientData.location
            binding.otherProfileLikes.text = recipientData.likes.toString()
            Glide.with(this).load(recipientData.image).centerCrop().into(binding.otherProfileImage)
            othersInterestAdapter.dataset = recipientData.interests
            binding.othersInterestRv.adapter = othersInterestAdapter
            binding.othersInterestRv.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)

            othersAdapter.dataset = similarDataset
            othersAdapter.activity = this
            binding.similarProfileRv.adapter = othersAdapter
            binding.similarProfileRv.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.other_profile_cancel -> onBackPressed()
            R.id.other_profile_favorite -> {
                if (auth!!.uid in recipientData.likesData) return
                ref = ref.child(getString(R.string.profile)).child(recipientData.auth).child(getString(R.string.likes_path))
                refLikesData = refLikesData.child(getString(R.string.profile)).child(recipientData.auth).child(getString(R.string.likes_data))
                val likes = recipientData.likes + 1
                val likesDataList = recipientData.likesData
                likesDataList.add(auth!!.uid)
                binding.otherProfileLikes.text = likes.toString()
                ref.setValue(likes).addOnSuccessListener { }.addOnFailureListener { }
                refLikesData.setValue(likesDataList).addOnSuccessListener { }.addOnFailureListener { }
            }
            R.id.other_profile_chat -> {
                val json = Gson().toJson(recipientData)
                val intent = Intent(this, com.iodaniel.abbblinddating.chat.ActivityChat::class.java)
                intent.putExtra(getString(R.string.profile), json)
                startActivity(intent)
                overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
            }
        }
    }
}

class OthersInterestAdapter : RecyclerView.Adapter<OthersInterestAdapter.ViewHolder>() {
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

class OthersAdapter : RecyclerView.Adapter<OthersAdapter.ViewHolder>() {
    var dataset: ArrayList<PersonData> = arrayListOf()
    private lateinit var context: Context
    lateinit var activity: Activity

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.similar_name)
        val image: ImageView = itemView.findViewById(R.id.similar_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.similar_people_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = dataset[position]
        holder.name.text = datum.name
        Glide.with(context).load(datum.image).into(holder.image)
        holder.itemView.setOnClickListener {
            val json = Gson().toJson(datum)
            val intent = Intent(context, ActivityOtherProfile::class.java)
            intent.putExtra(context.getString(R.string.profile), json)
            context.startActivity(intent)
            activity.overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
        }
    }

    override fun getItemCount() = dataset.size
}