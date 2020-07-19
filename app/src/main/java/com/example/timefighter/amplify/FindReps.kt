package com.example.timefighter.amplify

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URL
import java.net.HttpURLConnection;

class FindReps : AppCompatActivity() {
    val API_KEY = "AIzaSyAaZgPMcLjQm5BIsQ4hjXD1_BIwE2YiQno"
    lateinit var address: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_reps)
    }

    fun startSearch(View: View?) {
        address = findViewById<EditText>(R.id.addresstEditText).text.toString()
        RetrieveFeedTask().execute()
    }

    fun startEmailActivity(view: View?) {
        this.startActivity(Intent(this, EmailActivity::class.java).putExtra("address", address))
    }

    data class Channel(
        val type: String,
        val id: String
    )

    data class Representative(
        val name: String,
        val office: String,
        val party: String,
        val phones: List<String>,
        val urls: List<String>,
        val photoUrl: String,
        val emails: List<String>
    )

    inner class RetrieveFeedTask() : AsyncTask<Void, Void, String>() {

        override fun onPreExecute() {
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            findViewById<Button>(R.id.writeButton).visibility = View.VISIBLE
            findViewById<ScrollView>(R.id.scrollView).visibility = View.VISIBLE
            findViewById<TextView>(R.id.responseView).text = ""

        }

        override fun doInBackground(vararg params: Void?): String {
            var formattedAddress = ""
            var result = ""
            address.forEach {
                if (it.equals(' ')) {
                    formattedAddress += "%20"
                } else if (it.equals(',')) {
                    formattedAddress += "%2C"
                } else {
                    formattedAddress += it
                }
            }

            try {
                val url = URL("https://civicinfo.googleapis.com/civicinfo/v2/representatives?address=" + formattedAddress + "&key=" + API_KEY)
                var urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                try {
                    val bufferedReader = BufferedReader(InputStreamReader(urlConnection.getInputStream()))
                    var stringBuilder = StringBuilder()
                    var line = bufferedReader.readLine()
                    while (line != null) {
                        stringBuilder.append(line).append("\n")
                        line = bufferedReader.readLine()
                    }
                    bufferedReader.close()
                    return stringBuilder.toString()
                } finally {
                    urlConnection.disconnect()
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.message, e);
                return "";
            }
        }

        override fun onPostExecute(result: String?) {
            findViewById<Button>(R.id.writeButton).visibility = View.VISIBLE
            var response = result
            var representatives: MutableList<Representative> = arrayListOf()

            if(result == null) {
                response = "THERE WAS AN ERROR"
            }
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE

            var formattedResult = ""
            val resultObject = JSONObject(response) as JSONObject
            val officesArray = resultObject.getJSONArray("offices")
            val officialsArray = resultObject.getJSONArray("officials")

            for (i in 0..(officesArray.length()-1)) {
                val officeObject = officesArray.getJSONObject(i)
                val officeKeys = officeObject.keys()

                while(officeKeys.hasNext()) {
                    val key = officeKeys.next()
                    if (key.equals("officialIndices")) {
                        val officialIndices = officeObject.getJSONArray(key)
                        for (j in 0..(officialIndices.length() - 1)) {
                            val officialObject = officialsArray.getJSONObject(officialIndices.get(j) as Int)
                            var phones: MutableList<String> = arrayListOf()
                            var urls: MutableList<String> = arrayListOf()
                            var emails: MutableList<String> = arrayListOf()

                            val officialObjectKeys = officialObject.keys()
                            while(officialObjectKeys.hasNext()) {
                                val key2 = officialObjectKeys.next()
                                if (key2.equals("phones")) {
                                    val phonesArray = officialObject.getJSONArray("phones")
                                    for (k in 0..(phonesArray.length() - 1)) {
                                        phones.add(phonesArray.get(k) as String)
                                    }
                                }
                                if (key2.equals("emails")) {
                                    val emailsArray = officialObject.getJSONArray("emails")
                                    for (k in 0..(emailsArray.length() - 1)) {
                                        emails.add(emailsArray.get(k) as String)
                                    }
                                }
                                if (key2.equals("urls")) {
                                    val urlsArray = officialObject.getJSONArray("urls")
                                    for (k in 0..(urlsArray.length() - 1)) {
                                        urls.add(urlsArray.get(k) as String)
                                    }
                                }
                            }

                            var photoUrl: String = ""
                            try {
                                photoUrl = officialObject.get("photoUrl") as String
                            } catch (e: Exception) {

                            }

                            val rep = Representative(
                                officialObject.get("name") as String,
                                officeObject.get("name") as String,
                                officialObject.get("party") as String,
                                phones,
                                urls,
                                photoUrl,
                                emails
                            )

                            representatives.add(rep)
                        }

                    }
                }
            }

            representatives.forEach {
                formattedResult += it.name
                formattedResult += "\n"
                formattedResult += it.office
                formattedResult += "\n"
                formattedResult += it.party
                formattedResult += "\n"
                it.emails.forEach {
                    formattedResult += it
                    formattedResult += "\n"
                }
                formattedResult += "\n"
            }

            findViewById<TextView>(R.id.responseView).text = formattedResult
        }



    }
}
