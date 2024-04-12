package com.example.diplomaapplication.ui.map.view.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.diplomaapplication.R

@SuppressLint("Recycle")
class PermissionDeniedView(context: Context, attributeSet: AttributeSet)
    : CardView(context, attributeSet) {

    private var layoutInflater = context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE)
            as LayoutInflater

    private var messageTextView: TextView
    var message: String
        get() = messageTextView.text.toString().trim()
        set(value) { messageTextView.text = value }

    private var settingsButton: Button

    init {
        layoutInflater.inflate(R.layout.permission_denied_layout, this, true)
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.PermissionDeniedView)

        messageTextView = rootView.findViewById(R.id.message_textView)
        message = typedArray.getString(R.styleable.PermissionDeniedView_Message) ?: ""

        settingsButton = rootView.findViewById(R.id.go_to_app_settings_button)
        settingsButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also{
                it.data = Uri.fromParts("package", context.packageName, null)
            }

            context.startActivity(intent)
        }
    }
}