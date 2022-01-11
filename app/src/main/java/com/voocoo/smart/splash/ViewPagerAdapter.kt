package com.voocoo.smart.splash

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.PagerAdapter
import com.voocoo.smart.R

/**
 * date:2018/11/30 11:03
 * author：RanQing
 * description：
 */
class ViewPagerAdapter : PagerAdapter() {

    val items = arrayListOf(
        Item(R.mipmap.voocoo_fafa),
        Item(R.mipmap.voocoo_keke),
        Item(R.mipmap.voocoo_yoyo)
    )

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.item_splash_viewpager, container, false)
        view.findViewById<ImageView>(R.id.imageView).setImageResource(items[position].res)
//        view.findViewById<CardView>(R.id.cardView).foreground = container.context.getDrawable(items[position].res)
        container.addView(view)
        return view
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    class Item(@DrawableRes val res: Int)
}