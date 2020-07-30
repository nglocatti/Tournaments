package com.example.heavenesports.Activities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.heavenesports.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

enum class ProviderType{
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {

    lateinit var  menu_bottom : BottomNavigationView
    lateinit var nav_host : NavHostFragment

    lateinit var foto_user_cache : ImageView

    val database = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        nav_host = supportFragmentManager.findFragmentById(R.id.home_nav) as NavHostFragment

        menu_bottom = findViewById(R.id.tb_bottom)

        foto_user_cache = findViewById(R.id.foto_user_cache)

        NavigationUI.setupWithNavController(menu_bottom, nav_host.navController)

        val toolbar = findViewById<Toolbar>(R.id.tb_home)
        setSupportActionBar(toolbar)

        title = "Inicio"

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        val prefs = getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

    }

    override fun onStart() {
        super.onStart()

        val user_uid = FirebaseAuth.getInstance().currentUser?.uid
        val prefs = getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE).edit()

        database.collection("usuarios").document("$user_uid").get()
            .addOnSuccessListener { dataSnapshot  ->
                if (dataSnapshot  != null){
                    Glide.with(this)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .load(dataSnapshot.getString("URL_foto_perfil"))
                        .placeholder(R.drawable.foto_user)
                        .error(R.drawable.foto_user)
                        .into(foto_user_cache)

                    prefs.putString("foto_perfil", "Si")
                    prefs.apply()
                }
            }
            .addOnFailureListener{
                prefs.putString("foto_perfil", "No")
                prefs.apply()
            }
    }
}