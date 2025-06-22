package com.hit.otlogger.ui.dialog

import android.annotation.SuppressLint
import android.view.LayoutInflater
import com.hit.otlogger.base.BaseBottomSheetDialog
import com.hit.otlogger.databinding.DialogAddOtBinding
import com.hit.otlogger.util.CalendarUtil
import com.hit.otlogger.util.clickWithAnimation
import com.hit.otlogger.util.showToast

class AddOTBottomDialog : BaseBottomSheetDialog<DialogAddOtBinding>() {

    override fun initView() {

    }

    private var startTime: Long = 0L
    private var endTime: Long = 0L

    private var onTimeSelected: ((startTime: Long, endTime: Long) -> Unit)? = null
    fun setOnTimeSelectedListener(listener: (startTime: Long, endTime: Long) -> Unit) {
        onTimeSelected = listener
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.btnPickStartTime.setOnClickListener {
            pickTimeStart()
        }

        binding.btnPickEndTime.setOnClickListener {
            pickTimeEnd()
        }

        binding.btnClose.clickWithAnimation {
            dismiss()
        }

        binding.btnAddOT.setOnClickListener {
            if (startTime == 0L || endTime == 0L) {
                showToast("Vui lòng chọn thời gian bắt đầu và kết thúc")
                return@setOnClickListener
            }
            if (endTime <= startTime) {
                showToast("Thời gian kết thúc phải sau thời gian bắt đầu")
                return@setOnClickListener
            }
            onTimeSelected?.invoke(startTime, endTime)
            dismiss()
        }
    }

    private fun pickTimeStart() {
        val calendar = java.util.Calendar.getInstance()

        val datePickerDialog = android.app.DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Save selected date
                calendar.set(year, month, dayOfMonth)

                // After date is selected, show TimePickerDialog
                val timePickerDialog = android.app.TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        // Set time in calendar
                        calendar.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(java.util.Calendar.MINUTE, minute)

                        // Convert to milliseconds
                        startTime = calendar.timeInMillis

                        // Format and display date and time
                        val dateTimeFormat = java.text.SimpleDateFormat(
                            "dd/MM/yyyy - HH:mm", java
                                .util.Locale.getDefault()
                        )
                        binding.btnPickStartTime.text = dateTimeFormat.format(calendar.time)

                        if (endTime != 0L && endTime <= startTime) {
                            calculateTimeOT()
                        }
                    },
                    calendar.get(java.util.Calendar.HOUR_OF_DAY),
                    calendar.get(java.util.Calendar.MINUTE),
                    true // 24-hour format
                )
                timePickerDialog.show()
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun pickTimeEnd() {
        val calendar = java.util.Calendar.getInstance()

        val datePickerDialog = android.app.DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Save selected date
                calendar.set(year, month, dayOfMonth)

                // After date is selected, show TimePickerDialog
                val timePickerDialog = android.app.TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        // Set time in calendar
                        calendar.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(java.util.Calendar.MINUTE, minute)

                        // Convert to milliseconds
                        if (calendar.timeInMillis <= startTime) {
                            showToast("Thời gian kết thúc phải sau thời gian bắt đầu")
                            return@TimePickerDialog
                        }
                        endTime = calendar.timeInMillis

                        // Format and display date and time
                        val dateTimeFormat = java.text.SimpleDateFormat(
                            "dd/MM/yyyy - HH:mm", java
                                .util.Locale.getDefault()
                        )
                        binding.btnPickEndTime.text = dateTimeFormat.format(calendar.time)

                        calculateTimeOT()
                    },
                    calendar.get(java.util.Calendar.HOUR_OF_DAY),
                    calendar.get(java.util.Calendar.MINUTE),
                    true // 24-hour format
                )
                timePickerDialog.show()
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun calculateTimeOT() {
        CalendarUtil.diffTime(startTime, endTime) { hour, minutes ->
            val totalTime = String.format("%.1f", hour + minutes.toFloat() / 60)

            binding.tvCountTimeOT.text = "Thời gian OT: $totalTime giờ ($hour giờ $minutes phút)"
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): DialogAddOtBinding {
        return DialogAddOtBinding.inflate(layoutInflater)
    }
}