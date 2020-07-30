package com.example.heavenesports.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.heavenesports.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_user_info.view.*
import kotlinx.android.synthetic.main.settings_activity.view.*
import kotlinx.coroutines.*


class UserInfoFragment : Fragment() {

    lateinit var userinfoView : View
//
//    lateinit var btn_Foto_Perfil : Button

    lateinit var btn_guarda_user : Button

    lateinit var btn_regresar : Button

    lateinit var edt_username : EditText

    lateinit var edt_tag : EditText

    lateinit var foto_perfil : CircleImageView

    val database = FirebaseFirestore.getInstance()

    val storage = Firebase.storage

    val storageRef = storage.reference

    var flag_foto = 0

    var foto_uri: Uri? = null
    lateinit var foto_URL : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        userinfoView = inflater.inflate(R.layout.fragment_user_info, container, false)

//        btn_Foto_Perfil = userinfoView.findViewById(R.id.btn_foto_perfil)

//        val prefs = activity?.getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE)
//        if (prefs != null) {
//            if (prefs.getString("foto_perfil","No") == "Si"){
//                val foto_user =
//                    requireActivity().findViewById<View>(R.id.foto_user_cache) as ImageView
//
//                btn_Foto_Perfil.setBackgroundDrawable(foto_user.drawable)
//            }
//
//        }

        btn_guarda_user = userinfoView.findViewById(R.id.btn_guardar_user)
        btn_regresar = userinfoView.findViewById(R.id.btn_regresar)

        foto_perfil = userinfoView.findViewById(R.id.foto_perfil)


//        if (prefs != null) {
//            if (prefs.getString("foto_perfil","No") == "Si"){
//                val foto_user =
//                    requireActivity().findViewById<View>(R.id.foto_user_cache) as ImageView
//
//                foto_perfil.setImageBitmap(foto_user.drawable.toBitmap())
////                foto_perfil.setBackgroundDrawable(foto_user.drawable)
//            }
//
//        }

        edt_username = userinfoView.findViewById(R.id.edt_username)
        edt_tag = userinfoView.findViewById(R.id.edt_tag)

        return userinfoView
    }

    override fun onStart() {
        super.onStart()

        val user_uid = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseStorage.getInstance().getReference("/fotos/player/$user_uid")

        val prefs = activity?.getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE)
        if (prefs != null) {
            if (prefs.getString("foto_perfil","No") == "Si"){
                val foto_user =
                    requireActivity().findViewById<View>(R.id.foto_user_cache) as ImageView

                if (flag_foto == 0){
                    foto_perfil.setImageBitmap(foto_user.drawable.toBitmap())
                }
            }
        }

        val info = database.collection("usuarios").document("$user_uid").get()
            .addOnSuccessListener { dataSnapshot  ->
                if (dataSnapshot  != null){
                    Log.d("Test", "DocumentSnapshot data: ${dataSnapshot["tag"].toString()}")
                    edt_tag.setText(dataSnapshot.getString("tag"))
                    edt_username.setText(dataSnapshot.getString("username"))
                }
            }


        foto_perfil.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, 0)
        }

        btn_guarda_user.setOnClickListener{
            uploadImageToFirebaseStorage()
        }

        btn_regresar.setOnClickListener{
            flag_foto = 0
            val action =
                UserInfoFragmentDirections.actionUserInfoFragmentToHomeFragment()
            userinfoView.findNavController().navigate(action)
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data!= null){
//
//            foto_uri = data.data
//            try {
//                foto_uri?.let {
//                    if(Build.VERSION.SDK_INT < 28) {
//                        val bitmap = MediaStore.Images.Media.getBitmap(
//                            userinfoView.context.contentResolver,
//                            foto_uri
//                        )
//                        btn_Foto_Perfil.text=""
//                        btn_Foto_Perfil.setBackgroundDrawable(BitmapDrawable(resources, bitmap))
//                    } else {
//                        val source = ImageDecoder.createSource(userinfoView.context.contentResolver,
//                            foto_uri!!
//                        )
//                        val bitmap = ImageDecoder.decodeBitmap(source)
//                        btn_Foto_Perfil.text=""
//                        btn_Foto_Perfil.setBackgroundDrawable(BitmapDrawable(resources, bitmap))
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data!= null){

            flag_foto = 1
            foto_uri = data.data
            try {
                foto_uri?.let {
                    if(Build.VERSION.SDK_INT < 28) {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            userinfoView.context.contentResolver,
                            foto_uri
                        )
                        foto_perfil.setImageBitmap(bitmap)
                    } else {
                        val source = ImageDecoder.createSource(userinfoView.context.contentResolver,
                            foto_uri!!
                        )
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        foto_perfil.setImageBitmap(bitmap)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImageToFirebaseStorage(){
        if (foto_uri == null) return

        val user_uid = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseStorage.getInstance().getReference("/fotos/player/$user_uid")

        ref.putFile(foto_uri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    foto_URL = it.toString()
                    saveUserToFirebaseDatabase(foto_URL, edt_username.text.toString(), edt_tag.text.toString())
                }
            }
            .addOnFailureListener{
            }
    }

    fun saveUserToFirebaseDatabase(URL_foto : String, username : String, tag : String) {

        val user_uid = FirebaseAuth.getInstance().currentUser?.uid
        val usuario = hashMapOf(
           "URL_foto_perfil" to URL_foto,
            "username" to username,
            "tag" to tag
        )

        database.collection("usuarios").document(user_uid!!).set(usuario)
            .addOnSuccessListener {
                Snackbar.make(userinfoView,
                    R.string.ok_foto, Snackbar.LENGTH_SHORT)
        }
            .addOnFailureListener{
                Snackbar.make(userinfoView,
                    R.string.error_foto, Snackbar.LENGTH_SHORT).setAction("Reintentar"){
                    database.collection("usuarios")
                        .add(usuario)
                }
            }
    }

    fun updateImageProfile(){
        val user_uid = FirebaseAuth.getInstance().currentUser?.uid
        val prefs = activity?.getSharedPreferences(getString(R.string.login_pref), Context.MODE_PRIVATE)

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

                if (prefs != null) {
                    prefs.edit().putString("foto_perfil", "Si")
                }
                if (prefs != null) {
                    prefs.edit().apply()
                }

                Snackbar.make(userinfoView,"Perfil actualizado correctamente",Snackbar.LENGTH_SHORT)

            }
        }
        .addOnFailureListener{
            if (prefs != null) {
                prefs.edit().putString("foto_perfil", "No")
            }
            if (prefs != null) {
                prefs.edit().apply()
            }
            Snackbar.make(userinfoView,"Fallo la actualización de perfil",Snackbar.LENGTH_SHORT)
        }
    }
}