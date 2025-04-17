package com.ismanieji.parkingosystema

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {


    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginButton = findViewById(R.id.login_button)
        registerButton = findViewById(R.id.register_button)


        loginButton.setOnClickListener {
            openLoginActivity()
        }

        registerButton.setOnClickListener {
            openRegisterActivity()
        }
    }

    private fun openLoginActivity() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun openRegisterActivity() {
        val intent = Intent(this@MainActivity, RegisterActivity::class.java)
        startActivity(intent)
    }
}
