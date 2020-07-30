package com.example.heavenesports.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.heavenesports.R
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT : Long = 3000 // 1 sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val parentJob = Job()

        val scope = CoroutineScope(Dispatchers.Main + parentJob)

        scope.launch {
            delay(SPLASH_TIME_OUT)
            session()
        }
    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE)
        val email = prefs?.getString("email", null)
        val provider = prefs?.getString("provider", null)

        if(email != null && provider != null){
            showHome(email, ProviderType.valueOf(provider))
            finish()
        }
        else{
            showAuth()
            finish()
        }

    }

    private fun showHome(email: String, provider: ProviderType){
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    private fun showAuth(){
        val authIntent = Intent(this, AuthActivity::class.java)
        startActivity(authIntent)
    }

}