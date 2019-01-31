package com.loopmoth.looplace

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main_menu.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter


class MainMenu : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        database = FirebaseDatabase.getInstance().reference
        //tworzymy odwołanie do bazy
    }

    override fun onResume() {
        super.onResume()
        readUserID()
        bAdd.setOnClickListener {
            if(GPSStatus()){
                //jeżeli jest wysoka dokładność GPS
                val intent = Intent(this@MainMenu, NewAdventureForm::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this@MainMenu, "Włącz wysoką dokładność GPS", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS) //otwiera ustawienia związane z lokalizacją
                startActivity(intent)
            }
        }
        bPlay.setOnClickListener {
            if(GPSStatus()){
                //jeżeli jest wysoka dokładność GPS
                val intent = Intent(this@MainMenu, MainPlayMap::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this@MainMenu, "Włącz wysoką dokładność GPS", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS) //otwiera ustawienia związane z lokalizacją
                startActivity(intent)
            }
        }
    }

    fun readUserID(){
        val filename = "userID.conf"
        //tworzy się nowy plik, gdzie przechowywany jest ID użytkownika tworzony przez FireB

        if(fileList().contains(filename)) {
            //jeżeli plik istnieje
            readID(filename)
        }
        else{
            createFileWithID(filename)
        }
    }

    fun createFileWithID(filename: String){
        //tworzy plik
        try {
            val database = FirebaseDatabase.getInstance().reference
            val newUser = database.child("users").push()
            val user = User(newUser.key)
            newUser.setValue(user)

            val file = OutputStreamWriter(openFileOutput(filename, Activity.MODE_PRIVATE))
            file.write (newUser.key)
            file.flush ()
            file.close ()

            //Toast.makeText(this, "stworzono", Toast.LENGTH_LONG).show()
        } catch (e : IOException) {
        }
    }

    fun readID(filename: String){
        //czyta ID
        try {
            val file = InputStreamReader(openFileInput(filename))
            val br = BufferedReader(file)
            var line = br.readLine()
            val all = StringBuilder()
            while (line != null) {
                all.append(line + "\n")
                line = br.readLine()
            }
            br.close()
            file.close()
            //tvID.text=all
        }
        catch (e: IOException) {
        }
    }

    fun GPSStatus(): Boolean {
        //sprawdza czy jest wysoka dokładność
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val GpsStatus: Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return GpsStatus
    }
}
