package com.voocoo.commlib

import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat

//                       _ooOoo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                       O\ = /O
//                   ____/`---'\____
//                 .   ' \\| |// `.
//                  / \\||| : |||// \
//                / _||||| -:- |||||- \
//                  | | \\\ - /// | |
//                | \_| ''\---/'' | |
//                 \ .-\__ `-` ___/-. /
//              ______`. .' /--.--\ `. . __
//           ."" '< `.___\_<|>_/___.' >'"".
//          | | : `- \`.;`\ _ /`;.`/ - ` : | |
//            \ \ `-. \_ __\ /__ _/ .-` / /
//    ======`-.____`-.___\_____/___.-`____.-'======
//                       `=---='
//
//    .............................................
//             佛祖保佑             永无BUG
/**
 * Author:RanQing
 * Date:2021/12/21 5:15 下午
 * Description:
 */
infix fun SwitchCompat.onCheckedChanged(function: (CompoundButton, Boolean) -> Unit) {
    setOnCheckedChangeListener(function)
}

infix fun View.onClick(function: (view: View) -> Unit) {
    setOnClickListener { function(it) }
}

infix fun SeekBar.onProgressChanged(block: (seekBar: SeekBar, progress: Int, fromUser: Boolean) -> Unit) {
    setOnSeekBarChangeListener(object : OnSeekBarProgressChanged() {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            block(seekBar, progress, fromUser)
        }
    })
}

/**
 * 设置TextView是否可滚动
 *
 * @param scrollable
 */
infix fun TextView.isScrollable(scrollable: Boolean) {
    movementMethod = if (scrollable) ScrollingMovementMethod.getInstance() else null
}

/**
 * TextView滚动到最后一行
 *
 */
fun TextView.scroll2LastLine() {
    val offset = this.lineCount * this.lineHeight
    if (offset > this.height) {
        this.scrollTo(0, offset - height)
    }
}

abstract class OnSeekBarProgressChanged: SeekBar.OnSeekBarChangeListener {
    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}

/**
 * 防止重复点击监听
 * @param interval 禁连击时长
 * @param clickListener 有效监听
 */
fun View.setOnDisrepeatableClickListener(interval: Int = 500, clickListener: (view: View) -> Unit) {
    var clickCount = 0
    var lastClickTime = 0L
    onClick {
        val curMillis = System.currentTimeMillis()
        if (curMillis - lastClickTime > interval) {
            clickCount = 0
            clickListener.invoke(this)
        } else {
            clickCount++
            "重复点击".logE("repeat count:$clickCount")
        }
        lastClickTime = curMillis
    }
}

/**
 * 重复点击响应监听
 * 注：由于采用setOnClickListener()方法实现,所以会与此方法冲突,慎用!!!
 * @param validTimes 有效点击次数
 * @param interval 有效时间间隔
 * @param listener 点击响应监听器
 */
fun View.setOnRepeatClickListener(validTimes: Int = 2, interval: Int = 500, listener: (view: View) -> Unit) {

    var lastViewId = 0
    var clickCount = 0
    var lastClickTime = 0L

    onClick {
        if (this.id != lastViewId) {
            clickCount = 0
            lastViewId = this.id
            lastClickTime = 0L
        }
        val curMillis = System.currentTimeMillis()
        if (curMillis - lastClickTime <= interval) {
            clickCount++
        } else {
            clickCount = 0
        }
        lastClickTime = curMillis
        if (clickCount == validTimes-1) {
            listener.invoke(it)
        }
    }
}

/**
 * 双击响应监听
 * 注：由于采用setOnClickListener()方法实现,所以会与此方法冲突,慎用!!!
 */
fun View.setOnDoubleClickListener(listener: (view: View) -> Unit) {
    setOnRepeatClickListener {
        listener.invoke(it)
    }
}

/**
 * 三击响应监听
 * 注：由于采用setOnClickListener()方法实现,所以会与此方法冲突,慎用!!!
 */
fun View.setOnTripleClickListener(listener: (view: View) -> Unit) {
    setOnRepeatClickListener(3, listener = listener)
}

/**
 * 四击响应监听
 * 注：由于采用setOnClickListener()方法实现,所以会与此方法冲突,慎用!!!
 */
fun View.setOnQuadrupleClickListener(listener: (view: View) -> Unit) {
    setOnRepeatClickListener(4, listener = listener)
}

/**
 * 五击响应监听
 * 注：由于采用setOnClickListener()方法实现,所以会与此方法冲突,慎用!!!
 */
fun View.setOnPentaClickListener(listener: (view: View) -> Unit) {
    setOnRepeatClickListener(5, listener = listener)
}
