package com.example.heavenesports.Fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.heavenesports.Activities.AuthActivity
import com.example.heavenesports.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException
import kotlin.properties.Delegates

class HomeFragment : Fragment() {

    lateinit var homeView : View

    lateinit var btn_Salir : Button
    lateinit var btn_GetPuuid : Button

    lateinit var txt_api : TextView
    lateinit var txt_api2 : TextView
    lateinit var txt_sunombre : TextView
    lateinit var txt_juego : TextView

    var muestronombre : String = "TextView"
    var muestrojuego : String = "TextView"

    val url = "https://americas.api.riotgames.com/riot/account/v1/accounts/by-riot-id/%C3%9Fanana/LAS?api_key=RGAPI-b7e34963-6e47-4251-be95-0b29efd85db4"

    val url2 = "https://americas.api.riotgames.com/riot/account/v1/active-shards/by-game/val/by-puuid/Lrl2tPKQKuLHOMSDPLljSW9eXBVLaDTm_5v6pOESPYGiJpRy2T8qvtKXfzGTJ1JXicoMjCwDg0IWiA?api_key=RGAPI-b7e34963-6e47-4251-be95-0b29efd85db4"

    val client = OkHttpClient()

    val request = Request.Builder().url(url).build()
    val request2 = Request.Builder().url(url2).build()

    val database = FirebaseFirestore.getInstance()

    lateinit var user_puuid : userpuuid


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeView = inflater.inflate(R.layout.fragment_home, container, false)

        btn_Salir = homeView.findViewById(R.id.btn_logout)
        btn_GetPuuid = homeView.findViewById(R.id.btn_getpuuid)

        txt_api = homeView.findViewById(R.id.txt_api)
        txt_api2 = homeView.findViewById(R.id.txt_api2)

        txt_sunombre = homeView.findViewById(R.id.txt_sunombre)
        txt_juego = homeView.findViewById(R.id.txt_juego)

        //https://americas.api.riotgames.com/riot/account/v1/accounts/by-riot-id/%C3%9Fanana/LAS
        //https://americas.api.riotgames.com/riot/account/v1/accounts/by-riot-id/ELPERROPORTUGUES/6636
        //val url = "https://api.letsbuildthatapp.com/youtube/home_feed"

        // Inflate the layout for this fragment
        return homeView
    }

    override fun onStart() {
        super.onStart()

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val parentJob = Job()

        val handler = CoroutineExceptionHandler { _, throwable ->
            txt_api.text = "ERROR"
        }
        val scope = CoroutineScope(Dispatchers.IO + parentJob + handler)

        scope.launch {
            //cargo_datos_usuario()
            task1()
            task2()
        }

        btn_Salir.setOnClickListener{
            showDialog(homeView)
        }

        btn_GetPuuid.setOnClickListener{
            txt_api.text = muestronombre
            txt_api2.text = muestrojuego
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {

            R.id.action_config -> {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToSettingsActivity()
                homeView.findNavController().navigate(action)
            }

            R.id.action_user_info -> {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToUserInfoFragment()
                homeView.findNavController().navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showDialog ( view : View) {
        val builder : AlertDialog.Builder = AlertDialog.Builder(view.context)
        builder.setTitle("Cerrar sesión")
        builder.setMessage("¿Seguro desea cerrar sesión?")
        var decision by Delegates.notNull<Boolean>()

        builder.setPositiveButton("Sí", DialogInterface.OnClickListener{ dialog, which ->
            dialog.dismiss()
            FirebaseAuth.getInstance().signOut()

            val prefs =
                this.activity?.getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE)
                    ?.edit()

            if (prefs != null) {
                prefs.clear()
                prefs.apply()
            }

            val authIntent = Intent(view.context, AuthActivity::class.java)
            startActivity(authIntent)
            activity?.finish()
        })

        builder.setNegativeButton("No", DialogInterface.OnClickListener{ dialog, which->
            dialog.dismiss()
        })

        val alertDialog : AlertDialog = builder.create()
        alertDialog.show()
    }

    suspend fun task1 (){

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                txt_api.text = "No se encuentra el usuario"
                println("No se encuentra el usuario")
            }

            override fun onResponse(call: Call, response: Response) {

                val body = response?.body?.string()

                println(body)

                val gson = GsonBuilder().create()

                user_puuid = gson.fromJson(body, userpuuid::class.java)


                muestronombre = user_puuid.gameName + "#" + user_puuid.tagLine

            }
        })

    }

    suspend fun task2 (){

        client.newCall(request2).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                txt_api.text = "No se encuentra el usuario"
                println("No se encuentra el usuario")
            }

            override fun onResponse(call: Call, response: Response) {

                val body = response?.body?.string()

                println(body)

                val gson = GsonBuilder().create()

                val game_puuid = gson.fromJson(body, gamepuuid::class.java)

                muestrojuego = game_puuid.game + " en " + game_puuid.activeShard

            }
        })
    }

    suspend fun cargo_datos_usuario(){
        val user_uid = FirebaseAuth.getInstance().currentUser?.uid
        val prefs =
            activity?.getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE)
                ?.edit()

        val info = database.collection("usuarios").document("$user_uid").get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot  != null){
                    Glide.with(this)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .load(dataSnapshot.getString("URL_foto_perfil"))
                        .placeholder(R.drawable.foto_user)
                        .into(foto_user_cache)

                    if (prefs != null) {
                        prefs.putString("foto_perfil", "Si")
                    }
                    if (prefs != null) {
                        prefs.apply()
                    }
                }
            }
            .addOnFailureListener{
                if (prefs != null) {
                    prefs.putString("foto_perfil", "No")
                }
                if (prefs != null) {
                    prefs.apply()
                }
            }
    }

    class userpuuid (val puuid: String, val gameName : String, val tagLine : String)

    class gamepuuid(val puuid: String, val game : String, val activeShard : String)

}

