package com.loopmoth.looplace

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_happy_ending.*

class HappyEnding : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_ending)
    }

    override fun onResume() {
        super.onResume()

        bMenu.setOnClickListener {
            val intent = Intent(this@HappyEnding, MainMenu::class.java)
            startActivity(intent)
        }

        bMap.setOnClickListener {
            if(GPSStatus()){
                //jeżeli jest wysoka dokładność GPS
                val intent = Intent(this@HappyEnding, MapsActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this@HappyEnding, "Włącz wysoką dokładność GPS", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS) //otwiera ustawienia związane z lokalizacją
                startActivity(intent)
            }
        }
    }

    fun GPSStatus(): Boolean {
        //sprawdza czy jest wysoka dokładność
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val GpsStatus: Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return GpsStatus
    }
}
