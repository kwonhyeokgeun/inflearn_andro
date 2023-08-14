package com.mytest.br

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.UiThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChargeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("my log",intent?.action.toString())
        when(intent?.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                // 충전 시 실행할 코드
                Log.d("my log", "충전 중")
                val intent2 = Intent(context, MainActivity::class.java)
                intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                CoroutineScope(Dispatchers.Main).launch {
                    context.startActivity(intent2)
                }
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                // 충전 해제 시 실행할 코드
                Log.d("my log", "충전 해제")
                val intent2 = Intent(context, MainActivity::class.java)
                intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                CoroutineScope(Dispatchers.Main).launch {
                    context.startActivity(intent2)
                }

            }
        }
    }
}