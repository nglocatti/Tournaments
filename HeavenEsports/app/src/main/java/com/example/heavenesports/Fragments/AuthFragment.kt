package com.example.heavenesports.Fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.heavenesports.Activities.HomeActivity
import com.example.heavenesports.Activities.ProviderType
import com.example.heavenesports.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthFragment : Fragment() {
    lateinit var loginView : View

    lateinit var edt_Email : EditText
    lateinit var edt_Password : EditText
    lateinit var btn_Ingresar : Button
    lateinit var btn_Registrar : Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        loginView = inflater.inflate(R.layout.fragment_auth, container, false)

        edt_Email = loginView.findViewById(R.id.edt_email)
        edt_Password = loginView.findViewById(R.id.edt_password)
        btn_Ingresar = loginView.findViewById(R.id.btn_ingresar)
        btn_Registrar = loginView.findViewById(R.id.btn_registrar)


        setup()


        return loginView
    }

    override fun onStart() {
        super.onStart()

        authLayout.visibility = View.VISIBLE
    }


    private fun setup(){
        btn_Registrar.setOnClickListener{
            if (edt_Email.text.isNotEmpty() && edt_Password.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(edt_Email.text.toString(),
                    edt_Password.text.toString()).addOnCompleteListener {

                    if (it.isSuccessful){
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    }
                    else
                    {
                        showAlert()
                    }
                    }
            }
        }
        btn_Ingresar.setOnClickListener{
            if (edt_Email.text.isNotEmpty() && edt_Password.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(edt_Email.text.toString(),
                    edt_Password.text.toString()).addOnCompleteListener {

                    if (it.isSuccessful){
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    }
                    else
                    {
                        showAlert()
                    }
                }
            }
        }
    }

    fun showAlert(){
        val builder = AlertDialog.Builder(loginView.context)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun showHome(email: String, provider: ProviderType){
        val homeIntent = Intent(loginView.context, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

}