package com.puntl.sporttracker

import android.content.Context
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.View
import android.widget.DatePicker
import java.util.*

class DatePreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {
    private lateinit var datePicker: DatePicker

    init {
        dialogLayoutResource = R.layout.pref_dialog_date
        positiveButtonText = "ok"
        negativeButtonText = "cancel"
        dialogIcon = null
    }

    override fun onCreateDialogView(): View {
        val view = super.onCreateDialogView()
        datePicker = view.findViewById(R.id.edit) as DatePicker
        datePicker.maxDate = Date().time
        return view
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val year = datePicker.year
            val month = if (datePicker.month < 10-1) "0${datePicker.month + 1}" else "${datePicker.month + 1}"
            val day = if (datePicker.dayOfMonth < 10) "0${datePicker.dayOfMonth}" else "${datePicker.dayOfMonth}"
            persistString("$year-$month-$day")
        }
    }
}