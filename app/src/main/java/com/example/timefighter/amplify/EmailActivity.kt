package com.example.timefighter.amplify

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EmailActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var addressExtra: String

    override fun onCreate(savedInstanceState: Bundle?) {
        addressExtra = intent.extras?.get("address") as String
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        database = Firebase.database.reference
    }

    fun makeToast(View: View?) {
        Toast.makeText(this, "Sent!", Toast.LENGTH_LONG).show()
        writeNewSubmission()
    }

    @IgnoreExtraProperties
    data class Submission(
        var address: String? = "",
        var subject: String? = "",
        var body: String? = ""
    )

    private fun writeNewSubmission() {
        val sub = Submission(addressExtra, findViewById<EditText>(R.id.subjectEditText).text.toString(), findViewById<EditText>(R.id.editText).text.toString())
        database.child("submissions").child(addressExtra).setValue(sub)
    }

}
