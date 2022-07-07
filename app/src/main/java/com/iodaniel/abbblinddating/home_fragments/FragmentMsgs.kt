package com.iodaniel.abbblinddating.home_fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.chat.ActivityChat
import com.iodaniel.abbblinddating.data_class.ChatDisplayData
import com.iodaniel.abbblinddating.databinding.FragmentMsgsBinding
import com.iodaniel.abbblinddating.liveData.ChatLiveData
import com.iodaniel.abbblinddating.util.ChildEventTemplate
import com.iodaniel.abbblinddating.util.DateTimeFunctions

class FragmentMsgs : Fragment() {
    private lateinit var binding: FragmentMsgsBinding
    private val msgsAdapter = MsgsAdapter()
    private val storyAdapter = StoryAdapter()
    private val chatsDisplayList: java.util.ArrayList<ChatDisplayData> = arrayListOf()
    private val chatsKeyList: ArrayList<String> = arrayListOf()
    private lateinit var chatLiveData: ChatLiveData

    private var refDisplayMe = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance().currentUser!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMsgsBinding.inflate(inflater, container, false)

        binding.msgRvStory.adapter = storyAdapter
        binding.msgRvStory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.msgRv.adapter = msgsAdapter
        msgsAdapter.dataset = chatsDisplayList
        msgsAdapter.activity = requireActivity()
        binding.msgRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refDisplayMe = refDisplayMe.child(getString(R.string.messages_path)).child(auth.uid).child(getString(R.string.display_data_path))
        chatLiveData = ChatLiveData(refDisplayMe)
        chatLiveData.observe(viewLifecycleOwner) {
            val snapshot = it.first
            when (it.second) {
                ChildEventTemplate.onChildAdded -> {
                    val key = snapshot.key!!
                    if (key in chatsKeyList) return@observe
                    val json = Gson().toJson(snapshot.value)
                    val chatDisplayData = Gson().fromJson(json, ChatDisplayData::class.java)!!
                    chatsDisplayList.add(chatDisplayData)
                    chatsKeyList.add(key)
                    msgsAdapter.notifyItemInserted(chatsDisplayList.size)
                    //binding.msgRv.smoothScrollToPosition(chatsDisplayList.size)
                }
                ChildEventTemplate.onChildChanged -> {
                    val key = snapshot.key!!
                    //if (key in chatsKeyList) return@observe
                    val json = Gson().toJson(snapshot.value)
                    val chatDisplayData = Gson().fromJson(json, ChatDisplayData::class.java)!!
                    val pos = chatsKeyList.indexOf(key)
                    chatsDisplayList[pos] = chatDisplayData
                    //chatsKeyList.add(key)
                    msgsAdapter.notifyItemChanged(pos)
                    //binding.msgRv.smoothScrollToPosition(chatsDisplayList.size)
                }
                ChildEventTemplate.onChildRemoved -> {
                    val key = snapshot.key!!
                    if (key !in chatsKeyList) return@observe
                    val pos = chatsKeyList.indexOf(key)
                    chatsDisplayList.removeAt(pos)
                    chatsKeyList.removeAt(pos)
                    msgsAdapter.notifyItemRemoved(pos)
                }
            }
        }
    }
}

class MsgsAdapter : RecyclerView.Adapter<MsgsAdapter.ViewHolder>() {
    var dataset: ArrayList<ChatDisplayData> = arrayListOf()
    private val auth = FirebaseAuth.getInstance().currentUser!!
    private lateinit var context: Context
    lateinit var activity: Activity

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.msg_name)
        val msgMsg: TextView = itemView.findViewById(R.id.msg_msg)
        val image: ImageView = itemView.findViewById(R.id.msg_image)
        //val divider: View = itemView.findViewById(R.id.msg_divider)
        val msgTime: TextView = itemView.findViewById(R.id.msg_time)
        val msgNo: TextView = itemView.findViewById(R.id.msg_msgs_no)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.msgs_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = dataset[position]
        if (datum.myPersonData.auth == auth.uid) {
            Glide.with(context).load(datum.otherPersonData.image).into(holder.image)
            holder.name.text = datum.otherPersonData.name

            holder.itemView.setOnClickListener {
                val jsonProfile = Gson().toJson(datum.otherPersonData)
                val intent = Intent(context, ActivityChat::class.java)
                intent.putExtra(context.getString(R.string.profile), jsonProfile)
                context.startActivity(intent)
                activity.overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
            }
        }
        if (datum.otherPersonData.auth == auth.uid) {
            Glide.with(context).load(datum.myPersonData.image).into(holder.image)
            holder.name.text = datum.myPersonData.name

            holder.itemView.setOnClickListener {
                val jsonProfile = Gson().toJson(datum.myPersonData)
                val intent = Intent(context, ActivityChat::class.java)
                intent.putExtra(context.getString(R.string.profile), jsonProfile)
                context.startActivity(intent)
                activity.overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
            }
        }
        holder.msgMsg.text = datum.mostRecentMessage
        val pair = DateTimeFunctions.getTime(datum.timeSent)
        val timeDisplay = "${pair.first}:${pair.second}"
        holder.msgTime.text = timeDisplay
        holder.msgNo.text = "1"
    }

    override fun getItemCount() = dataset.size
}