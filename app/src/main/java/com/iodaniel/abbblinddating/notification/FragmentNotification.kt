package com.iodaniel.abbblinddating.notification

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.chat.ActivityChat
import com.iodaniel.abbblinddating.data_class.NotificationData
import com.iodaniel.abbblinddating.databinding.FragmentNotificationBinding
import com.iodaniel.abbblinddating.liveData.ProfileLiveData
import com.iodaniel.abbblinddating.util.ChildEventTemplate
import com.iodaniel.abbblinddating.util.DateTimeFunctions.getDay
import com.iodaniel.abbblinddating.util.DateTimeFunctions.getTime

class FragmentNotification : Fragment() {
    private lateinit var binding: FragmentNotificationBinding
    private val notificationAdapter = NotificationAdapter()
    private val notificationsDisplayList: ArrayList<NotificationData> = arrayListOf()
    //private val notificationsKeyList: ArrayList<String> = arrayListOf()

    private var ref = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance().currentUser!!
    private lateinit var profileLiveData: ProfileLiveData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        binding.notificationBack.setOnClickListener { requireActivity().onBackPressed() }
        notificationAdapter.dataset = notificationsDisplayList
        notificationAdapter.activity = requireActivity()
        binding.notificationMsgRv.adapter = notificationAdapter
        binding.notificationMsgRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ref = ref.child(getString(R.string.notification_path)).child(auth.uid)
        profileLiveData = ProfileLiveData(ref)
        profileLiveData.observe(viewLifecycleOwner) {
            val snapshot = it.first.children.reversed()
            notificationsDisplayList.clear()
            when (it.second) {
                ChildEventTemplate.onDataChange -> {
                    for (i in snapshot) {
                        val notificationData = i.getValue(NotificationData::class.java)!!
                        notificationsDisplayList.add(notificationData)
                    }
                    notificationAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    var dataset: ArrayList<NotificationData> = arrayListOf()
    private lateinit var context: Context
    lateinit var activity: Activity

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notificationMsg: TextView = itemView.findViewById(R.id.notification_msg)

        //val notificationIcon: ImageView = itemView.findViewById(R.id.notification_icon)
        //val notificationMore: ImageView = itemView.findViewById(R.id.notification_more)
        val notificationTime: TextView = itemView.findViewById(R.id.notification_time)
        val notificationCard: CardView = itemView.findViewById(R.id.notification_card)
        val header: TextView = itemView.findViewById(R.id.notification_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.notification_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = dataset[position]
        holder.notificationMsg.text = datum.notificationMessage
        val pair = getTime(datum.time)
        val time = "${pair.first}:${pair.second}"
        holder.notificationTime.text = time
        displayHeader(holder, dataset, datum)

        holder.notificationCard.setOnClickListener {
            val jsonProfile = Gson().toJson(datum.myPersonData)
            val intent = Intent(context, ActivityChat::class.java)
            intent.putExtra(context.getString(R.string.profile), jsonProfile)
            context.startActivity(intent)
            activity.overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
        }
    }

    private fun displayHeader(holder: ViewHolder, dataset: ArrayList<NotificationData>, datum: NotificationData) {
        if (!hasPrevious(dataset, datum)) {
            val presentDay = getDay(datum.time)
            holder.header.visibility = View.VISIBLE
            holder.header.text = presentDay
            return
        }
        val presentDay = getDay(datum.time)
        val previousDay = getDay(dataset[dataset.indexOf(datum) - 1].time)
        if (previousDay == presentDay) {
            holder.header.visibility = View.GONE
            return
        } else {
            holder.header.visibility = View.VISIBLE
            holder.header.text = presentDay
            return
        }
    }

    private fun hasPrevious(dataset: ArrayList<NotificationData>, datum: NotificationData) = dataset.indexOf(datum) != 0

    override fun getItemCount() = dataset.size
}