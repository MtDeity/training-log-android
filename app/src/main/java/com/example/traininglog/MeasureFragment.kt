package com.example.traininglog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_measure.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MeasureFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_measure, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        etDate.setText(date)


        etDate.setOnClickListener {
        }

    }
}

//class DatePickDialogFragmen : DialogFragment(), DatePickerDialog.OnDateSetListener {
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//    }
//
//    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
//    }
//
//
////    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
////        val c: Calendar = Calendar.getInstance()
////        val year: Int = c.get(Calendar.YEAR)
////        val month: Int = c.get(Calendar.MONTH)
////        val day: Int = c.get(Calendar.DAY_OF_MONTH)
////        return DatePickerDialog(
////            activity!!,
////            activity as MainActivity?, year, month, day
////        )
////    }
////
////    override fun onDateSet(
////        view: DatePicker?, year: Int,
////        monthOfYear: Int, dayOfMonth: Int
////    ) {
////    }
////
////
////    @Override
////    @NonNull
////    public Dialog onCreateDialog(Bundle savedInstanceState) {
////        final Calendar c = Calendar.getInstance();
////        int year = c.get(Calendar.YEAR);
////        int month = c.get(Calendar.MONTH);
////        int day = c.get(Calendar.DAY_OF_MONTH);
////
////        return new DatePickerDialog(getActivity(),
////        (MainActivity)getActivity(),  year, month, day);
////    }
////
////    @Override
////    public void onDateSet(android.widget.DatePicker view, int year,
////    int monthOfYear, int dayOfMonth) {
////    }
//}