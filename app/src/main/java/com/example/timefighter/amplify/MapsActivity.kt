package com.example.timefighter.amplify

import android.app.ProgressDialog
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        addMarkers()
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(42.3,-83.7)))
    }

    fun addMarkers() {
        val database = Firebase.database
        val ref = database.getReference("submissions")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var subs: MutableList<EmailActivity.Submission> = arrayListOf<EmailActivity.Submission>()
                dataSnapshot.children.forEach {
                    val sub = EmailActivity.Submission(
                        it.child("address").getValue().toString(),
                        it.child("subject").getValue().toString(),
                        it.child("body").getValue().toString()
                    )
                    subs.add(sub)
                }
                subs.forEach {
                    mMap.addMarker((MarkerOptions().position(getLatLng(it.address.toString())).title(it.subject).snippet(it.body)))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }

    fun getLatLng(address: String): LatLng {
        val coder = Geocoder(this)
        var add: MutableList<Address> = arrayListOf()
        var result: LatLng = LatLng(0.0, 0.0)

        try {
            add = coder.getFromLocationName(address, 5)
            if (address == null) {
                return result
            }
            val location: Address = add.get(0)
            location.latitude
            location.longitude

            result = LatLng(location.latitude, location.longitude)
        } finally {
            return result
        }

    }

}
