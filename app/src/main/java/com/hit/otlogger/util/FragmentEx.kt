package com.hit.otlogger.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hit.otlogger.base.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

fun Fragment.checkPermission(permission: String): Boolean {
    return requireContext().checkPermission(permission)
}

fun Fragment.findChildFragment(TAG: String): Fragment? {
    return childFragmentManager.findFragmentByTag(TAG)
}

fun Fragment.showToast(msg: String, isShowDurationLong: Boolean = false) {
    requireContext().showToast(msg, isShowDurationLong)
}

fun Fragment.getVersionName(): String {
    return requireContext().getVersionName()
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Fragment.delayBeforeAction(timeDelay: Long = 300, action: () -> Unit) {
    lifecycleScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            delay(timeDelay)
        }
        action()
    }
}

fun Fragment.launchWitCoroutine(
    dispatcher: CoroutineContext = Dispatchers.Main, action: () -> Unit
) {
    lifecycleScope.launch(dispatcher) {
        action()
    }
}

fun BaseFragment<*>.launchOnStarted(block: suspend CoroutineScope.() -> Unit) {
    launchCoroutine {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            block()
        }
    }
}

fun Fragment.safeViewLifecycleOwner(): LifecycleOwner? {
    if (view == null) {
        return null
    }
    return viewLifecycleOwner
}

@Suppress("DEPRECATION")
fun Fragment.getDefaultRotation(): Int {
    return try {
        requireActivity().windowManager.defaultDisplay.rotation
    } catch (e: Exception) {
        0
    }
}

val Fragment.currentState: Lifecycle.State?
    get() = if (view != null) viewLifecycleOwner.lifecycle.currentState else null

fun Fragment.currentStateIsResume() = currentState == Lifecycle.State.RESUMED