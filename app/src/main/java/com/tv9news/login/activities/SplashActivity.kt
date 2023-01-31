package com.tv9news.login.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.tv9news.R
import com.tv9news.databinding.ActivitySplashBinding
import com.tv9news.home.activities.MainActivity
import com.tv9news.utils.helpers.Constants
import com.tv9news.utils.helpers.SharedPreference
import kotlinx.coroutines.delay

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        lifecycleScope.launchWhenCreated {
            delay(1000)
            startActivity(
                if (SharedPreference.instance?.getBoolean(Constants.IS_PREFENCE_SELECTED) ?: false == false)
                    Intent(this@SplashActivity, LoginActivity::class.java)
                else
                    Intent(this@SplashActivity, MainActivity::class.java)
            )
            finish()
        }
    }
}