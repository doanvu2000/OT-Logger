package com.hit.otlogger.ui.dialog

import android.annotation.SuppressLint
import android.view.LayoutInflater
import com.hit.otlogger.base.BaseBottomSheetDialog
import com.hit.otlogger.databinding.DialogAddOtBinding
import com.hit.otlogger.util.clickWithAnimation
import com.hit.otlogger.util.getDay
import com.hit.otlogger.util.getMonth
import com.hit.otlogger.util.getYear
import com.hit.otlogger.util.showToast
import com.hit.otlogger.util.toTimeFormat

class AddOTBottomDialog : BaseBottomSheetDialog<DialogAddOtBinding>() {

    override fun initView() {

    }

    private var startDay = 0
    private var startMonth = 0
    private var startYear = 0
    private var startHour = 0
    private var startMinutes = 0

    private var endHour = 0
    private var endMinutes = 0

    private var onTimeSelected: ((
        day: Int, month: Int, year: Int, startHour: Int, startMinutes: Int, endHour: Int, endMinutes: Int
    ) -> Unit)? = null

    fun setOnTimeSelectedListener(
        listener: (
            startDay: Int, startMonth: Int, startYear: Int, startHour: Int, startMinutes: Int, endHour: Int, endMinutes: Int
        ) -> Unit
    ) {
        onTimeSelected = listener
    }

    override fun initData() {
        val calendar = java.util.Calendar.getInstance()
        startDay = calendar.getDay()
        startMonth = calendar.getMonth()
        startYear = calendar.getYear()
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
            if (startHour == 0 && startMinutes == 0 && endHour == 0 && endMinutes == 0) {
                showToast("Vui lòng chọn thời gian bắt đầu và kết thúc")
                return@setOnClickListener
            }

            onTimeSelected?.invoke(
                startDay, startMonth, startYear, startHour, startMinutes, endHour, endMinutes
            )
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun pickTimeStart() {
        val dialogPickTime = DialogPickHourMinutes(requireContext())
        dialogPickTime.show(
            startDay, startMonth, startYear, startHour, startMinutes
        ) { day, month, year, hour, minutes ->
            startDay = day
            startMonth = month
            startYear = year
            startHour = hour
            startMinutes = minutes

            updateTimeStart()
            calculateTimeOT()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun pickTimeEnd() {
        val dialogPickTime = DialogPickHourMinutes(requireContext())
        dialogPickTime.show(
            startDay, startMonth, startYear, startHour, startMinutes
        ) { day, month, year, hour, minutes ->
            if (day != startDay || month != startMonth || year != startYear) {
                showToast("Thời gian kết thúc phải trong cùng ngày với thời gian bắt đầu")
                return@show
            }

            if (hour < startHour || (hour == startHour && minutes < startMinutes)) {
                showToast("Thời gian kết thúc phải sau thời gian bắt đầu")
                return@show
            }

            endHour = hour
            endMinutes = minutes

            updateTimeEnd()
            calculateTimeOT()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTimeStart() {
        binding.tvStartTime.text =
            "${startHour.toTimeFormat()}:${startMinutes.toTimeFormat()} - " + "${startDay.toTimeFormat()}/${startMonth.toTimeFormat()}/$startYear"
    }

    @SuppressLint("SetTextI18n")
    private fun updateTimeEnd() {
        binding.tvEndTime.text =
            "${endHour.toTimeFormat()}:${endMinutes.toTimeFormat()} - " + "${startDay.toTimeFormat()}/${startMonth.toTimeFormat()}/$startYear"
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun calculateTimeOT() {
        if ((startHour == 0 && startMinutes == 0) || (endHour == 0 && endMinutes == 0)) {
            binding.tvCountTimeOT.text = "Thời gian OT: 0 giờ (0 giờ 0 phút)"
            return
        }

        if (endHour < startHour || (endHour == startHour && endMinutes < startMinutes)) {
            binding.tvCountTimeOT.text = "Thời gian OT: 0 giờ (0 giờ 0 phút)"
            showToast("Thời gian kết thúc phải sau thời gian bắt đầu")
            return
        }

        val totalStartMinutes = startHour * 60 + startMinutes
        val totalEndMinutes = endHour * 60 + endMinutes

        val diff = totalEndMinutes - totalStartMinutes

        val diffHour = diff / 60
        val diffMinutes = diff - (diffHour * 60)

        if (diffMinutes > 0) {
            binding.tvCountTimeOT.text = "Thời gian OT: $diffHour giờ $diffMinutes phút"
        } else {
            binding.tvCountTimeOT.text = "Thời gian OT: $diffHour giờ"
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): DialogAddOtBinding {
        return DialogAddOtBinding.inflate(layoutInflater)
    }
}