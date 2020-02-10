package com.example.traininglog

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer, container, false)

        timerText.text = "3:00"
        val timer = MyCountDownTimer(3 * 60 * 1000, 100)
        playPause.setOnClickListener {
            timer.isRunning = when
                (timer.isRunning) {
                true -> {
                    timer.cancel()
                    playPause.setImageResource(
                        R.drawable.play
                    )
                    false
                }
                false -> {
                    timer.start()
                    playPause.setImageResource(
                        R.drawable.pause
                    )
                    true
                }
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