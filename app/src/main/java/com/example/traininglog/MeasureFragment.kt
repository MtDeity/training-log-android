package com.example.traininglog

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_measure.*
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
            val dialogFragment = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { view, year, month, day ->
                etDate.setText("${year}/${month+1}/${day}")
            }, year, month, day)
            dialogFragment.show()
        }
    }
}