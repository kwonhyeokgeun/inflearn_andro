package com.example.stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import java.util.Timer
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    private lateinit var btn_start : Button
    private lateinit var btn_reset : Button
    private lateinit var tv_minute : TextView
    private lateinit var tv_second : TextView
    private lateinit var tv_millsecond : TextView
    private var isRunning : Boolean = false

    private var timer : Timer? = null
    private var time =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_start = findViewById(R.id.btn_start)
        btn_reset = findViewById(R.id.btn_reset)
        tv_minute = findViewById(R.id.tv_minute)
        tv_second = findViewById(R.id.tv_second)
        tv_millsecond = findViewById(R.id.tv_millisecond)


        btn_start.setOnClickListener {
            Log.v("확인","확인")
            if(isRunning){
                pause()
            }else{
                start()
            }
        }

        btn_reset.setOnClickListener {
            reset()
        }
    }

    private fun start(){
        btn_start.text = getString(R.string.btn_pause_en)
        isRunning = true

        timer = timer(period=10){
            time++
            val milsec = time%100
            val sec = (time%6000)/100
            val min = time/6000
            runOnUiThread {
                // UI 조작 로직
                tv_millsecond.text = if(milsec<10) ",0${milsec}" else ",${milsec}"
                tv_second.text = if(sec<10) ":0${sec}" else ":${sec}"
                tv_minute.text = if(min<10) "0${min}" else "${min}"
            }

        }
    }

    private fun reset(){
        if(!isRunning){
            time=0
            tv_millsecond.text = ",00"
            tv_second.text =":00"
            tv_minute.text = "00"
        }
    }

    private fun pause(){
        btn_start.text = getString(R.string.btn_start_en)
        isRunning = false

        timer?.cancel()
    }
}