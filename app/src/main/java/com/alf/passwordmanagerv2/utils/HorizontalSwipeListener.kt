package com.alf.passwordmanagerv2.utils

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

private const val TAG = "HorizontalSwipeListener"

class HorizontalSwipeListener(
    private val onSwipeLeft: (() -> Unit)? = null,
    private val onSwipeTop: (() -> Unit)? = null,
    private val onSwipeRight: (() -> Unit)? = null,
    private val onSwipeBottom: (() -> Unit)? = null
) : GestureDetector.SimpleOnGestureListener() {

    private val distanceThreshold = 100
    private val velocityThreshold = 100

    override fun onFling(
        e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float
    ): Boolean {
        try {
            if (e1 == null) return false

            val deltaX = e2.x - e1.x
            val deltaY = e2.y - e1.y

            if (abs(deltaX) > distanceThreshold && abs(velocityX) > velocityThreshold) {
                if (deltaX > 0) {
                    onSwipeRight?.invoke()
                    Log.d(TAG, "onFling: right")
                } else {
                    onSwipeLeft?.invoke()
                    Log.d(TAG, "onFling: left")
                }
                return true
            }

            if (abs(deltaY) > distanceThreshold && abs(velocityY) > velocityThreshold) {
                if (deltaY > 0) {
                    onSwipeBottom?.invoke()
                    Log.d(TAG, "onFling: bottom")
                } else {
                    onSwipeTop?.invoke()
                    Log.d(TAG, "onFling: top")
                }
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "onFling: $e")
        }
        return false
    }
}