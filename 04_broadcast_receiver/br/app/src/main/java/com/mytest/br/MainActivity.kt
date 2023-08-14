package com.mytest.br

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var btn:Button
    private lateinit var customReceiver : BroadcastReceiver
    private var customAction = "com.example.myapp.CUSTOM_ACTION"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        customReceiver = CustomReceiver()

        btn = findViewById(R.id.btn)
        btn.setOnClickListener {
            //커스텀 리시버 발동
            val customIntent = Intent(customAction)
            customIntent.putExtra("msg","메시지~")
            sendBroadcast(customIntent)
        }

        //서비스시작
        val intent = Intent(this, MyService::class.java)
        startService(intent)
    }

    override fun onResume() {
        //커스텀 필터 구독
        val intentFilter = IntentFilter()
        intentFilter.addAction(customAction)
        registerReceiver(customReceiver, intentFilter)

        super.onResume()
    }

    override fun onPause() {
        //커스텀 필터 구독취소
        unregisterReceiver(customReceiver)
        super.onPause()
    }
}