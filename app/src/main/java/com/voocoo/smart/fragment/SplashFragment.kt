package com.voocoo.smart.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.findNavController
import com.voocoo.smart.R
import com.voocoo.smart.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SplashFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSplashBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            while (binding.pbLoading.progress < 100) {
                delay(20)
                ++binding.pbLoading.progress
            }
            if (binding.pbLoading.progress == binding.pbLoading.max) {
                findNavController().navigate(R.id.fragmentMain)
//                findNavController().popBackStack()
            }
        }
        return binding.root
    }
}