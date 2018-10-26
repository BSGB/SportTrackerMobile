package com.puntl.sporttracker

import android.content.Context
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import android.widget.TextView

private const val SEEK_BAR_MAX = 60000
private const val SEEK_BAR_STEP = 10000
private const val SEEK_BAR_DEFAULT = 10000

class SeekBarPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {
    private lateinit var seekBar: SeekBar
    private lateinit var progressTextView: TextView
    private var progress = SEEK_BAR_DEFAULT

    init {
        dialogLayoutResource = R.layout.pref_dialog_seekbar
        positiveButtonText = "ok"
        negativeButtonText = "cancel"
        dialogIcon = null
    }

    override fun onCreateDialogView(): View {
        val view = super.onCreateDialogView()
        seekBar = view.findViewById(R.id.edit) as SeekBar
        progressTextView = view.findViewById(R.id.progressEditText) as TextView

        seekBar.max = SEEK_BAR_MAX
        progressTextView.text = context.getString(R.string.picked_tracker_time, (progress / MILLIS_IN_SECOND).toString())
        seekBar.progress = progress

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                val rawProgress = progress / SEEK_BAR_STEP * SEEK_BAR_STEP
                val progress = if (rawProgress == 0) 1000 else rawProgress

                this@SeekBarPreference.progress = progress
                progressTextView.text = context.getString(R.string.picked_tracker_time, (progress / MILLIS_IN_SECOND).toString())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
        return view
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            persistString(progress.toString())
        }
    }
}