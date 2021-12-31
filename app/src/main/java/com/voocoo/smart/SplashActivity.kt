package com.voocoo.smart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.voocoo.smart.databinding.ActivitySplashBinding
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivitySplashBinding>(this, R.layout.activity_splash)

        lifecycleScope.launch {
            while (pbLoading.progress < 100) {
                delay(20)
                ++pbLoading.progress
            }
            if (pbLoading.progress == pbLoading.max) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }
    }
}