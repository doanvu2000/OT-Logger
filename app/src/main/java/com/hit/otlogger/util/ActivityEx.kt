package com.hit.otlogger.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.OVERRIDE_TRANSITION_CLOSE
import android.app.Activity.OVERRIDE_TRANSITION_OPEN
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Context.ROLE_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.provider.Settings
import android.telecom.TelecomManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hit.otlogger.R
import com.hit.otlogger.base.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

fun Activity.setFullScreenMode(isFullScreen: Boolean = false) {
    if (isFullScreen) {
        if (isSdkR()) {
            val controller = window.insetsController

            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION") window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    } else {
        if (isSdkR()) {
            val controller = window.insetsController

            if (controller != null) {
                controller.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION") window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun ComponentActivity.getActivityResultLauncher(callBack: (Map<String, Boolean>) -> Unit): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        callBack.invoke(permissions)
    }
}

fun AppCompatActivity.requestMultiplePermission(permission: List<String>) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

    }.launch(permission.toTypedArray())
}

fun AppCompatActivity.findFragment(tag: String): Fragment? {
    return supportFragmentManager.findFragmentByTag(tag)
}

fun Activity.openSMS(smsBody: String, phone: String) {
    val intent = Intent(
        Intent.ACTION_SENDTO, Uri.parse("smsto:$phone")
    )
    intent.putExtra("sms_body", smsBody)
    this.startActivity(intent)
}

fun Activity.callPhone(phone: String) {
    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone"))
    startActivity(intent)
}

fun FragmentActivity.openNetWorkSetting() {
    try {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val cn = ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings")
        intent.component = cn
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    } catch (ignored: ActivityNotFoundException) {
        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
    }
}

@SuppressLint("UnspecifiedRegisterReceiverFlag")
fun Activity.registerBroadcast(
    broadcastReceiver: BroadcastReceiver,
    intentFilter: IntentFilter,
    listenToBroadcastsFromOtherApps: Boolean = false
) {
    if (isSdk33()) {
        val receiverFlags = if (listenToBroadcastsFromOtherApps) {
            Context.RECEIVER_EXPORTED
        } else {
            Context.RECEIVER_NOT_EXPORTED
        }
        registerReceiver(broadcastReceiver, intentFilter, receiverFlags)
    } else {
        registerReceiver(broadcastReceiver, intentFilter)
    }
}

fun Activity.getScreenWidth(): Int {
    return if (isSdkR()) {
        val windowMetrics = windowManager.currentWindowMetrics
        val insets =
            windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        windowMetrics.bounds.width() - insets.left - insets.right
    } else {
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION") windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }
}

fun Activity.getScreenHeight(): Int {
    return if (isSdkR()) {
        val windowMetrics = windowManager.currentWindowMetrics
        val insets =
            windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        windowMetrics.bounds.height() - insets.top - insets.bottom
    } else {
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION") windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.heightPixels
    }
}

fun Activity.openWithSlide() {
    if (isSdk34()) {
        overrideActivityTransition(
            OVERRIDE_TRANSITION_OPEN,
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            Color.TRANSPARENT
        )
    } else {
        @Suppress("DEPRECATION") overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }
}

/**
 * call when finish activity to apply animation
 *
 * */
fun Activity.finishWithSlide() {
    finish()
    if (isSdk34()) {
        overrideActivityTransition(
            OVERRIDE_TRANSITION_CLOSE,
            R.anim.slide_in_left,
            R.anim.slide_out_right,
            Color.TRANSPARENT
        )
    } else {
        @Suppress("DEPRECATION") overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
    }
}

fun AppCompatActivity.replaceFragment(
    frameId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true,
    bundle: Bundle? = null
) {
    bundle?.let {
        fragment.arguments = bundle
    }
    if (addToBackStack) {
        replaceFragmentAddBackStack(frameId, fragment)
    } else {
        replaceFragmentNoAddBackStack(frameId, fragment)
    }
}

fun AppCompatActivity.replaceFragmentNoAddBackStack(frameId: Int, fragment: Fragment) {
    Log.d(Constants.TAG, "replaceFragment no add backstack: ${fragment.javaClass.name}")
    supportFragmentManager.beginTransaction().replace(frameId, fragment).commit()
}

fun AppCompatActivity.replaceFragmentAddBackStack(frameId: Int, fragment: Fragment) {
    Log.d(Constants.TAG, "replaceFragment add backstack: ${fragment.javaClass.name}")
    supportFragmentManager.beginTransaction().replace(frameId, fragment)
        .addToBackStack(fragment.javaClass.name).commit()
}

fun AppCompatActivity.removeFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction().remove(fragment).commit()
}

fun Activity.openExactAlarmSettingPage() {
    if (isSdkS()) {
        startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
    }
}

/**
 * required android M,
 * requestOpenWriteSettingLauncher is registerForActivityResultLauncher
 * */
fun Activity.openManageWriteSetting() {
    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
    intent.data = Uri.parse("package:$packageName")
//        requestOpenWriteSettingLauncher.launch(intent)

}

/**
 * required android M,
 * requestOpenSettingLauncher is registerForActivityResultLauncher
 * */
fun Activity.openSettingOverlay() {
    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    intent.data = Uri.fromParts("package", packageName, null as String?)
//        requestOpenSettingLauncher.launch(intent)

}

/**
 * required android M,
 * startChangeDefaultDialler is registerForActivityResultLauncher
 * */
fun Activity.setDefaultPhoneApp() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
        val isHasRole = roleManager.isRoleAvailable(RoleManager.ROLE_DIALER)
        val isAppRoleHeld = roleManager.isRoleHeld(RoleManager.ROLE_DIALER)
        if (isHasRole) {
            if (!isAppRoleHeld) {
                val intent2 = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
//                startChangeDefaultDialler.launch(intent2)
            }
        }
    } else {
        val intent1 = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
        intent1.putExtra(
            TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, this.packageName
        )
//        startChangeDefaultDialler.launch(intent1)
    }
}

