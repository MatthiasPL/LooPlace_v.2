package com.loopmoth.looplace

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main_play_map.*

class MainPlayMap : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_play_map)
    }

    override fun onResume() {
        super.onResume()
        bShowAdventures.setOnClickListener {
            //val listmusic = mutableListOf<String>()
            val advlist = mutableListOf("adv1","adv2","a","d","v","e","n","t","u","r","e")

            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, advlist)
            AdvList.adapter = adapter

            bStart.visibility=Button.GONE

            if(AdvList.visibility==ListView.INVISIBLE) {
                bShowAdventures.text="Ukryj dostępne mapy"
                AdvList.visibility = ListView.VISIBLE
            }
            else if(AdvList.visibility==ListView.VISIBLE) {
                bShowAdventures.text="Pokaż dostępne mapy"
                AdvList.visibility = ListView.INVISIBLE
            }


        }
        AdvList.setOnItemClickListener { parent, view, position, id ->
            AdvList.visibility=ListView.GONE
            bShowAdventures.text="Pokaż dostępne mapy"
            val advname = AdvList.getItemAtPosition(position)
            bStart.text="Rozpocznij przygodę "+advname
            bStart.visibility=Button.VISIBLE
        }
    }

    
}