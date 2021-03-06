package com.example.traininglog

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    private var sharing: String = "true"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val accountReceiver = AccountReceiver(activity!!, etId, etEmail, swSharing)
        accountReceiver.execute()

        swSharing.isChecked = (sharing == "false")
        swSharing.setOnCheckedChangeListener { buttonView, isChecked ->
            sharing = if (isChecked) "false" else "true"
        }

        btDelete.setOnClickListener {
            val dialogFragment = DeleteDialogFragment()
            dialogFragment.show(fragmentManager!!, "aaa")
        }

        btEdit.setOnClickListener {
            val name = etId.text.toString()
            val email = etEmail.text.toString()

            val editReceiver = EditReceiver(activity!!)
            editReceiver.execute(name, email, sharing)

            val inputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }

    class AccountReceiver() : AsyncTask<String, String, String>() {

        private var context: Context? = null
        private var etId: EditText? = null
        private var etEmail: EditText? = null
        private var swSharing: Switch? = null

        constructor(context: Context, etId: EditText, etEmail: EditText, swSharing: Switch) : this() {
            this.context = context
            this.etId = etId
            this.etEmail = etEmail
            this.swSharing = swSharing
        }

        override fun doInBackground(vararg params: String): String {
            val pref = PreferenceManager.getDefaultSharedPreferences(this.context)
            val token = pref.getString("cached_access_token", "")
            val id = pref.getString("ID", "")

            var result = ""
            var urlConnection: HttpURLConnection? = null

            val urlStr = "https://mukimukiroku.herokuapp.com/users/${id}"
            val url = URL(urlStr)
            Log.i("AccountSuccess", "0")

            try {
                Log.i("AccountSuccess", "1")
                urlConnection = url.openConnection() as HttpURLConnection
                Log.i("AccountSuccess", "2")
                urlConnection.connectTimeout = 100000
                urlConnection.readTimeout = 100000
                urlConnection.requestMethod = "GET"
                urlConnection.addRequestProperty("Content-Type", "application/json; charset=UTF-8")
                urlConnection.addRequestProperty("Authorization", "Bearer $token")
                urlConnection.doOutput = false
                urlConnection.doInput = true
                Log.i("AccountSuccess", "3")
                urlConnection.connect()
                Log.i("AccountSuccess", urlConnection.toString())
                Log.i("AccountSuccess", "a")
//                val outputStream = PrintStream(urlConnection.outputStream)
//                outputStream.close()
                Log.i("AccountSuccess", "b")

                val inputStream = urlConnection.inputStream
                Log.i("AccountSuccess", "c")
                result = is2String(inputStream)
                inputStream.close()
                Log.i("AccountSuccess", result)
            } catch (e: Exception) {
                e.printStackTrace()

                val errorStream = urlConnection?.errorStream
                result = is2String(errorStream)
                errorStream?.close()
                Log.i("AccountError", result)
            } finally {
                urlConnection?.disconnect()
                return result
            }
        }

        override fun onPostExecute(result: String) {
            if (result == "") {
                Log.i("AccountError", "エラー")
            } else {
                Log.i("AccountSuccess", "成功")
                val rootJSON = JSONObject(result)
                val name = rootJSON.getString("name")
                val email = rootJSON.getString("id")
                val sharingBoolean = rootJSON.getBoolean("user_private")

                this.etId?.setText(name)
                this.etEmail?.setText("test@test.com")
                this.swSharing?.isChecked = !sharingBoolean
            }
            return
        }

        private fun is2String(stream: InputStream?): String {
            val sb = StringBuilder()
            val reader = BufferedReader(InputStreamReader(stream!!, "UTF-8"))
            var line = reader.readLine()
            while (line != null) {
                sb.append(line)
                line = reader.readLine()
            }
            reader.close()
            return sb.toString()
        }
    }

    class EditReceiver() : AsyncTask<String, String, String>() {

        private var context: Context? = null

        constructor(context: Context) : this() {
            this.context = context
        }

        override fun doInBackground(vararg params: String): String {
            val pref = PreferenceManager.getDefaultSharedPreferences(this.context)
            val token = pref.getString("cached_access_token", "")
            val id = pref.getString("ID", "")


            var result = ""
            var urlConnection: HttpURLConnection? = null
            val paramsValue = JSONObject()
            val name = params[0]
            val email = params[1]
            val sharing = params[2]
            paramsValue.put("name", name)
            paramsValue.put("email", email)
            paramsValue.put("user_private", sharing)

            val bodyParameter = JSONObject()
            bodyParameter.put("user_update_params", paramsValue)


            val urlStr = "https://mukimukiroku.herokuapp.com/users/${id}"
            val url = URL(urlStr)

            try {
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = 100000
                urlConnection.readTimeout = 100000
                urlConnection.requestMethod = "PUT"
                urlConnection.addRequestProperty("Content-Type", "application/json; charset=UTF-8")
                urlConnection.addRequestProperty("Authorization", "Bearer $token")
                urlConnection.doOutput = true
                urlConnection.doInput = true
                urlConnection.connect()
                val outputStream = PrintStream(urlConnection.outputStream)
                outputStream.print(bodyParameter)
                outputStream.close()

                val inputStream = urlConnection.inputStream
                result = is2String(inputStream)
                inputStream.close()
                Log.i("EditSuccess", result)
            } catch (e: Exception) {
                e.printStackTrace()

                val errorStream = urlConnection?.errorStream
                result = is2String(errorStream)
                errorStream?.close()
                Log.i("EditError", result)
            } finally {
                urlConnection?.disconnect()
                return result
            }
        }

        override fun onPostExecute(result: String) {
            if (result == "") {
                Log.i("EditError", "エラー")
            } else {
                Log.i("EditSuccess", "成功")
                val text = "更新しました。"
                val duration = Toast.LENGTH_LONG
                val toast = Toast.makeText(this.context, text, duration)
                toast.show()
            }
            return
        }

        private fun is2String(stream: InputStream?): String {
            val sb = StringBuilder()
            val reader = BufferedReader(InputStreamReader(stream!!, "UTF-8"))
            var line = reader.readLine()
            while (line != null) {
                sb.append(line)
                line = reader.readLine()
            }
            reader.close()
            return sb.toString()
        }
    }
}

