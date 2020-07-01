package com.andresruiz.contactosappruizandres

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val sharedPref = getPreferences(Context.MODE_PRIVATE);
        textEmail.setText(sharedPref.getString(LOGIN_KEY,""))
        editTextTextPassword.setText(sharedPref.getString(PASSWORD_KEY,""))

        btnLogin.setOnClickListener {
            //email
            if (validarEmail(textEmail.text.toString())) {
                Toast.makeText(this, "rayos", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "textEmail.text.toString()", Toast.LENGTH_LONG).show();

            }
            //validacion Password

            ////// guardar en archivo de preferencias información de login/clave
            if(chkRecordarme.isChecked){
                val editor = sharedPref.edit();
                editor.putString(LOGIN_KEY,textEmail.text.toString())
                editor.putString(PASSWORD_KEY,editTextTextPassword.text.toString())
                editor.commit()
            }else{
                val editor = sharedPref.edit()
                editor.putString(LOGIN_KEY,"")
                editor.putString(PASSWORD_KEY,"")
                editor.commit()

            }
            Toast.makeText(this, "Mensaje Grabado", Toast.LENGTH_LONG).show();

        }




    }

    private fun validarEmail(email: String): Boolean {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}