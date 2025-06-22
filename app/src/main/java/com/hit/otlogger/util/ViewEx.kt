package com.hit.otlogger.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.hit.otlogger.R

fun View.hideKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun View.showKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.focusAndShowKeyboard() {
    /**
     * This is to be called when the window already has focus.
     */
    fun View.showTheKeyboardNow() {
        if (!isFocused) return
        postDelayed(
            {
                // We still post the call, just in case we are being notified of the windows focus
                // but InputMethodManager didn't get properly setup yet.
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            },
            200
        )
    }

    requestFocus()
    if (hasWindowFocus()) {
        // No need to wait for the window to get focus.
        showTheKeyboardNow()
    } else {
        // We need to wait until the window gets focus.
        val listener = object : ViewTreeObserver.OnWindowFocusChangeListener {
            override fun onWindowFocusChanged(hasFocus: Boolean) {
                // This notification will arrive just before the InputMethodManager gets set up.
                if (hasFocus) {
                    this@focusAndShowKeyboard.showTheKeyboardNow()
                    // It’s very important to remove this listener once we are done.
                    viewTreeObserver.removeOnWindowFocusChangeListener(this)
                }
            }
        }
        viewTreeObserver.addOnWindowFocusChangeListener(listener)
    }
}

fun View.disableView() {
    this.isClickable = false
    this.postDelayed({ this.isClickable = true }, 500)
}

class SafeClickListener(val onSafeClickListener: (View) -> Unit) : View.OnClickListener {
    override fun onClick(v: View) {
        v.disableView()
        onSafeClickListener(v)
    }
}

fun View.setOnSafeClick(onSafeClickListener: (View) -> Unit) {
    val safeClick = SafeClickListener {
        onSafeClickListener(it)
    }
    setOnClickListener(safeClick)
}

fun View.hide() {
    if (!isHide) {
        this.visibility = View.INVISIBLE
    }
}

fun View.show() {
    if (!isShow) {
        this.visibility = View.VISIBLE
    }
}

fun View.gone() {
    if (!isGone) {
        this.visibility = View.GONE
    }
}

fun clickAnimation(mContext: Context?, view: View) {
    //  return new AlphaAnimation(1F, 0.4F); // Change "0.4F" as per your recruitment.
    if (mContext != null) {
        val myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce)
        view.startAnimation(myAnim)
    }
}

fun View.clickAnimation() {
    try {
        if (!isAttachedToWindow) {
            return
        }
        context ?: return
        clickAnimation(context, this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.clickWithAnimation(action: (View) -> Unit) {
    setOnClickListener {
        clickAnimation()
        action.invoke(this)
    }
}

val View.isShow: Boolean
    get() = this.isVisible

val View.isHide: Boolean
    get() = this.isInvisible
val View.isGone: Boolean
    get() = this.visibility == View.GONE

fun View.showOrGone(isShow: Boolean) {
    if (isShow) {
        show()
    } else {
        gone()
    }
}

fun View.showOrHide(isShow: Boolean) {
    if (isShow) {
        show()
    } else {
        hide()
    }
}

fun View.setSize(width: Int, height: Int) {
    val rootParam = this.layoutParams
    rootParam.width = width
    rootParam.height = height
    layoutParams = rootParam
    requestLayout()
}

fun View.isSoftKeyboardVisible(): Boolean {
    val rect = Rect()
    rootView.getWindowVisibleDisplayFrame(rect)
    val screenHeight = rootView.height
    val keyboardHeight = screenHeight - rect.bottom
    val threshold = screenHeight * 0.15 // Adjust this value as per your requirements
    return keyboardHeight > threshold
}

/**
 * make view is free to add in another view group
 * */
fun View?.removeSelf() {
    this ?: return
    val parentView = parent as? ViewGroup ?: return
    parentView.removeView(this)
}

@SuppressLint("ClickableViewAccessibility")
fun View.setOnTouchAction(
    isDisableClick: Boolean = false,
    onDownAction: (() -> Unit)? = null,
    onUpAction: (() -> Unit)? = null,
    onMoveAction: (() -> Unit)? = null
) {
    setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onDownAction?.invoke()
            }

            MotionEvent.ACTION_UP -> {
                onUpAction?.invoke()
            }

            MotionEvent.ACTION_MOVE -> {
                onMoveAction?.invoke()
            }
        }
        isDisableClick
    }
}