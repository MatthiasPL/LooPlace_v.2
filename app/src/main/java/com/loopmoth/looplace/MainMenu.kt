package com.loopmoth.looplace

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main_menu.*

class MainMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
    }

    override fun onResume() {
        super.onResume()
        bAdd.setOnClickListener {
            val intent = Intent(this@MainMenu, NewAdventureForm::class.java)
            startActivity(intent)
        }
        bPlay.setOnClickListener {

        }
    }
}
