package com.example.traininglog

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_my_page.*

class MyPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    "トレーニング" -> {
                        val intent = Intent(this@MyPageActivity, TrainingActivity::class.java)
                        startActivity(intent)
                    }
                    "ルーティン" -> {
                        val intent = Intent(this@MyPageActivity, RoutineActivity::class.java)
                        startActivity(intent)
                    }
                    "計測" -> {
                        val intent = Intent(this@MyPageActivity, MeasureActivity::class.java)
                        startActivity(intent)
                    }
                    "タイマー" -> {
                        val intent = Intent(this@MyPageActivity, TimerActivity::class.java)
                        startActivity(intent)
                    }
                    "マイページ" -> {
                        val intent = Intent(this@MyPageActivity, MyPageActivity::class.java)
                        startActivity(intent)
                    }
                    else -> {
                        val intent = Intent(this@MyPageActivity, TrainingActivity::class.java)
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
