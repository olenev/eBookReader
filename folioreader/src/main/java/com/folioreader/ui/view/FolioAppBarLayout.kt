package com.folioreader.ui.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.AppBarLayout

class FolioAppBarLayout : AppBarLayout {

    companion object {
        @JvmField
        val LOG_TAG: String = FolioAppBarLayout::class.java.simpleName
    }

    var navigationBarHeight: Int = 0
    var insets: Rect? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        ViewCompat.setOnApplyWindowInsetsListener(this) { _, windowInsets ->
            Log.v(LOG_TAG, "-> onApplyWindowInsets")

            val bars = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )

            this.insets = Rect(bars.left, bars.top, bars.right, bars.bottom)

            navigationBarHeight = bars.bottom

            setMargins(bars.left, bars.top, bars.right)
            windowInsets
        }
    }

    override fun fitSystemWindows(insets: Rect?): Boolean {
        Log.v(LOG_TAG, "-> fitSystemWindows")
        // For API level 19 and below

        this.insets = Rect(insets)

        navigationBarHeight = insets!!.bottom

        setMargins(insets.left, insets.top, insets.right)
        return super.fitSystemWindows(insets)
    }

    private fun setMargins(left: Int, top: Int, right: Int) {

        val marginLayoutParams = layoutParams as MarginLayoutParams
        marginLayoutParams.leftMargin = left
        marginLayoutParams.topMargin = top
        marginLayoutParams.rightMargin = right
        layoutParams = marginLayoutParams
    }

    fun setTopMargin(top: Int) {
        val marginLayoutParams = layoutParams as MarginLayoutParams
        marginLayoutParams.topMargin = top
        layoutParams = marginLayoutParams
    }
}