package com.mytest.br

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log

class MyService : Service() {
    private lateinit var chargeReceiver : BroadcastReceiver

    override fun onCreate() {
        chargeReceiver = ChargeReceiver()
        Log.d("my log","create")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        Log.d("my log","등록!!")
        // 브로드캐스트 리시버 등록
        registerReceiver(chargeReceiver, filter)

        return super.onStartCommand(intent, flags, startId)

    }

    override fun onDestroy() {
        unregisterReceiver(chargeReceiver)
        Log.d("my log","서비스끝!!")
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}