package com.example.heavenesports.Activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.heavenesports.R
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        title = "Autenticación"

    }

    override fun onStart() {
        super.onStart()

        authLayout.visibility = View.VISIBLE
    }

}


