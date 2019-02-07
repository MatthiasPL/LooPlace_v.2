package com.loopmoth.looplace

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_new_adventure_form.*

class NewAdventureForm : AppCompatActivity() {

    private val PERMISSIONS_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_adventure_form)
    }

    override fun onResume() {
        super.onResume()
        bAddNewAdventure.setOnClickListener {
            val permission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            if (permission == PackageManager.PERMISSION_GRANTED) {
                //sprawdzenie czy jest nadane uprawnienie dostępu do lokalizacji
                if(tDesc.text.toString()!="" && tName.text.toString()!=""){ //ZAD tutaj usuwasz kiedy chcesz mieć puste, daj domyślnie jakieś spacje czy coś
                    //jeżeli te pola nie są puste -> nowa aktywność
                    val intent = Intent(this@NewAdventureForm, adventure_add_points::class.java)

                    val name = tName.text.toString()
                    val desc = tDesc.text.toString()

                    intent.putExtra("name", name)
                    intent.putExtra("desc", desc)
                    //przesyłanie danych do następnej aktywności

                    tName.text.clear()
                    tDesc.text.clear()
                    //wyczyszczenie formularza

                    startActivity(intent)
                    //otwiera nową aktywność - mapę z możliwością dodawania punktów
                }
                else{
                    Toast.makeText(this@NewAdventureForm, "Uzupełnij pola tekstowe", Toast.LENGTH_LONG).show()
                }
            }
            else{
                //jeżeli nie, poproś o uprawnienie
                val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                //tablica uprawnień
                ActivityCompat.requestPermissions(this,
                    permissions,
                    PERMISSIONS_REQUEST)
                Toast.makeText(this@NewAdventureForm, "Po ustawieniu uprawnień wciśnij przycisk raz jeszcze", Toast.LENGTH_LONG).show()
            }
        }
    }
}
