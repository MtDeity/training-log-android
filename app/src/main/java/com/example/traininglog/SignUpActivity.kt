package com.example.traininglog

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.HttpURLConnection
import java.net.URL

class SignUpActivity : AppCompatActivity() {
    lateinit var sharing: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        swSharing.setOnCheckedChangeListener { buttonView, isChecked ->
            sharing = if (isChecked) "false" else "true"
        }
    }

    fun onSignUpClick(view: View) {
        val emailStr = etEmail.text.toString()
        val passwordStr = etPassword.text.toString()
        val passwordConfirmationStr = etPasswordConfirmation.text.toString()
        val idStr = emailStr.split("@")[0]

        val receiver = SignUpReceiver()
        receiver.execute(idStr, emailStr, passwordStr, passwordConfirmationStr, sharing)

        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private inner class SignUpReceiver() : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String): String {
            var result = ""
            var urlConnection: HttpURLConnection? = null

            val paramsValue = JSONObject()
            val id = params[0]
            val email = params[1]
            val password = params[2]
            val passwordConfirmation = params[3]
            val sharing = params[4]

            paramsValue.put("name", id)
            paramsValue.put("email", email)
            paramsValue.put("password", password)
            paramsValue.put("password_confirmation", passwordConfirmation)
            paramsValue.put("user_private", sharing)

            val bodyParameter = JSONObject()
            bodyParameter.put("sign_up_params", paramsValue)

            val urlStr = "https://mukimukinoko5050.herokuapp.com/sign_up"
            val url = URL(urlStr)

            try {
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = 10000
                urlConnection.readTimeout = 10000
                urlConnection.requestMethod = "POST"
                urlConnection.addRequestProperty("Content-Type", "application/json; charset=UTF-8")
                urlConnection.doOutput = true
                urlConnection.doInput = true
                urlConnection.connect()
                val outputStream = PrintStream(urlConnection.outputStream)
                outputStream.print(bodyParameter)
                outputStream.close()

                val inputStream = urlConnection.inputStream
                result = is2String(inputStream)
                inputStream.close()
                Log.i("SignUpSuccess", result)
            } catch (e: Exception) {
                e.printStackTrace()

                val errorStream = urlConnection?.errorStream
                result = is2String(errorStream)
                errorStream?.close()
                Log.i("SignUpError", result)
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
                Log.i("SignUpToken", token)
                val pref = PreferenceManager.getDefaultSharedPreferences(application)
                pref.edit {
                    putString("cached_access_token", "Bearer $token")
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
