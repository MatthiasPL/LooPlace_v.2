package com.loopmoth.looplace

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_adventure_point.*

class AdventurePoint : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    //odwołanie do bazy danych

    private lateinit var advkey: String
    private var pointnum=1
    //który to punkt
    private var adventureTemp: Adventure? = null
    private val adventureArray: MutableList<Adventure> = mutableListOf()
    private var mark:CMarker?=null
    private val character = 5
    private val singlecharacter = character.toChar()
    //separator
    private lateinit var correctAnswer:String
    private var numberofpoints:Int?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adventure_point)

        database = FirebaseDatabase.getInstance().reference

        advkey = intent.getStringExtra("advkey")
        //pointnum=intent.getIntExtra("pointnum",-1)
        Toast.makeText(this@AdventurePoint, pointnum.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        tvQuestion.text = advkey
        GetFullAdventure()
        tvPowitanie.text = "Witaj w punkcie" + getNumber(mark!!.tag.toString()) + ": " + mark!!.title
        tvSnippet.text = mark!!.snippet
        tvQuestion.text = getQuestion(mark!!.tag.toString())
        correctAnswer = getAnswer(mark!!.tag.toString())

        Toast.makeText(this@AdventurePoint, advkey, Toast.LENGTH_SHORT).show()

        bAnswer.setOnClickListener {
            if (etAnswer.text.toString() == correctAnswer) {
                pointnum++
                if (pointnum <= numberofpoints!!) {
                    //jeśli to nie koniec przygody
                    GetFullAdventure()
                    //pobranie informacji o nowym znaczniku
                    val gmmIntentUri = Uri.parse("google.navigation:q="+ mark!!.latitude+","+mark!!.longitude);
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
                }
                else{
                    //koniec przygody
                    val intent = Intent(this@AdventurePoint, HappyEnding::class.java)
                    startActivity(intent)
                }
            }
            else {
                //jeśli zła odpowiedź
                etAnswer.text.clear()
                Toast.makeText(this@AdventurePoint, "Odpowiedź niepoprawna", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun GetFullAdventure() {
        //wczytanie markerów z bazy na mapę i dodanie tych markerów do tablicy
        val FullAdvListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists())
                    //Toast.makeText(this@MapsActivity, "hi", Toast.LENGTH_SHORT).show()
                adventureArray.clear()
                dataSnapshot.children.mapNotNullTo(adventureArray) { it.getValue<Adventure>(Adventure::class.java) }
                //mapowanie z bazy do tablicy
               // Toast.makeText(this@MapsActivity, adventureArray.toString(), Toast.LENGTH_SHORT).show()
                adventureArray.forEach{
                    if(it.key==advkey){
                        //jeżeli klucz przygody jest ten, którego poszukujemy, to stwórz przygodę z danych zawartych w bazie
                        adventureTemp = Adventure(it.name, it.description, it.markers!!, it.key)
                        //pobieramy ilosc punktów przygody
                        numberofpoints = it.markers!!.size
                    }
                }

                mark=adventureTemp!!.markers!![pointnum]
                    //pobranie danych o obecnym znaczniku
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
                //jeżeli nie uda się połączyć z bazą
            }
        }
        database.child("adventures").addListenerForSingleValueEvent(FullAdvListener)
    }

    private fun getNumber(tag: String): String{
        //wyciągnięcie liczby z tagu
        val result = tag.split(singlecharacter)
        return result[0]
    }

    private fun getQuestion(tag: String): String{
        //wyciągnięcie pytania z tagu
        val result = tag.split(singlecharacter)
        return result[1]
    }

    private fun getAnswer(tag: String): String{
        //wyciągnięcie odpowiedzi z tagu
        val result = tag.split(singlecharacter)
        return result[2]
    }
}

