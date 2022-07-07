package com.iodaniel.abbblinddating.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.iodaniel.abbblinddating.R
import com.iodaniel.abbblinddating.data_class.PersonData
import com.iodaniel.abbblinddating.home_fragments.ActivityOtherProfile


class StackAdapter : RecyclerView.Adapter<StackAdapter.StoryAdapterViewHolder>() {

    var dataset: ArrayList<PersonData> = arrayListOf()
    private lateinit var context: Context
    lateinit var activity: Activity

    class StoryAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.stack_image)
        val name: TextView = itemView.findViewById(R.id.stack_name)
        val location: TextView = itemView.findViewById(R.id.stack_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapterViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.discover_row, parent, false)
        return StoryAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryAdapterViewHolder, position: Int) {
        val datum = dataset[position]
        holder.name.text = datum.name
        holder.location.text = datum.location
        Glide.with(context).load(Uri.parse(datum.image)).centerCrop().into(holder.image)
        holder.itemView.setOnClickListener {
            val json = Gson().toJson(datum)
            val intent = Intent(context, ActivityOtherProfile::class.java)
            intent.putExtra("profile", json)
            context.startActivity(intent)
            activity.overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
        }
    }

    override fun getItemCount() = dataset.size
}