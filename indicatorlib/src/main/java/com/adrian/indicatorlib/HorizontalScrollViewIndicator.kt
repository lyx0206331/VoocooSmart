package com.adrian.indicatorlib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.HorizontalScrollView

/**
 * date:2018/12/5 16:48
 * author：RanQing
 * description：水平滚动指示器，需搭配HorScrollView使用
 */
class HorScrollViewIndicator @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        const val COLOR_BACKGROUND = Color.GRAY
        const val COLOR_BAR = Color.CYAN
    }

    //指示器背景画笔
    private val bgPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }
    //指示器进度滑块画笔
    private val barPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }
    //HorScrollView滚动距离，自定义监听
    private var mScrollX = 0
    //HorScrollView的可滚动长度。其值为HorScrollView的内容总宽度与HorScrollView宽度之差
    private var allowScrollWidth = 0
    var mHorScrollView: HorScrollView? = null
        set(value) {
            field = value
            value?.scrollViewListener = object : HorScrollView.ScrollViewListener {
                override fun onScrollChanged(scrollView: HorScrollView, x: Int, y: Int, oldX: Int, oldY: Int) {
//                    logE("scroll x:$x, y:$y, oldx:$oldX, oldy:$oldY")
                    mScrollX = x
                    invalidate()
                }
            }
        }
    //指示器背景颜色
    var mBgColor: Int = COLOR_BACKGROUND
        set(value) {
            field = value
            bgPaint.color = value
            invalidate()
        }
    //指示器进度滑块颜色
    var mBarColor: Int = COLOR_BAR
        set(value) {
            field = value
            barPaint.color = value
            invalidate()
        }
    //指示器角半径
    var mRadius = 5f
        set(value) {
            field = value
            invalidate()
        }
    //指示器滑块宽度。根据HorScrollView中的内容总宽度与HorScrollView宽度的比例计算
    private var mBarWidth = 0

    init {
        val a = context?.obtainStyledAttributes(attrs, R.styleable.HorScrollViewIndicator, defStyleAttr, 0)
        a?.apply {
            mBgColor = getColor(R.styleable.HorScrollViewIndicator_hsvi_background_color, COLOR_BACKGROUND).orZero()
            mBarColor = getColor(R.styleable.HorScrollViewIndicator_hsvi_bar_color, COLOR_BAR).orZero()
            mRadius = getDimension(R.styleable.HorScrollViewIndicator_hsvi_radius, 5f)
        }
        a?.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val width = if (widthMode == MeasureSpec.EXACTLY) widthSize else widthSize.shr(1)
        val height = if (heightMode == MeasureSpec.EXACTLY) heightSize else heightSize.shr(1)

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (mHorScrollView?.childCount == 0) {
            throw Exception("HorScrollView must has a child view")
        }
        val mScrollViewW = mHorScrollView?.width.orZero()
        val mScrollChildViewW = mHorScrollView?.getChildAt(0)?.width.orZero()
        allowScrollWidth = mScrollChildViewW - mScrollViewW
        //防止onDraw()方法中分母为0
        allowScrollWidth = allowScrollWidth.coerceAtLeast(1)
        mBarWidth = (1f * width * mScrollViewW / mScrollChildViewW).toInt()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val bgRectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        //计算滑块在指示器上的滑动距离
        val offsetX = 1f * (width - mBarWidth) * mScrollX / allowScrollWidth
        val barRectF = RectF(offsetX, 0f, offsetX + mBarWidth, height.toFloat())
        //绘制指示器背景
        canvas?.drawRoundRect(bgRectF, mRadius, mRadius, bgPaint)
        //绘制指示器滑块
        canvas?.drawRoundRect(barRectF, mRadius, mRadius, barPaint)
    }
}

/**
 * 此类搭配HorScrollViewIndicator使用
 */
class HorScrollView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    HorizontalScrollView(context, attrs, defStyleAttr) {

    //自定义滚动监听器
    var scrollViewListener: ScrollViewListener? = null

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        scrollViewListener?.onScrollChanged(this, l, t, oldl, oldt)
    }

    interface ScrollViewListener {
        fun onScrollChanged(scrollView: HorScrollView, x: Int, y: Int, oldX: Int, oldY: Int)
    }
}

fun logE(msg: String) {
    Log.e("SCROLLVIEW", msg)
}