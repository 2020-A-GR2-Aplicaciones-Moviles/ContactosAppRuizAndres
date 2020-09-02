package com.andresruiz.contactosappruizandres

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern


class LoginActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance();
        InicializarArchivoPreferenciasEncriptado()
        LeerDatosDeArchivoPreferenciasEncriptado();

        btnLogin.setOnClickListener {
            //email
            if (validarEmail(textEmail.text.toString())) {
                AutenticarUsuario(textEmail.text.toString(), editTextTextPassword.text.toString())

            } else {
                Toast.makeText(this, "textEmail.text.toString()", Toast.LENGTH_LONG).show();

            }
            //validacion Password

            ////// guardar en archivo de preferencias información de login/clave
            EscribirDatosEnArchivoPreferenciasEncriptado();

        }




    }
    fun AutenticarUsuario(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    var intent = Intent(this,PrincipalTmpActivity2::class.java)
                    intent.putExtra(LOGIN_KEY,auth.currentUser!!.email)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(baseContext, task.exception!!.message,
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validarEmail(email: String): Boolean {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun InicializarArchivoPreferenciasEncriptado(){
         val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        sharedPreferences = EncryptedSharedPreferences.create(
            SECRET_FILENAME,//filename
            masterKeyAlias,
            this,//context
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    fun EscribirDatosEnArchivoPreferenciasEncriptado() {

        if (chkRecordarme.isChecked) {
            val editor = sharedPreferences.edit()
            editor.putString(LOGIN_KEY, textEmail.text.toString())
            editor.putString(PASSWORD_KEY, editTextTextPassword.text.toString())
            editor.commit()
        } else {
            val editor = sharedPreferences.edit()
            editor.putString(LOGIN_KEY, "")
            editor.putString(PASSWORD_KEY, "")
            editor.commit()
        }
    }
    fun LeerDatosDeArchivoPreferenciasEncriptado() {

        textEmail.setText(sharedPreferences.getString(LOGIN_KEY,""))
        editTextTextPassword.setText(sharedPreferences.getString(PASSWORD_KEY,""))
    }
    //funciones si se necesita almacernar en el almacenamiento intero del dispositivo

    fun EscribirDatosEnArchivoInterno(){
        val texto:String = "texto" + System.lineSeparator() +"almacenado"
        openFileOutput(CONTACTS_FILENAME,Context.MODE_PRIVATE).bufferedWriter().use{fos-> fos.write(texto)}
    }
    fun LeerDatosEnArchivoIntterno(){
        openFileInput(CONTACTS_FILENAME).bufferedReader().use {
            val datoLeido = it.readText()
            val textArray = datoLeido.split(System.lineSeparator());
            val texto1 = textArray[0]
            val texto2 = textArray[1]

        }
    }

}

