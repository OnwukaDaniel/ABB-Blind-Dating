package com.iodaniel.abbblinddating.games.truth_or_dare

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.databinding.FragmentTodInGameBinding
import com.iodaniel.abbblinddating.dialogs.DialogQuitGame
import com.iodaniel.abbblinddating.viewModel.GeneralGameViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class FragmentTODInGame : Fragment() {
    private lateinit var binding: FragmentTodInGameBinding
    private var recipientData = PersonData()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val tODBubbleAdapter = TODBubbleAdapter()
    private var dataset: ArrayList<Map<String, String>> = arrayListOf()
    private val generalGameViewModel: GeneralGameViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTodInGameBinding.inflate(inflater, container, false)
        generalGameViewModel.personData.observe(viewLifecycleOwner) { recipientData = it }
        binding.totBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().addToBackStack("quit")
                .replace(R.id.tod_root, DialogQuitGame())
                .commit() }
        binding.totQuit.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().addToBackStack("quit")
                .replace(R.id.tod_root, DialogQuitGame())
                .commit()
        }
        tODBubbleAdapter.dataset = dataset
        binding.totRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.totRv.adapter = tODBubbleAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.todSend.setOnClickListener {
            val msg = binding.todMsg.text.trim().toString()
            if (msg == "") return@setOnClickListener
            dataset.add(mapOf("msg" to msg, "type" to SENT.toString()))
            tODBubbleAdapter.notifyItemInserted(dataset.size)
        }
    }

    companion object {
        const val RECEIVED = 0
        const val SENT = 1
    }

    inner class TODBubbleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var dataset: ArrayList<Map<String, String>> = arrayListOf()
        private var chooseLayout: ArrayList<LinearLayout> = arrayListOf()
        private lateinit var context: Context
        var recipientData = PersonData()

        inner class ViewHolderMe(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val truth: CardView = itemView.findViewById(R.id.tot_right_truth)
            val dare: CardView = itemView.findViewById(R.id.tot_right_dare)
            val msg: TextView = itemView.findViewById(R.id.tot_right_msg)
            val camera: ImageView = itemView.findViewById(R.id.tot_right_camera)
            val choose: LinearLayout = itemView.findViewById(R.id.tot_right_choose)
        }

        inner class ViewHolderReceived(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val truth: CardView = itemView.findViewById(R.id.tot_left_truth)
            val dare: CardView = itemView.findViewById(R.id.tot_left_dare)
            val msg: TextView = itemView.findViewById(R.id.tot_left_msg)
            val camera: ImageView = itemView.findViewById(R.id.tot_left_camera)
            val choose: LinearLayout = itemView.findViewById(R.id.tot_left_choose)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            context = parent.context
            return when (viewType) {
                RECEIVED -> {
                    val viewReceived = LayoutInflater.from(context).inflate(R.layout.tod_bubble_left_row, parent, false)
                    ViewHolderReceived(viewReceived)
                }
                else -> {
                    val viewMe = LayoutInflater.from(context).inflate(R.layout.tod_bubble_right_row, parent, false)
                    ViewHolderMe(viewMe)
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val datum = dataset[position]
            when (getItemViewType(position)) {
                RECEIVED -> {
                    (holder as ViewHolderReceived).msg.text = datum["msg"]
                    chooseLayout.add(holder.choose)
                }
                SENT -> {
                    (holder as ViewHolderMe).msg.text = datum["msg"]
                    chooseLayout.add(holder.choose)
                }
            }
            onCLicks(holder, getItemViewType(position))
        }

        private fun onCLicks(holder: RecyclerView.ViewHolder, itemViewType: Int) {
            val pos = holder.adapterPosition
            when (itemViewType) {
                RECEIVED -> {
                    (holder as ViewHolderReceived).dare.setOnClickListener {
                        binding.todMsg.setText(getString(R.string.i_dare_you_to))
                        (holder as ViewHolderReceived).camera.visibility = View.VISIBLE
                        if (pos != 0) chooseLayout[pos - 1].visibility = View.GONE
                    }
                    (holder as ViewHolderReceived).truth.setOnClickListener {
                        (holder as ViewHolderReceived).camera.visibility = View.GONE
                        if (pos != 0) chooseLayout[pos - 1].visibility = View.GONE
                    }
                }
                SENT -> {
                    (holder as ViewHolderMe).dare.setOnClickListener {
                        binding.todMsg.setText(getString(R.string.i_dare_you_to))
                        (holder as ViewHolderMe).camera.visibility = View.VISIBLE
                        if (pos != 0) chooseLayout[pos - 1].visibility = View.GONE
                    }
                    holder.truth.setOnClickListener {
                        (holder as ViewHolderMe).camera.visibility = View.GONE
                        if (pos != 0) chooseLayout[pos - 1].visibility = View.GONE
                    }
                }
            }
        }

        override fun getItemCount() = dataset.size

        override fun getItemViewType(position: Int) = if (dataset[position]["type"]!!.toInt() == RECEIVED) RECEIVED else SENT

    }
}