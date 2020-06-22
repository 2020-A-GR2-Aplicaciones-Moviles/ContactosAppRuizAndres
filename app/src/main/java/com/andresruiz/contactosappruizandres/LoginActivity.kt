package com.andresruiz.contactosappruizandres

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
        btnLogin.setOnClickListener {

            if (validarEmail(textEmail.text.toString())) {
                Toast.makeText(this, "rayos", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "textEmail.text.toString()", Toast.LENGTH_LONG).show();

            }
        }
    }

    private fun validarEmail(email: String): Boolean {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}