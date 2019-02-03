package com.loopmoth.looplace

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.FirebaseError
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_adventure_list.*
import kotlinx.android.synthetic.main.activity_new_adventure_form.*
import java.io.Serializable

class AdventureList : AppCompatActivity() {

    var mode: String? = null
    private val adventureArray: MutableList<Adventure> = mutableListOf()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adventure_list)

        database = FirebaseDatabase.getInstance().reference

        mode = intent.getStringExtra("mode")
        //sprawdzenie czy została wybrana akcja wyboru czy usuwania
        //Toast.makeText(this@AdventureList, mode, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()

        initAdventures()

        adventurelist.setOnItemClickListener { parent, view, position, id ->
            if(mode=="removal"){
                database.child("adventures").child(adventureArray[id.toInt()].key!!).setValue(null)
                adventureArray.removeAt(id.toInt())

                val advArray: MutableList<String> = mutableListOf()

                adventureArray.forEach{
                    advArray.add(it.name + "\n" + it.description + "\nLiczba punktów: " + it.markers!!.size.toString())
                }

                val adapter = ArrayAdapter(this@AdventureList, android.R.layout.simple_list_item_1, advArray)
                adventurelist.adapter = adapter
            }
            if(mode=="edit"){
                val intent = Intent(this@AdventureList, adventure_add_points::class.java)

                intent.putExtra("name", adventureArray[id.toInt()].name)
                intent.putExtra("desc", adventureArray[id.toInt()].description)

                intent.putExtra("adventureKey", adventureArray[id.toInt()].key)

                startActivity(intent)
            }
            //Toast.makeText(this@AdventureList, adventureArray[id.toInt()].key, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initAdventures() {
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                adventureArray.clear()
                dataSnapshot.children.mapNotNullTo(adventureArray) { it.getValue<Adventure>(Adventure::class.java) }

                val advArray: MutableList<String> = mutableListOf()

                adventureArray.forEach{
                    advArray.add(it.name + "\n" + it.description + "\nLiczba punktów: " + it.markers!!.size.toString())
                }

                val adapter = ArrayAdapter(this@AdventureList, android.R.layout.simple_list_item_1, advArray)
                adventurelist.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        database.child("adventures").addListenerForSingleValueEvent(menuListener)
    }
}
