package com.voocoo.smart.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.adrian.indicatorsample.viewpager.ZoomOutPageTransformer
import com.voocoo.commlib.showShortToast
import com.voocoo.smart.MainActivity
import com.voocoo.smart.R
import com.voocoo.smart.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivitySplashBinding>(this,
            R.layout.activity_splash
        )
        setContentView(binding.root)

        binding.viewPager.apply {
            adapter = ViewPagerAdapter()
            setPageTransformer(true, ZoomOutPageTransformer())
            binding.dotsIndicator.mViewPager = this
        }

        binding.btnEnterHome.setOnClickListener {
            if (binding.pbLoading.progress == binding.pbLoading.max) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            } else {
                showShortToast(R.string.loading)
            }
        }

        lifecycleScope.launch {
            while (binding.pbLoading.progress < 100) {
                delay(50)
                ++binding.pbLoading.progress
            }
        }
    }
}