fun ComponentActivity.handleBackPressed(action: () -> Unit) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            action()
        }
    })
}

fun AppCompatActivity.delayBeforeAction(timeDelay: Long = 300, action: () -> Unit) {
    lifecycleScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            delay(timeDelay)
        }
        action()
    }
}

fun AppCompatActivity.launchWitCoroutine(
    dispatcher: CoroutineContext = Dispatchers.Main,
    action: () -> Unit
) {
    lifecycleScope.launch(dispatcher) {
        action()
    }
}

fun Activity.gotoDetailSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.parse("package:$packageName")
    startActivity(intent)
    /**
     * should use registerForActivityResult to handle result instead of (startActivity or startActivityForResult):
     * registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
     * */
}

/**
 * get mime type of file
 * */
fun Activity.getMimeType(uri: Uri): String? {
    return contentResolver.getType(uri)
}

/**
 * get name and size of file
 * */
fun Activity.getNameAndSizeFile(uri: Uri, onQueryFile: (name: String, size: Long) -> Unit) {
    contentResolver.query(
        uri, null, null, null, null
    )?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        val name = cursor.getString(nameIndex)
        val size = cursor.getLong(sizeIndex)
        onQueryFile.invoke(name, size)
    }
}

const val FLAG_KEEP_SCREEN_ON = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

fun Activity.enableScreenOn() {
    window.addFlags(FLAG_KEEP_SCREEN_ON)
}

fun Activity.disableScreenOn() {
    window.clearFlags(FLAG_KEEP_SCREEN_ON)
}

@Suppress("DEPRECATION")
fun Activity.setLayoutParamFullScreen() {
    window?.let {
        it.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        it.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
}

fun BaseActivity<*>.launchOnStarted(block: suspend CoroutineScope.() -> Unit) {
    launchCoroutine(Dispatchers.Main) {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            block()
        }
    }
}

fun Activity.checkShowRationale(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(
        this, permission
    )