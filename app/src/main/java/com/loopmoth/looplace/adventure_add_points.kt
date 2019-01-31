package com.loopmoth.looplace

import android.annotation.SuppressLint
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_adventure_add_points.*

class adventure_add_points : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var database: DatabaseReference
    //odwołanie do bazy danych

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val mMarkerArray = ArrayList<Marker>()
    //tablica markerów na mapie

    private var long: Double? = 0.0
    private var lat: Double? = 0.0
    //współrzędne użytkownika

    private var pointLong: Double = 0.0
    private var pointLat: Double = 0.0
    //współrzędne klikanego punktu

    private var pointSelected: Boolean = false
    //czy punkt jest zaznaczony?

    private var tempmarker: Marker? = null
    //tymczasowy marker tworzony podczas kliknięcia na mapę

    private lateinit var name: String
    private lateinit var desc: String
    //dane z formularza wyżej

    private var infoWindowShown: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adventure_add_points)

        name = intent.getStringExtra("name")
        desc = intent.getStringExtra("desc")
        //przesłane dane z poprzedniego formularza

        viewForm.visibility = View.INVISIBLE

        //Toast.makeText(this@adventure_add_points, name+desc, Toast.LENGTH_LONG).show()
        //do sprawdzenia czy przesyłanie danych pomiędzy aktywnościami jest poprawne

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //do sprawdzenia ostatniej lokalizacji użytkownika, aby otworzyć mapę w jego lokalizacji

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //wczytanie fragmentu mapy
    }

    override fun onResume() {
        super.onResume()
        bAdd.setOnClickListener {
            if(tempmarker!=null){
                //jeżeli jest tymczasowy marker (czyli po kliknięciu na mapę)
                pointLat = tempmarker!!.position.latitude
                pointLong = tempmarker!!.position.longitude
                //zapisanie jego współrzędnych

                mMarkerArray.remove(tempmarker!!)
                tempmarker!!.remove()
                //usunięcie tego markera

                val mark = mMap.addMarker(MarkerOptions().title(tName.text.toString()).snippet(tDesc.text.toString()).position(LatLng(pointLat, pointLong)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                mark.tag = "alll"
                //dodanie nowego, już stałego
                pointSelected = false
                viewForm.visibility = View.INVISIBLE
                clearFormView()

                mMarkerArray.add(mark)
                //dodanie markera do tablicy
            }
        }
        bCancel.setOnClickListener {
            if(tempmarker!=null){
                mMarkerArray.remove(tempmarker!!)
                tempmarker!!.remove()
                pointSelected = false
                viewForm.visibility = View.INVISIBLE
                clearFormView()
                //usunięcie zaznaczonego znacznika
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //odniesienie do mapy, aby można było w łatwy sposób odwoływać się do mapy
        obtainLocation()
        //pobranie lokalizacji, przeniesienie tam widoku i prybliżenie

        mMap.setOnMapClickListener {
            //po kliknięciu na mapę -> akcja, it-> obiekt LatLng przetrzymujący współrzędne, ma właściowści latitude i longitude
            if(!pointSelected && !infoWindowShown){
                //jeżeli jest już fioletowy marker i nie jest pokazane okienko z informacjami dotyczącego wskaźnika -> nie rób nic, inaczej -> pokaż formularz i dodaj marker
                tempmarker = mMap.addMarker(MarkerOptions().position(it).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)))
                viewForm.visibility = View.VISIBLE
                pointSelected = true
            }
            else{
                viewForm.visibility = View.INVISIBLE
                infoWindowShown = false
                clearFormView()
                //po odznaczeniu wskaźnika schowaj okno z edycją wskaźnika

                mMarkerArray.forEach{
                    Toast.makeText(this@adventure_add_points, it.tag.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        mMap.setOnMarkerClickListener {
            if(!it.isInfoWindowShown){
                it.showInfoWindow()
                infoWindowShown = true
                viewForm.visibility = View.VISIBLE
                //pokaż okno z informacjami dotyczącymi punktu oraz okno z możliwością edycji punktu

                setFormViewText(it.title.toString(), it.snippet.toString())
                //ustawienie tekstu

                tempmarker = it
            }

            return@setOnMarkerClickListener true
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtainLocation(){
        //pobieranie lokalizacji użytkownika
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                lat =  location?.latitude
                long = location?.longitude
                //pobranie koordynatów użytkownika

                val currentLocation = LatLng(lat!!, long!!)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16.0f))
                //newLatLng - też poprawna funkcja, ale bez możliwości przybliżania od strony programistycznej
            }
    }

    private fun clearFormView(){
        tName.text.clear()
        tDesc.text.clear()
    }

    private fun setFormViewText(name: String, desc: String){
        tName.setText(name)
        tDesc.setText(desc)
    }
}
