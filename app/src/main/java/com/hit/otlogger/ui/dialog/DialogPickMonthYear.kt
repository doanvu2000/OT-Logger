package com.hit.otlogger.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import com.hit.otlogger.R
import com.hit.otlogger.databinding.DialogPickMonthYearBinding
import com.hit.otlogger.util.clickWithAnimation
import com.hit.otlogger.util.toTimeFormat
import java.util.Calendar

class DialogPickMonthYear(private val context: Context) {
    private val binding by lazy {
        DialogPickMonthYearBinding.inflate(LayoutInflater.from(context))
    }

    private val dialog by lazy {
        androidx.appcompat.app.AlertDialog.Builder(context, R.style.dialog_transparent_width)
            .setView(binding.root).create()
    }

    fun isShowing(): Boolean {
        return dialog.isShowing
    }

    fun hide() {
        dialog.dismiss()
    }

    private var monthSelected = 0
    private var yearSelected = 0

    fun show(onPickListener: (month: Int, year: Int) -> Unit) {
        dialog.setCancelable(true)

        val calendar = Calendar.getInstance()
        monthSelected = calendar.get(Calendar.MONTH) + 1
        yearSelected = calendar.get(Calendar.YEAR)

        updateData()

        setupNumberPicker()

        binding.btnPick.clickWithAnimation {
            onPickListener(monthSelected, yearSelected)
            hide()
        }

        if (!isShowing()) {
            dialog.show()
        }
    }

    private fun setupNumberPicker() {
        binding.monthPicker.apply {
            minValue = 1
            maxValue = 12
            value = monthSelected
            setOnValueChangedListener { _, _, newVal ->
                monthSelected = newVal
                updateData()
            }
        }

        binding.yearPicker.apply {
            minValue = yearSelected - 5
            maxValue = yearSelected + 5
            value = yearSelected
            setOnValueChangedListener { _, _, newVal ->
                yearSelected = newVal
                updateData()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateData() {
        binding.tvResultPick.text = "Tháng ${monthSelected.toTimeFormat()}, Năm $yearSelected"
    }
}