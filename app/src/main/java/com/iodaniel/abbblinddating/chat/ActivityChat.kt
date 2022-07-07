package com.iodaniel.abbblinddating.chat

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.data_class.ChatData
import com.iodaniel.abbblinddating.data_class.ChatDisplayData
import com.iodaniel.abbblinddating.data_class.NotificationData
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.ActivityChatBinding
import com.iodaniel.abbblinddating.games.cards.ActivityCards
import com.iodaniel.abbblinddating.games.chess.ActivityChess
import com.iodaniel.abbblinddating.games.ludo.ActivityLudo
import com.iodaniel.abbblinddating.games.tic_tac_toe.ActivityTicTacToe
import com.iodaniel.abbblinddating.games.truth_or_dare.ActivityTruthOrDare
import com.iodaniel.abbblinddating.liveData.ChatLiveData
import com.iodaniel.abbblinddating.util.ChildEventTemplate.onChildAdded
import com.iodaniel.abbblinddating.util.ChildEventTemplate.onChildChanged
import com.iodaniel.abbblinddating.util.ChildEventTemplate.onChildRemoved
import com.iodaniel.abbblinddating.util.DateTimeFunctions
import com.iodaniel.abbblinddating.viewModel.CallsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class ActivityChat : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private val callsViewMode: CallsViewModel by viewModels()
    private var recipientData = PersonData()
    private var pref: SharedPreferences? = null
    private val chatBubbleAdapter = ChatBubbleAdapter()
    private val chatsDataList: ArrayList<ChatData> = arrayListOf()
    private val chatsKeyList: ArrayList<String> = arrayListOf()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var refMe = FirebaseDatabase.getInstance().reference
    private var refOther = FirebaseDatabase.getInstance().reference
    private var refNetworkAuth = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance().currentUser!!
    private lateinit var chatLiveData: ChatLiveData

    private var refNotification = FirebaseDatabase.getInstance().reference
    private var refDisplayMe = FirebaseDatabase.getInstance().reference
    private var refDisplayOther = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getProfileData()
        pref = getSharedPreferences(getString(R.string.ALL_PREF), Context.MODE_PRIVATE)
        binding.gameCard.setOnClickListener(this)
        binding.gameTtt.setOnClickListener(this)
        binding.gameTot.setOnClickListener(this)
        binding.chatGameShareGift.setOnClickListener(this)
        binding.gameLudo.setOnClickListener(this)
        binding.gameChess.setOnClickListener(this)
        binding.chatBack.setOnClickListener(this)
        binding.chatVideo.setOnClickListener(this)
        binding.chatVoice.setOnClickListener(this)
        binding.chatActionSend.setOnClickListener(this)
        binding.chatActionGame.setOnClickListener(this)
        binding.chatActionGift.setOnClickListener(this)
        binding.chatsRv.layoutManager = LinearLayoutManager(this@ActivityChat, LinearLayoutManager.VERTICAL, false)
        binding.chatsRv.adapter = chatBubbleAdapter
        chatBubbleAdapter.dataset = chatsDataList
        chatBubbleAdapter.recipientData = recipientData
    }

    private fun getProfileData() {
        if (intent.hasExtra(getString(R.string.profile))) {
            val profileJson = intent.getStringExtra(getString(R.string.profile))
            recipientData = Gson().fromJson(profileJson, PersonData::class.java)
            val selectedChatName = recipientData.name
            binding.chatName.text = selectedChatName
            initializeChat()
        }
    }

    private fun initializeChat() {
        refNotification = refNotification.child(getString(R.string.notification_path)).child(recipientData.auth)
        refDisplayMe = refDisplayMe.child(getString(R.string.messages)).child(auth.uid).child(getString(R.string.display_data))
        refDisplayOther = refDisplayOther.child(getString(R.string.messages)).child(recipientData.auth).child(getString(R.string.display_data))

        refMe = refMe.child(getString(R.string.messages)).child(getString(R.string.chats)).child(auth.uid).child(recipientData.auth)
        refOther = refOther.child(getString(R.string.messages)).child(getString(R.string.chats)).child(recipientData.auth).child(auth.uid)
        refNetworkAuth = refNetworkAuth.child(auth.uid)
        chatLiveData = ChatLiveData(refMe)
        chatLiveData.observe(this) {
            val snapshot = it.first
            when (it.second) {
                onChildAdded -> {
                    val key = snapshot.key!!
                    if (key in chatsKeyList) return@observe
                    val json = Gson().toJson(snapshot.value)
                    val chatData = Gson().fromJson(json, ChatData::class.java)!!
                    chatData.chatTimeKey = key
                    chatsDataList.add(chatData)
                    chatsKeyList.add(key)
                    chatBubbleAdapter.notifyItemInserted(chatsDataList.size)
                    binding.chatsRv.smoothScrollToPosition(chatsDataList.size)
                }
                onChildChanged -> {
                    val key = snapshot.key!!
                    if (key in chatsKeyList) return@observe
                    val json = Gson().toJson(snapshot.value)
                    val chatData = Gson().fromJson(json, ChatData::class.java)!!
                    chatsDataList.add(chatData)
                    chatsKeyList.add(key)
                    chatBubbleAdapter.notifyItemInserted(chatsDataList.size)
                    binding.chatsRv.smoothScrollToPosition(chatsDataList.size)
                }
                onChildRemoved -> {
                    val key = snapshot.key!!
                    if (key !in chatsKeyList) return@observe
                    val pos = chatsKeyList.indexOf(key)
                    chatsDataList.removeAt(pos)
                    chatsKeyList.removeAt(pos)
                    chatBubbleAdapter.notifyItemRemoved(pos)
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
    }

    private fun closeAllDialog(){
        binding.chatGiftDialog.visibility = View.GONE
        binding.chatGamesDialog.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.chat_back -> onBackPressed()
            R.id.chat_voice -> {
                callsViewMode.setPersonData(recipientData)
                supportFragmentManager.beginTransaction().addToBackStack("voice")
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                        R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                    .replace(R.id.chat_root, com.iodaniel.abbblinddating.call.FragmentVoice())
                    .commit()
            }
            R.id.chat_video -> {
                callsViewMode.setPersonData(recipientData)
                supportFragmentManager.beginTransaction().addToBackStack("voice")
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                        R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                    .replace(R.id.chat_root, com.iodaniel.abbblinddating.call.FragmentVideo())
                    .commit()
            }
            R.id.chat_action_send -> {
                //var messageSent = false
                val json = pref!!.getString(getString(R.string.user_profile), "")
                if (json == "") {
                    Snackbar.make(binding.root, getString(R.string.not_registered), Snackbar.LENGTH_LONG).show()
                    return
                }
                val myPersonData = Gson().fromJson(json, PersonData::class.java)!!
                val msg = binding.chatMsg.text.trim().toString()
                if (msg == "") return
                val timeSent = Calendar.getInstance().timeInMillis.toString()
                val newMessage = ChatData(
                    message = msg,
                    timeSent = timeSent,
                    senderName = myPersonData.name,
                    receiverName = recipientData.name,
                    senderAuth = auth.uid,
                    receiverAuth = recipientData.auth,
                    chatTimeKey = timeSent
                )
                val mostRecentData = ChatDisplayData(
                    mostRecentMessage = msg,
                    timeSent = timeSent,
                    myPersonData = myPersonData,
                    otherPersonData = recipientData,
                    senderAuth = auth.uid,
                    receiverAuth = recipientData.auth,
                )
                val msgFragment = getString(R.string.sent_you_a_message)
                val notificationData = NotificationData(
                    notificationMessage = "${myPersonData.name} $msgFragment \n$msg",
                    noMessages = 1,
                    time = timeSent,
                    myPersonData = myPersonData,
                    otherPersonData = recipientData
                )
                refNetworkAuth.get().addOnSuccessListener {
                    val time = Calendar.getInstance().timeInMillis.toString()
                    //messageSent = true
                    newMessage.chatTimeKey = time
                    mostRecentData.timeSent = time
                    chatsKeyList.add(time)
                    refMe.child(newMessage.chatTimeKey).setValue(newMessage).addOnSuccessListener {
                        newMessage.senderName = recipientData.name
                        newMessage.receiverName = myPersonData.name
                        refOther.child(newMessage.chatTimeKey).setValue(newMessage).addOnSuccessListener {
                            notificationData.time = time
                            refNotification.child(time).setValue(notificationData)
                            refDisplayMe.child(recipientData.auth).setValue(mostRecentData)
                            mostRecentData.myPersonData = recipientData
                            mostRecentData.otherPersonData = myPersonData
                            refDisplayOther.child(auth.uid).setValue(mostRecentData)
                        }.addOnFailureListener {

                        }
                    }.addOnFailureListener {

                    }
                }
                scope.launch {
                    delay(10_000)
                    //if (!messageSent)
                }
                chatsDataList.add(newMessage)
                chatBubbleAdapter.notifyItemInserted(chatsDataList.size)
                binding.chatsRv.smoothScrollToPosition(chatsDataList.size)
                binding.chatMsg.setText("")
            }
            R.id.chat_action_gift -> {
                binding.chatGiftDialog.visibility = if (binding.chatGiftDialog.visibility == View.GONE) View.VISIBLE else View.GONE
            }
            R.id.chat_action_game -> {
                closeAllDialog()
                binding.chatGamesDialog.visibility = if (binding.chatGamesDialog.visibility == View.GONE) View.VISIBLE else View.GONE
            }
            R.id.game_ttt -> {
                val json = Gson().toJson(recipientData)
                val intent = Intent(this, ActivityTicTacToe::class.java)
                intent.putExtra(getString(R.string.profile), json)
                startActivity(intent)
                overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
            }
            R.id.game_tot -> {
                val json = Gson().toJson(recipientData)
                val intent = Intent(this, ActivityTruthOrDare::class.java)
                intent.putExtra(getString(R.string.profile), json)
                startActivity(intent)
                overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
            }
            R.id.game_ludo -> {
                val json = Gson().toJson(recipientData)
                val intent = Intent(this, ActivityLudo::class.java)
                intent.putExtra(getString(R.string.profile), json)
                startActivity(intent)
                overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
            }
            R.id.game_card -> {
                val json = Gson().toJson(recipientData)
                val intent = Intent(this, ActivityCards::class.java)
                intent.putExtra(getString(R.string.profile), json)
                startActivity(intent)
                overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
            }
            R.id.game_chess -> {
                val json = Gson().toJson(recipientData)
                val intent = Intent(this, ActivityChess::class.java)
                intent.putExtra(getString(R.string.profile), json)
                startActivity(intent)
                overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
            }
            R.id.chat_game_share_gift -> {
                binding.chatGamesDialog.visibility = View.GONE
                binding.chatGiftDialog.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        if (binding.chatGiftDialog.visibility == View.VISIBLE || binding.chatGamesDialog.visibility == View.VISIBLE) {
            binding.chatGiftDialog.visibility = View.GONE
            binding.chatGamesDialog.visibility = View.GONE
        } else super.onBackPressed()
    }
}

class ChatBubbleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var dataset: ArrayList<ChatData> = arrayListOf()
    private lateinit var context: Context
    private val auth = FirebaseAuth.getInstance().currentUser!!
    var recipientData = PersonData()

    class ViewHolderMe(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val msgMsgMe: TextView = itemView.findViewById(R.id.chat_bubble_r_msg)
        val msgTimeMe: TextView = itemView.findViewById(R.id.chat_bubble_r_time)
    }

    class ViewHolderReceived(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val msgMsgReceived: TextView = itemView.findViewById(R.id.chat_bubble_l_msg)
        val imageReceived: ImageView = itemView.findViewById(R.id.chat_bubble_l_image)
        val msgTimeReceived: TextView = itemView.findViewById(R.id.chat_bubble_l_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            RECEIVED -> {
                val viewReceived = LayoutInflater.from(context).inflate(R.layout.chat_bubble_left_row, parent, false)
                ViewHolderReceived(viewReceived)
            }
            else -> {
                val viewMe = LayoutInflater.from(context).inflate(R.layout.chat_bubble_right_row, parent, false)
                ViewHolderMe(viewMe)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val datum = dataset[position]
        val pair = DateTimeFunctions.getTime(datum.timeSent)
        val time = "${pair.first}:${pair.second}"
        when (getItemViewType(position)) {
            RECEIVED -> {
                (holder as ViewHolderReceived).msgMsgReceived.text = datum.message
                holder.msgTimeReceived.text = time
                Glide.with(context).load(recipientData.image).centerCrop().into(holder.imageReceived)
            }
            SENT -> {
                (holder as ViewHolderMe).msgMsgMe.text = datum.message
                holder.msgTimeMe.text = time
            }
        }
    }

    override fun getItemCount() = dataset.size

    override fun getItemViewType(position: Int) = if (dataset[position].senderAuth == auth.uid) SENT else RECEIVED

    companion object {
        const val RECEIVED = 0
        const val SENT = 1
    }
}