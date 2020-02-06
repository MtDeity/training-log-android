package com.example.traininglog

import android.content.Context
import androidx.fragment.app.*

class TabAdapter(fm: FragmentManager, private val context: Context): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    override fun getItem(position: Int): Fragment {
        return when (position){
            0 -> TrainingFragment()
            1 -> TrainingFragment()
            2 -> TrainingFragment()
            3 -> TrainingFragment()
            else -> TrainingFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position){
            0 -> "トレーニング"
            1 -> "ルーティーン"
            2 -> "計測"
            3 -> "タイマー"
            else -> "マイページ"
        }
    }

    override fun getCount(): Int {
        return 5
    }
}