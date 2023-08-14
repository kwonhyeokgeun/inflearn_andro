package com.mytest.br

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class CustomReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("my log",intent?.action.toString())
        if(intent?.action == "com.example.myapp.CUSTOM_ACTION"){
            var msg = intent?.getStringExtra("msg")
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
}