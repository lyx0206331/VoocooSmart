package com.adrian.indicatorsample.viewpager

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs
import kotlin.math.max

/**
 * date:2018/11/30 9:47
 * author：RanQing
 * description：
 */
class ZoomOutPageTransformer : ViewPager.PageTransformer {

    companion object {
        const val MIN_SCALE = .9f
        const val MIN_ALPHA = .5f
    }

    override fun transformPage(page: View, position: Float) {
        val pageW = page.width
        val pageH = page.height

        when {
            position < -1 -> //向左滑出屏幕
                page.alpha = 0f
            position <= 1 -> {  //修改默认滑动过渡以缩小页面
                val scaleFactor = max(MIN_SCALE, 1 - abs(position))
                val vMargin = pageH * (1 - scaleFactor) / 2
                val hMargin = pageW * (1 - scaleFactor) / 2
                if (position < 0) {
                    page.translationX = hMargin - vMargin / 2
                } else {
                    page.translationX = -hMargin + vMargin / 2
                }

                page.scaleX = scaleFactor
                page.scaleY = scaleFactor

                page.alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
            }
            else -> page.alpha = 0f   ////向右滑出屏幕
        }
    }
}