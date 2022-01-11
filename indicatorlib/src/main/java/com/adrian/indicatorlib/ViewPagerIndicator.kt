package com.adrian.indicatorlib

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

/**
 * date:2018/11/28 16:05
 * author：RanQing
 * description：ViewPager指示器
 */
class DotsIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        const val DEFAULT_POINT_COLOR = Color.WHITE
        const val DEFAULT_WIDTH_FACTOR = 2.5f
    }

    val dots by lazy {
        arrayListOf<ImageView>()
    }
    var mViewPager: ViewPager? = null
        set(value) {
            field = value
            setUpViewPager()
            refreshDots()
        }
    var dotsSize = dp2px(context, 16)
    var dotsCornerRadius = dotsSize / 2
    var dotsSpacing = dp2px(context, 4)
    var currentPage = 0
    var dotsWidthFactor = DEFAULT_WIDTH_FACTOR
    @ColorInt
    var dotsSelectedColor: Int = Color.CYAN
        set(value) {
            field = value
            setUpCircleColors()
        }
    @ColorInt
    var dotsUnselectedColor = DEFAULT_POINT_COLOR
        set(value) {
            field = value
            setUpCircleColors()
        }
    var dotsClickable = true
    var pageChangedListener: ViewPager.OnPageChangeListener? = null

    init {
        orientation = HORIZONTAL

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.DotsIndicator, defStyleAttr, 0)
            dotsSelectedColor = a.getColor(R.styleable.DotsIndicator_di_dots_selected_color, DEFAULT_POINT_COLOR)
            dotsUnselectedColor = a.getColor(R.styleable.DotsIndicator_di_dots_unselected_color, DEFAULT_POINT_COLOR)
            setUpCircleColors()

            dotsWidthFactor = a.getFloat(R.styleable.DotsIndicator_di_dots_width_factor, 2.5f)
            if (dotsWidthFactor < 1) {
                dotsWidthFactor = 2.5f
            }
            dotsSize = a.getDimension(R.styleable.DotsIndicator_di_dots_size, dotsSize)
            dotsCornerRadius = a.getDimension(R.styleable.DotsIndicator_di_dots_corner_radius, dotsSize / 2)
            dotsSpacing = a.getDimension(R.styleable.DotsIndicator_di_dots_spacing, dotsSpacing)

            a.recycle()
        } else {
            setUpCircleColors()
        }

        if (isInEditMode) {
            addDots(5)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshDots()
    }

    private fun refreshDots() {
        if (mViewPager != null && mViewPager?.adapter != null) {
            if (dots.size.orZero() < mViewPager?.adapter?.count.orZero()) {
                addDots(mViewPager?.adapter?.count.orZero() - dots.size.orZero())
            } else if (dots.size.orZero() > mViewPager?.adapter?.count.orZero()) {
                removeDots(dots.size.orZero() - mViewPager?.adapter?.count.orZero())
            }
            setUpDotsAnimators()
        } else {
            Log.e(DotsIndicator::class.java.simpleName,
                    "You have to set an adapter to the view pager before !")
        }
    }

    private fun setUpDotsAnimators() {
        mViewPager?.apply {
            if (adapter != null && adapter?.count.orZero() > 0) {
                if (currentPage < dots.size.orZero()) {
                    val dot = dots[currentPage]
                    dot.apply {
                        val params = layoutParams as RelativeLayout.LayoutParams
                        params.width = dotsSize.toInt()
                        dot.layoutParams = params
                    }
                }

                currentPage = currentItem.orZero()
                if (currentPage >= dots.size.orZero()) {
                    currentPage = dots.size.orZero() - 1
                    setCurrentItem(currentPage, false)
                }
                val dot = dots[currentPage]
                dot.apply {
                    val params = layoutParams as RelativeLayout.LayoutParams
                    params.width = (dotsSize * dotsWidthFactor).toInt()
                    dot.layoutParams = params
                }

                if (pageChangedListener != null) {
                    removeOnPageChangeListener(pageChangedListener!!)
                }
                setUpOnPageChangedListener()
                addOnPageChangeListener(pageChangedListener!!)
            }
        }
    }

    private fun setUpOnPageChangedListener() {
        pageChangedListener = object : ViewPager.OnPageChangeListener {

            var lastPage = 0

            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position != currentPage && positionOffset == 0f || currentPage < position) {
                    setDotWidth(dots[currentPage], dotsSize.toInt())
                    currentPage = position
                }

                if (abs(currentPage - position) > 1) {
                    setDotWidth(dots[currentPage], dotsSize.toInt())
                    currentPage = lastPage
                }

                var dot = dots[currentPage]

                var nextDot: ImageView? = null
                if (currentPage == position && currentPage + 1 < dots.size.orZero()) {
                    nextDot = dots[currentPage + 1]
                } else if (currentPage > position) {
                    nextDot = dot
                    dot = dots[currentPage - 1]
                }

                val dotWidth = (dotsSize + (dotsSize * (dotsWidthFactor - 1) * (1 - positionOffset))).toInt()
                setDotWidth(dot, dotWidth)

                if (nextDot != null) {
                    val nextDotWidth = (dotsSize + (dotsSize * (dotsWidthFactor - 1) * positionOffset)).toInt()
                    setDotWidth(nextDot, nextDotWidth)
                }

                lastPage = position
            }

            private fun setDotWidth(dot: ImageView?, dotWidth: Int) {
                dot?.apply {
                    val dotParams = layoutParams
                    dotParams.width = dotWidth
                    layoutParams = dotParams
                    if (dotWidth.toFloat() == dotsSize) {
                        (background as GradientDrawable).setColor(dotsUnselectedColor)
                    } else {
                        (background as GradientDrawable).setColor(dotsSelectedColor)
                    }
                }
            }

            override fun onPageSelected(p0: Int) {
            }
        }
    }

    private fun setUpViewPager() {
        mViewPager?.adapter?.apply {
            registerDataSetObserver(object : DataSetObserver() {
                override fun onChanged() {
                    super.onChanged()
                    refreshDots()
                }
            })
        }
    }

    private fun removeDots(count: Int) {
        for (i in 0 until count) {
            removeViewAt(childCount - 1)
            dots.removeAt(dots.size.orZero() - 1)
        }
    }

    private fun setUpCircleColors() {
        for ((index, value) in dots.withIndex()) {
            if (index == currentPage) {
                ((value.background) as GradientDrawable).setColor(dotsSelectedColor)
            } else {
                ((value.background) as GradientDrawable).setColor(dotsUnselectedColor)
            }
        }
    }

    private fun addDots(count: Int) {
        for (i in 0 until count) {
            val dot = LayoutInflater.from(context).inflate(R.layout.dot_layout, this, false)
            val iv = dot.findViewById<ImageView>(R.id.dot)
            val lp: RelativeLayout.LayoutParams = iv.layoutParams as RelativeLayout.LayoutParams
            lp.width = dotsSize.toInt()
            lp.height = dotsSize.toInt()
            lp.setMargins(dotsSpacing.toInt(), 0, dotsSpacing.toInt(), 0)
            (iv.background as GradientDrawable).cornerRadius = dotsCornerRadius
            (iv.background as GradientDrawable).setColor(dotsUnselectedColor)

            dot.setOnClickListener {
                if (dotsClickable && mViewPager != null && mViewPager?.adapter != null && i < mViewPager?.adapter?.count.orZero()) {
                    mViewPager?.setCurrentItem(i, true)
                }
            }

            dots.add(iv)
            addView(dot)
        }
    }

}

class WormDotsIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var strokeDots = arrayListOf<ImageView>()
    private var dotIndicatorView: ImageView? = null
    //    private var dotIndicatorLayout: View? = null
    private var viewPager: ViewPager? = null

    var dotsSize = dp2px(context, 16).toInt()
    var dotsCornerRadius = dotsSize / 2f
    var dotsSpacing = dp2px(context, 4).toInt()
    var dotsStrokeWidth = dp2px(context, 2).toInt()
    var dotIndicatorColor = getThemePrimaryColor(context)
        set(value) {
            field = value
            dotIndicatorView?.apply {
                setupDotBackground(false, dotIndicatorView!!)
            }
        }
    var dotsStrokeColor = dotIndicatorColor

    var horizontalMargin: Int = dp2px(context, 24).toInt()
    var dotIndicatorXSpring: SpringAnimation? = null
    var dotIndicatorWidthSpring: SpringAnimation? = null
    var strokeDotsLinearLayout: LinearLayout? = null

    var dotsClickable = true
    var pageChangedListener: ViewPager.OnPageChangeListener? = null

    init {
        strokeDotsLinearLayout = LinearLayout(context)
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.setMargins(horizontalMargin, 0, horizontalMargin, 0)
        strokeDotsLinearLayout?.layoutParams = lp
        strokeDotsLinearLayout?.orientation = LinearLayout.HORIZONTAL
        addView(strokeDotsLinearLayout)

        attrs?.apply {
            val a = context.obtainStyledAttributes(this, R.styleable.WormDotsIndicator, defStyleAttr, 0)
            dotIndicatorColor = a.getColor(R.styleable.WormDotsIndicator_wdi_dots_color, dotIndicatorColor)
            dotsStrokeColor = a.getColor(R.styleable.WormDotsIndicator_wdi_dots_stroke_color, dotsStrokeColor)
            dotsSize = a.getDimensionPixelSize(R.styleable.WormDotsIndicator_wdi_dots_size, dotsSize)
            dotsSpacing = a.getDimensionPixelSize(R.styleable.WormDotsIndicator_wdi_dots_spacing, dotsSpacing)
            dotsCornerRadius = a.getDimension(R.styleable.WormDotsIndicator_wdi_dots_corner_radius, dotsCornerRadius)
            dotsStrokeWidth = a.getDimensionPixelSize(R.styleable.WormDotsIndicator_wdi_dots_stroke_width, dotsStrokeWidth)

            a.recycle()
        }

        if (isInEditMode) {
            addStrokeDots(5)
            addView(buildDot(false))
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshDots()
    }

    private fun refreshDots() {
        if (dotIndicatorView == null) {
            setupDotIndicator()
        }
        if (viewPager == null || viewPager?.adapter == null) {
            Log.e(WormDotsIndicator::class.java.simpleName, "You have to set an adapter to the view pager before !")
        } else {
            viewPager?.adapter?.apply {
                if (strokeDots.size < count) {
                    addStrokeDots(count - strokeDots.size)
                } else if (strokeDots.size > count) {
                    removeDots(strokeDots.size - count)
                }
                setupDotsAnimators()
            }
        }
    }

    private fun setupDotIndicator() {
        dotIndicatorView = buildDot(false)
        addView(dotIndicatorView)
        dotIndicatorXSpring = SpringAnimation(dotIndicatorView, SpringAnimation.TRANSLATION_X)
        val springForceX = SpringForce(0f)
        springForceX.dampingRatio = 1f
        springForceX.stiffness = 300f
        dotIndicatorXSpring?.spring = springForceX

        val floatPropertyCompat = object : FloatPropertyCompat<Any>("DotsWidth") {
            override fun setValue(`object`: Any?, value: Float) {
                val params = dotIndicatorView?.layoutParams
                params?.width = value.toInt()
                dotIndicatorView?.requestLayout()
            }

            override fun getValue(`object`: Any?): Float {
                return dotIndicatorView?.layoutParams?.width.orZero().toFloat()
            }
        }
        dotIndicatorWidthSpring = SpringAnimation(dotIndicatorView, floatPropertyCompat)
        val springForceWidth = SpringForce(0f)
        springForceWidth.dampingRatio = 1f
        springForceWidth.stiffness = 300f
        dotIndicatorXSpring?.spring = springForceWidth
    }

    private fun addStrokeDots(count: Int) {
        for (i in 0 until count) {
            val dot = buildDot(true)
            dot.setOnClickListener {
                viewPager?.adapter?.apply {
                    if (dotsClickable && i < this.count) {
                        viewPager?.setCurrentItem(i, true)
                    }
                }
            }
            strokeDots.add(dot)
            strokeDotsLinearLayout?.addView(dot)
        }
    }

    private fun buildDot(stroke: Boolean): ImageView {
        val dotImageView = ImageView(context)
        dotImageView.background = ContextCompat.getDrawable(
            context,
            if (stroke) R.drawable.worm_dot_stroke_background else R.drawable.worm_dot_background
        )
        val lp = FrameLayout.LayoutParams(dotsSize, dotsSize)
        lp.gravity = Gravity.CENTER_VERTICAL
        lp.setMargins(dotsSpacing, 0, dotsSpacing, 0)
        dotImageView.layoutParams = lp

        setupDotBackground(stroke, dotImageView)
        return dotImageView
    }

    private fun setupDotBackground(stroke: Boolean, dotImageView: ImageView) {
        val dotBackground = dotImageView.background as GradientDrawable
        if (stroke) {
            dotBackground.setStroke(dotsStrokeWidth, dotsStrokeColor)
        } else {
            dotBackground.setColor(dotIndicatorColor)
        }
        dotBackground.cornerRadius = dotsCornerRadius
    }

    private fun removeDots(count: Int) {
        for (i in 0 until count) {
            strokeDotsLinearLayout?.removeViewAt(strokeDotsLinearLayout?.childCount.orZero() - 1)
            strokeDots.removeAt(strokeDots.size - 1)
        }
    }

    private fun setupDotsAnimators() {
        viewPager?.let {
            it.adapter?.apply {
                pageChangedListener?.apply {
                    if (count > 0) {
                        it.removeOnPageChangeListener(this)
                    }
                    setupOnPageChangedListener()
                    it.addOnPageChangeListener(this)
                }
            }
        }
    }

    private fun setupOnPageChangedListener() {
        pageChangedListener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                val stepX = dotsSize + dotsSpacing * 2
                var xFinalPosition: Float = 0f
                var widthFinalPosition: Float
                if (positionOffset >= 0 && positionOffset < .1f) {
                    xFinalPosition = (horizontalMargin + position * stepX).toFloat()
                    widthFinalPosition = dotsSize.toFloat()
                } else if (positionOffset in .1f .. .9f) {
                    xFinalPosition = (horizontalMargin + position * stepX).toFloat()
                    widthFinalPosition = dotsSize.toFloat() + stepX
                } else {
                    xFinalPosition = (horizontalMargin + (position + 1) * stepX).toFloat()
                    widthFinalPosition = dotsSize.toFloat()
                }
                if (dotIndicatorXSpring?.spring?.finalPosition != xFinalPosition) {
                    dotIndicatorXSpring?.spring?.finalPosition = xFinalPosition
                }

                if (dotIndicatorWidthSpring?.spring?.finalPosition != widthFinalPosition) {
                    dotIndicatorWidthSpring?.spring?.finalPosition = widthFinalPosition
                }

                if (!dotIndicatorXSpring?.isRunning.orFalse()) {
                    dotIndicatorXSpring?.start()
                }

                if (!dotIndicatorWidthSpring?.isRunning.orFalse()) {
                    dotIndicatorWidthSpring?.start()
                }
            }

            override fun onPageSelected(position: Int) {
            }

        }
    }

    private fun setupViewPager() {
        viewPager?.adapter?.apply {
            this.registerDataSetObserver(object : DataSetObserver() {
                override fun onChanged() {
                    super.onChanged()
                    refreshDots()
                }
            })
        }
    }
}

fun dp2px(context: Context, dp: Int): Float = context.resources.displayMetrics.density * dp

fun getThemePrimaryColor(context: Context): Int {
    val value = TypedValue()
    context.theme.resolveAttribute(android.R.attr.colorPrimary, value, true)
    return value.data
}

fun Int?.orZero(): Int = this ?: 0

fun Boolean?.orFalse(): Boolean = this ?: false