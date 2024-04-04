package com.example.diplomaapplication.recycler_view.view_holders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomaapplication.R

class MessagesRecyclerViewHolderReceiver(v: View):RecyclerView.ViewHolder(v) {
    val receiverText: TextView = v.findViewById(R.id.messageReceiverText)
}