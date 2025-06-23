package com.hit.otlogger.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.hit.otlogger.R
import com.hit.otlogger.databinding.DialogPickHourMinutesBinding
import com.hit.otlogger.util.clickWithAnimation
import com.hit.otlogger.util.toTimeFormat

class DialogPickHourMinutes(private val context: Context) {
    private val binding by lazy {
        DialogPickHourMinutesBinding.inflate(LayoutInflater.from(context))
    }

    private val dialog by lazy {
        AlertDialog.Builder(context, R.style.dialog_transparent_width).setView(binding.root)
            .create()
    }

    fun isShowing(): Boolean {
        return dialog.isShowing
    }

    fun hide() {
        dialog.dismiss()
    }

    private var hourSelected = 0
    private var minutesSelected = 0

    private var daySelected: Int = 0
    private var monthSelected: Int = 0
    private var yearSelected: Int = 0

    fun show(
        day: Int = 0, month: Int, year: Int,
        hour: Int = 0, minutes: Int = 0,
        onPickListener: (
            day: Int, month: Int, year: Int, hour: Int, minutes: Int
        ) -> Unit
    ) {
        dialog.setCancelable(true)

        daySelected = day
        monthSelected = month
        yearSelected = year

        hourSelected = hour
        minutesSelected = minutes

        updateData()

        setupNumberPicker()

        binding.btnPickDay.clickWithAnimation {
            pickDay()
        }

        binding.btnPick.clickWithAnimation {
            onPickListener(daySelected, monthSelected, yearSelected, hourSelected, minutesSelected)
            hide()
        }

        if (!isShowing()) {
            dialog.show()
        }
    }

    private fun setupNumberPicker() {
        binding.hourPicker.apply {
            minValue = 0
            maxValue = 23
            value = hourSelected
            setOnValueChangedListener { _, _, newVal ->
                hourSelected = newVal
                updateData()
            }
        }

        binding.minutesPicker.apply {
            minValue = 0
            maxValue = 59
            value = minutesSelected
            setOnValueChangedListener { _, _, newVal ->
                minutesSelected = newVal
                updateData()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateData() {
        binding.tvResultPick.text =
            "${hourSelected.toTimeFormat()}:${minutesSelected.toTimeFormat()} - " + "${daySelected.toTimeFormat()}/${monthSelected.toTimeFormat()}/$yearSelected"
    }

    private fun pickDay() {
        val datePickerDialog = android.app.DatePickerDialog(
            context, { _, year, month, dayOfMonth ->
                // Save selected date
                daySelected = dayOfMonth
                monthSelected = month + 1
                yearSelected = year
                updateData()
            }, yearSelected, monthSelected - 1, daySelected
        )
        datePickerDialog.show()
    }
}