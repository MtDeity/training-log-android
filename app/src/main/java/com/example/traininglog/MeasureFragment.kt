package com.example.traininglog

import android.app.DatePickerDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_measure.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class MeasureFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_measure, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/[]M/[]d"))
        etDate.setText(date)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        etDate.setOnClickListener {
            val inputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)


            val dialogFragment = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { view, year, month, day ->
                etDate.setText(String.format(Locale.US, "%d/%d/%d", year, month+1, day))
            }, year, month, day)
            dialogFragment.show()
        }

        btSend.setOnClickListener {
            val weightStr = etWeight.text.toString()
            val dateStr = etDate.text.toString()

            val measureReceiver = MeasureReceiver(activity!!)
            measureReceiver.execute(weightStr, dateStr)

            val inputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }

    class MeasureReceiver() : AsyncTask<String, String, String>() {

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
            val weightStr = params[0]
            val dateStr = params[1]
            paramsValue.put("body_weight", weightStr)
            paramsValue.put("date", dateStr)

            val bodyParameter = JSONObject()
            bodyParameter.put("measure_params", paramsValue)


            val urlStr = "https://mukimukiroku.herokuapp.com/measures"
            val url = URL(urlStr)

            try {
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = 100000
                urlConnection.readTimeout = 100000
                urlConnection.requestMethod = "POST"
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
                Log.i("MeasureSuccess", result)
            } catch (e: Exception) {
                e.printStackTrace()

                val errorStream = urlConnection?.errorStream
                result = is2String(errorStream)
                errorStream?.close()
                Log.i("MeasureError", result)
            } finally {
                urlConnection?.disconnect()
                return result
            }
        }

        override fun onPostExecute(result: String) {
            if (result == "") {
                Log.i("MeasureError", "エラー")
            } else {
                Log.i("MeasureSuccess", "成功")
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