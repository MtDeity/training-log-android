package com.example.traininglog

import android.app.PendingIntent.getActivity
import android.content.Context
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
import kotlinx.android.synthetic.main.fragment_my_page.*
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btDelete.setOnClickListener {
            val dialogFragment = DeleteDialogFragment()
            dialogFragment.show(fragmentManager!!, "aaa")
        }

    }
}

class DeleteReceiver() : AsyncTask<String, String, String>() {

    private var context: Context? = null

    constructor(context:Context): this() {
        this.context = context
    }

    override fun doInBackground(vararg params: String): String {
        val pref = PreferenceManager.getDefaultSharedPreferences(this.context)
        val token = pref.getString("cached_access_token", "")
        val id = pref.getString("ID", "")


        var result = ""
        var urlConnection: HttpURLConnection? = null

        val urlStr = "https://mukimukinoko5050.herokuapp.com/users/${id}"
        val url = URL(urlStr)

        Log.i("token", token)

        try {
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connectTimeout = 100000
            urlConnection.readTimeout = 100000
            urlConnection.requestMethod = "DELETE"
            urlConnection.addRequestProperty("Content-Type", "application/json; charset=UTF-8")
            urlConnection.addRequestProperty("Authorization", "Bearer $token")
            urlConnection.doOutput = true
            urlConnection.doInput = true
            Log.i("DeleteParams", urlConnection.toString())
            urlConnection.connect()
            Log.i("DeleteConnect", "connect")

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
            val pref = PreferenceManager.getDefaultSharedPreferences(this.context)
            pref.edit { putString("cached_access_token", "") }
//             val intent = Intent(this.context, MainActivity::class.java)
//             startActivity(intent)
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