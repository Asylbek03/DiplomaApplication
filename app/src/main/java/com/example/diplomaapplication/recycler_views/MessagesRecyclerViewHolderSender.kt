package com.example.diplomaapplication.recycler_view.view_holders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomaapplication.R

class MessagesRecyclerViewHolderSender(v:View):RecyclerView.ViewHolder(v) {
    val sendText: TextView = v.findViewById(R.id.messageSenderText)
}