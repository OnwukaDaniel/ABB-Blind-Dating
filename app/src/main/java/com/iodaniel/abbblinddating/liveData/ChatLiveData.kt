package com.iodaniel.abbblinddating.liveData

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ChildEventListener
import com.iodaniel.abbblinddating.util.ChildEventTemplate

class ChatLiveData(private val ref: DatabaseReference): LiveData<Pair<DataSnapshot, Int>>() {

    private val listener = Listener()

    override fun onActive() {
        super.onActive()
        ref.addChildEventListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        ref.removeEventListener(listener)
    }

    inner class Listener : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            if (snapshot.exists()){
                value = snapshot to ChildEventTemplate.onChildAdded
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            if (snapshot.exists()){
                value = snapshot to ChildEventTemplate.onChildChanged
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            if (snapshot.exists()){
                value = snapshot to ChildEventTemplate.onChildRemoved
            }
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            if (snapshot.exists()){
                value = snapshot to ChildEventTemplate.onChildMoved
            }
        }

        override fun onCancelled(error: DatabaseError) {

        }
    }
}