package com.hit.otlogger.data.database

import androidx.lifecycle.asFlow
import com.hit.otlogger.base.BaseViewModel
import com.hit.otlogger.data.model.OTModel
import com.hit.otlogger.util.CalendarUtil
import com.hit.otlogger.util.toTimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.Calendar
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OTViewModel(private val repository: AppRepository) : BaseViewModel() {
    companion object {
        const val NAME_SUFFIX = "Guess Challenge"
    }

    suspend fun Any.await(): Any {
        return suspendCoroutine {
            it.resume(this)
        }
    }

    val allData: Flow<List<OTModel>> = repository.allData.asFlow()

    fun insertData(entity: OTModel) {
        launchSafe(Dispatchers.IO) {
            repository.insertGallery(entity)
        }
    }

    fun deleteData(entity: OTModel) {
        launchSafe(Dispatchers.IO) {
            repository.deleteGallery(entity)
        }
    }

    fun calculateTotalOT(
        data: List<OTModel>, onResult: (hour: Int, minutes: Int, total: Int) -> Unit
    ) {
        var total = 0
        data.forEach {
            CalendarUtil.diffTime(
                it.hourStart, it.minutesStart, it.hourEnd, it.minutesEnd
            ) { hour, minutes ->
                total += hour * 60 + minutes
            }
        }

        val hour = total / 60
        val minutes = total - hour * 60

        onResult.invoke(hour, minutes, total)
    }

    fun getTotalFormat(
        data: List<OTModel>,
        monthSelected: Int,
        yearSelected: Int,
        totalOTTime: ((content: String, minutes: Int, avg: Int) -> Unit)? = null
    ): String {
        val title = "Giờ OT tháng $monthSelected: \n"
        val body = StringBuilder()
        var total = 0
        data.forEach { ot ->

            if (ot.year == yearSelected && ot.month == monthSelected) {

                CalendarUtil.diffTime(
                    ot.hourStart, ot.minutesStart, ot.hourEnd, ot.minutesEnd
                ) { hour, minutes ->
                    total += hour * 60 + minutes

                    body.append("-Ngày ${ot.day.toTimeFormat()}/${ot.month.toTimeFormat()}/${ot.year}:")

                    body.append("${ot.getTimeStart()} - ${ot.getTimeEnd()}")
                    if (hour > 0) {
                        body.append(" ($hour giờ")
                    }

                    if (minutes > 0) {
                        body.append(" $minutes phút")
                    }

                    body.append(")\n")
                    body.append("--------------------------------\n")
                }
            }
        }

        val totalHour = total / 60
        val totalMinutes = total - totalHour * 60

        val timeString = StringBuilder("")

        if (totalHour > 0) {
            timeString.append("$totalHour giờ ")
        }

        if (totalMinutes > 0) {
            timeString.append("$totalMinutes phút")
        }

        val content = StringBuilder()
        content.append(title)
        content.append(body)
        content.append("\nTổng: $timeString")

        val avg = calculateAverageOT(total, monthSelected, yearSelected)

        totalOTTime?.invoke(content.toString(), total, avg)
        return content.toString()
    }

    /**
     * Calculates the average overtime hours for a given list of OTModel.
     * If a specific date is provided, calculates for that day.
     * If a week (list of dates) is provided, calculates for the week.
     * Returns the average in hours as a Float.
     */
    fun calculateAverageOT(
        totalMinutes: Int, monthSelected: Int, yearSelected: Int
    ): Int {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.MONTH, monthSelected - 1) // Calendar.MONTH is zero-based
            set(Calendar.YEAR, yearSelected)
        }

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 4

        return totalMinutes / daysInMonth
    }

    sealed class Event {
        data object ClickAdd : Event()
        data object ClickChooseMonth : Event()
        data object ClickCopy : Event()
    }

    private val eventChannel = Channel<Event>()
    val event = eventChannel.receiveAsFlow()

    fun clickAdd() {
        sendEvent(eventChannel, Event.ClickAdd)
    }

    fun clickChooseMonth() {
        sendEvent(eventChannel, Event.ClickChooseMonth)
    }

    fun clickCopy() {
        sendEvent(eventChannel, Event.ClickCopy)
    }
}