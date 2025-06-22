package com.hit.otlogger.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.drawable.Drawable
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.hit.otlogger.BuildConfig


fun Context.checkPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED
}

fun Context.openActivity(pClass: Class<out Activity>, bundle: Bundle?) {
    val intent = Intent(this, pClass)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    startActivity(intent)
}

fun Context.openActivity(pClass: Class<out Activity>, isFinish: Boolean = false) {
    openActivity(pClass, null)
    if (isFinish) {
        (this as Activity).finish()
    }
}

fun Context.openActivity(pClass: Class<out Activity>, isFinish: Boolean = false, bundle: Bundle?) {
    openActivity(pClass, bundle)
    if (isFinish) {
        (this as Activity).finish()
    }
}

fun Context.openActivity(
    pClass: Class<out Activity>,
    isFinish: Boolean = false,
    isAnimation: Boolean = true,
    bundle: Bundle?
) {
    openActivity(pClass, bundle)
    if (isAnimation) {
        (this as Activity).openWithSlide()
    }
    if (isFinish) {
        (this as Activity).finish()
    }
}

@SuppressLint("DiscouragedApi")
fun Context.getDrawableIdByName(name: String): Int {
    return resources.getIdentifier(name, "drawable", packageName)
}

fun Context.inflateLayout(layoutResource: Int, parent: ViewGroup): View {
    return LayoutInflater.from(this).inflate(layoutResource, parent, false)
}

fun Context.shareApp() {
    val subject = "Let go to record your emoji today!!"
    val sharingIntent = Intent(Intent.ACTION_SEND)
    sharingIntent.type = "text/plain"
    val shareBody = "https://play.google.com/store/apps/details?id=$packageName"
    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
    this.startActivity(Intent.createChooser(sharingIntent, "Share to"))
}

fun Context.navigateToMarket(publishNameStore: String) {
    val market = "market://details?id="
    val webPlayStore = "https://play.google.com/store/apps/details?id="
    try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW, Uri.parse(market + publishNameStore)
                //market://details?id=<package_name>
            )
        )
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW, Uri.parse(webPlayStore + publishNameStore)
                //https://play.google.com/store/apps/details?id=<package_name>
            )
        )
    }
}

fun Context.rateApp() {
    val uri = Uri.parse("market://details?id=$packageName")
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    goToMarket.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    try {
        startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
            )
        )
    }
}

fun Context.sendEmail(toEmail: String, feedBackString: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    val data = Uri.parse(
        "mailto:$toEmail?subject=$feedBackString&body="
    )
    intent.data = data
    try {
        startActivity(intent)
    } catch (ex: Exception) {
        showToast("Not have email app to send email!")
        ex.printStackTrace()
    }
}

fun Context.getRingTone(): Ringtone {
    val defaultRingtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
    return RingtoneManager.getRingtone(this, defaultRingtoneUri)
}

fun Context.isNetworkAvailable(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
    return if (capabilities != null) {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(
            NetworkCapabilities.TRANSPORT_WIFI
        ) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    } else false
}

/**
 * require declare permission ACCESS_NETWORK_STATE in Manifest
 * */
fun Context.isInternetAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    return when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

fun Context.showToast(msg: String, isShowDurationLong: Boolean = false) {
    val duration = if (isShowDurationLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    Toast.makeText(this, msg, duration).show()
}

fun Context.getColorById(colorSource: Int): Int {
    return ContextCompat.getColor(this, colorSource)
}

fun Context.getColorByIdWithTheme(colorAttr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(colorAttr, typedValue, true)
    return typedValue.data
}

fun Context.getVersionName(): String {
    return try {
        val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        packageInfo.versionName ?: BuildConfig.VERSION_NAME
    } catch (e: Exception) {
        BuildConfig.VERSION_NAME
    }
}

fun Context.getDrawableById(drawableId: Int): Drawable? {
    return ContextCompat.getDrawable(this, drawableId)
}

fun Context.getLinkApp() = "https://play.google.com/store/apps/details?id=$packageName"

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}