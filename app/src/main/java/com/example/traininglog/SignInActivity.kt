package com.example.traininglog

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.HttpURLConnection
import java.net.URL


class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
    }

    fun onSignInClick(view: View) {
        val emailStr = etEmail.text.toString()
        val passwordStr = etPassword.text.toString()

        val receiver = SignInReceiver()
        receiver.execute(emailStr, passwordStr)

        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun onLinkClick(view: View) {
        val intent = Intent(applicationContext, SignUpActivity::class.java)
        startActivity(intent)
    }

    private inner class SignInReceiver() : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String): String {
            var result = ""
            var urlConnection: HttpURLConnection? = null

            val paramsValue = JSONObject()
            val email = params[0];
            val password = params[1]
            paramsValue.put("sign_in_text", email)
            paramsValue.put("password", password)
            val bodyParameter = JSONObject()
            bodyParameter.put("sign_in_params", paramsValue)

            val urlStr = "https://mukimukinoko5050.herokuapp.com/sign_in"
            val url = URL(urlStr)

            try {
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = 100000
                urlConnection.readTimeout = 100000
                urlConnection.requestMethod = "POST"
                urlConnection.addRequestProperty("Content-Type", "application/json; charset=UTF-8")
                urlConnection.doOutput = true
                urlConnection.doInput = true
                Log.i("SignIn", "0")
                urlConnection.connect()
                Log.i("SignIn", "1")
                val outputStream = PrintStream(urlConnection.outputStream)
                outputStream.print(bodyParameter)
                outputStream.close()

                val inputStream = urlConnection.inputStream
                result = is2String(inputStream)
                inputStream.close()
                Log.i("SignInSuccess", result)
            } catch (e: Exception) {
                e.printStackTrace()

                val errorStream = urlConnection?.errorStream
                result = is2String(errorStream)
                errorStream?.close()
                Log.i("SignInError", result)
            } finally {
                urlConnection?.disconnect()
                return result
            }
        }

        override fun onPostExecute(result: String) {
            if (result == "") {
                tvErrorMessage.text = "エラー"
            } else {
                val rootJSON = JSONObject(result)
                val token = rootJSON.getString("token")
                val id = rootJSON.getString("id")
                Log.i("SignInToken", token)
                val pref = PreferenceManager.getDefaultSharedPreferences(application)
                pref.edit {
                    putString("cached_access_token", token)
                    putString("ID", id)
                }
                val intent = Intent(applicationContext, HomeActivity::class.java)
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
}

