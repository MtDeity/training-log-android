package com.example.traininglog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hasAccount()
    }

    private fun hasAccount() {
        val pref = PreferenceManager.getDefaultSharedPreferences(application)

//        pref.edit { putString("cached_access_token", "") }

        val accessToken = pref.getString("cached_access_token", "")
        val hasToken = !accessToken.isNullOrEmpty()

        if (hasToken) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}