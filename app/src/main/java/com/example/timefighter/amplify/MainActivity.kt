package com.example.timefighter.amplify

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun launchFindReps(view: View?) {
        this.startActivity(Intent(this, FindReps::class.java))
    }

    fun launchMap(view: View?) {
        this.startActivity(Intent(this, MapsActivity::class.java))
    }
}
