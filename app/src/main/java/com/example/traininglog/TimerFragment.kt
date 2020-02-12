package com.example.traininglog

import android.content.res.ColorStateList
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_timer.*

class TimerFragment : Fragment() {
    private lateinit var soundPool: SoundPool
    private var soundResId = 0

    inner class MyCountDownTimer(
        millisInFuture: Long,
        countDownInterval: Long
    ) :
        CountDownTimer(millisInFuture, countDownInterval) {

        init {
            val minute = millisInFuture / 1000L / 60L
            val second = millisInFuture / 1000L % 60L
            timerText.text = "%1d:%2$02d".format(minute, second)
        }

        var isRunning = false

        override fun onTick(millisUntilFinished: Long) {
            val minute = millisUntilFinished / 1000L / 60L
            val second = millisUntilFinished / 1000L % 60L
            timerText.text = "%1d:%2$02d".format(minute, second)
        }

        override fun onFinish() {
            timerText.text = "0:00"
            soundPool.play(soundResId, 1.0f, 100f, 0, 0, 1.0f)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var millisInFuture: Long = 3 * 60 * 1000
        var timer = MyCountDownTimer(millisInFuture, 100)

        tenSec.setOnClickListener {
            millisInFuture += 1 * 10 * 1000
            timer = MyCountDownTimer(millisInFuture, 100)
        }

        oneMin.setOnClickListener {
            millisInFuture += 1 * 60 * 1000
            timer = MyCountDownTimer(millisInFuture, 100)
        }

        fiveMin.setOnClickListener {
            millisInFuture += 5 * 60 * 1000
            timer = MyCountDownTimer(millisInFuture, 100)
        }

        playPause.setOnClickListener {
            timer.isRunning = if (timer.isRunning) {
                timer.cancel()
                playPause.setImageResource(R.drawable.play)
                reset.isClickable = true
                reset.imageTintList= ColorStateList.valueOf(getResources().getColor(android.R.color.black))
                false
            } else {
                timer.start()
                playPause.setImageResource(R.drawable.pause)
                reset.isClickable = false
                reset.imageTintList= ColorStateList.valueOf(getResources().getColor(android.R.color.darker_gray))
                true
            }
        }

        reset.setOnClickListener {
            if (!timer.isRunning) {
                millisInFuture = 0 * 60 * 1000
                timer = MyCountDownTimer(millisInFuture, 100)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        soundPool =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                @Suppress("DEPRECATION")
                SoundPool(2, AudioManager.STREAM_ALARM, 0)
            } else {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build()
            }
        soundResId = soundPool.load(context, R.raw.buzz_fade_out, 1)
    }

    override fun onPause() {
        super.onPause()
        soundPool.release()
    }
}