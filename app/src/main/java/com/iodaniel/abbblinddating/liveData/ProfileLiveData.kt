package com.iodaniel.abbblinddating.liveData

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.iodaniel.abbblinddating.util.ChildEventTemplate

class ProfileLiveData(private val ref: DatabaseReference): LiveData<Pair<DataSnapshot, Int>>() {

    private val listener = Listener()

    override fun onActive() {
        super.onActive()
        ref.addValueEventListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        ref.removeEventListener(listener)
    }

    inner class Listener : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()){
                value = snapshot to ChildEventTemplate.onDataChange
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }
}