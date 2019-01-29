package com.loopmoth.looplace

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main_menu.*

class NewAdventureForm : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_adventure_form)
    }

    override fun onResume() {
        super.onResume()
        bAdd.setOnClickListener {

        }
    }
}
