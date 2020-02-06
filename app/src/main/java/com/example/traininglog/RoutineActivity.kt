package com.example.traininglog

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_routine.*

class RoutineActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routine)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    "トレーニング" -> {
                        val intent = Intent(this@RoutineActivity, TrainingActivity::class.java)
                        startActivity(intent)
                    }
                    "ルーティン" -> {
                        val intent = Intent(this@RoutineActivity, RoutineActivity::class.java)
                        startActivity(intent)
                    }
                    "計測" -> {
                        val intent = Intent(this@RoutineActivity, MeasureActivity::class.java)
                        startActivity(intent)
                    }
                    "タイマー" -> {
                        val intent = Intent(this@RoutineActivity, TimerActivity::class.java)
                        startActivity(intent)
                    }
                    "マイページ" -> {
                        val intent = Intent(this@RoutineActivity, MyPageActivity::class.java)
                        startActivity(intent)
                    }
                    else -> {
                        val intent = Intent(this@RoutineActivity, TrainingActivity::class.java)
                        startActivity(intent)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

//        tabLayout.getTabAt(2)?.select()
    }
}