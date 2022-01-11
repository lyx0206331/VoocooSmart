package com.voocoo.smart.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.voocoo.smart.R
import com.voocoo.smart.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        val viewPager = binding.viewPager
        viewPager.adapter = MainViewPagerAdapter(this)
        val tabLayout = binding.tabs
        TabLayoutMediator(tabLayout, viewPager) { tab, posision ->
            when(posision) {
                FRAGMENT_HOME_PAGE_INDEX -> {
                    tab.setIcon(R.drawable.home_tab_selector)
                    tab.setText(R.string.home)
                }
                FRAGMENT_MARKET_PAGE_INDEX -> {
                    tab.setIcon(R.drawable.home_tab_selector)
                    tab.setText(R.string.market)
                }
                FRAGMENT_FORUM_PAGE_INDEX -> {
                    tab.setIcon(R.drawable.home_tab_selector)
                    tab.setText(R.string.forum)
                }
                FRAGMENT_MINE_PAGE_INDEX -> {
                    tab.setIcon(R.drawable.home_tab_selector)
                    tab.setText(R.string.mine)
                }
            }
        }.attach()
        return binding.root
    }

}

const val FRAGMENT_HOME_PAGE_INDEX = 0
const val FRAGMENT_MARKET_PAGE_INDEX = 1
const val FRAGMENT_FORUM_PAGE_INDEX = 2
const val FRAGMENT_MINE_PAGE_INDEX = 3

class MainViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        FRAGMENT_HOME_PAGE_INDEX to { HomeFragment() },
        FRAGMENT_MARKET_PAGE_INDEX to { MarketFragment() },
        FRAGMENT_FORUM_PAGE_INDEX to { ForumFragment() },
        FRAGMENT_MINE_PAGE_INDEX to { MineFragment() }
    )

    override fun getItemCount(): Int = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment = tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
}