package com.example.traininglog

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.HttpURLConnection
import java.net.URL

class MyPageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }
}

class DeleteReceiver() : AsyncTask<String, String, String>() {
    override fun doInBackground(): String {
        val pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
        val id = pref.getString("ID", "")

        var result = ""
        var urlConnection: HttpURLConnection? = null

        val urlStr = "https://mukimukinoko5050.herokuapp.com/user/${id}"
        val url = URL(urlStr)

        try {
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connectTimeout = 100000
            urlConnection.readTimeout = 100000
            urlConnection.requestMethod = "DELETE"
            urlConnection.addRequestProperty("Content-Type", "application/json; charset=UTF-8")
            urlConnection.doOutput = true
            urlConnection.doInput = true
            urlConnection.connect()

            val inputStream = urlConnection.inputStream
            result = is2String(inputStream)
            inputStream.close()
            Log.i("DeleteSuccess", result)
        } catch (e: Exception) {
            e.printStackTrace()

            val errorStream = urlConnection?.errorStream
            result = is2String(errorStream)
            errorStream?.close()
            Log.i("DeleteError", result)
        } finally {
            urlConnection?.disconnect()
            return result
        }
    }

    override fun onPostExecute(result: String) {
        if (result == "") {
            Log.i("DeleteError", "エラー")
        } else {
            val pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
            pref.edit { putString("cached_access_token", "") }
            val intent = Intent(getActivity(), MainActivity::class.java)
            startActivity(intent)
        }
        return
    }
}

private fun is2String(stream: InputStream?): String {
    val sb = StringBuilder()
    val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
    var line = reader.readLine()
    while (line != null) {
        sb.append(line)
        line = reader.readLine()
    }
    reader.close()
    return sb.toString()
}