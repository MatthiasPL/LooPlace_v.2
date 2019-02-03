package com.loopmoth.looplace

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_adventure_list.*

class AdventureList : AppCompatActivity() {

    var mode: String? = null
    private val adventureArray: MutableList<Adventure> = mutableListOf()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adventure_list)

        database = FirebaseDatabase.getInstance().reference

        mode = intent.getStringExtra("mode")
        Toast.makeText(this@AdventureList, mode, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()

        initAdventures()
    }

    //TODO: finish firebase
    private fun initAdventures() {
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                adventureArray.clear()
                dataSnapshot.children.mapNotNullTo(adventureArray) { it.getValue<Adventure>(Adventure::class.java) }

                val adapter = ArrayAdapter(this@AdventureList, android.R.layout.simple_list_item_1, adventureArray)
                adventurelist.adapter = adapter

                adventureArray.forEach(){
                    Toast.makeText(this@AdventureList, it.name, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        database.child("adventures").addListenerForSingleValueEvent(menuListener)
    }
}
