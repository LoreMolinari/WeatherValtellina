package com.dinatium.weatherValtellina

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MeteoActivity : AppCompatActivity() {

    /*Inizializzazione città e codice api*/
    var place: String? = "Sondrio"
    var MeteoIcon: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meteo)

        val extras = intent.extras
        if (extras != null) {
            place = extras.getString("city").toString()
        }

        WeatherTask().execute()

        val meteoDomani: Button = findViewById(R.id.meteoDomani)
        meteoDomani.setOnClickListener {
            val i = Intent(applicationContext, MeteoDomani::class.java)
            i.putExtra("city", place)
            startActivity(i)
        }
    }


    @SuppressLint("StaticFieldLeak")
    inner class WeatherTask : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.caricamento).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.meteoContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errore).visibility = View.GONE
        }

        @SuppressLint("CutPasteId")
        override fun doInBackground(vararg params: String?): String? {
            val response = try {
                if (place.toString() == "Valfurva") {
                    URL("https://api.openweathermap.org/data/2.5/weather?lat=46.414303131132186&lon=10.491103526898536&appid=bad9662e97abd6a3ab5f9dbd245f943c&lang=it&units=metric").readText(
                        Charsets.UTF_8
                    )
                } else {
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$place&appid=bad9662e97abd6a3ab5f9dbd245f943c&lang=it&units=metric").readText(
                        Charsets.UTF_8
                    )
                }
            } catch (e: Exception) {
                null
            }

            val icon = JSONObject(response)
                .getJSONArray("weather")
                .getJSONObject(0)
                .getString("icon")

            val urldisplay = "https://openweathermap.org/img/wn/$icon@2x.png"

            try {
                val `in` = URL(urldisplay).openStream()
                MeteoIcon = BitmapFactory.decodeStream(`in`)
            } catch (e: java.lang.Exception) {
                findViewById<TextView>(R.id.errore).text = e.printStackTrace().toString()
                findViewById<TextView>(R.id.errore).visibility = View.VISIBLE
                e.printStackTrace()
            }

            return response
        }

        @SuppressLint("SetTextI18n", "DefaultLocale", "CutPasteId")
        override fun onPostExecute(result: String?) {
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val temp = "" + main.getInt("temp") + "°C"
                val tempMin = "Min Temp: " + main.getInt("temp_min") + "°C"
                val tempMax = "Max Temp: " + main.getInt("temp_max") + "°C"
                val percepita = "Temp Percepita: " + main.getInt("feels_like") + "°C"
                val pressure = main.getString("pressure") + " hPa"
                val humidity = main.getString("humidity") + "%"
                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed") + " m/s"
                val weatherDescription = weather.getString("description")
                val name =
                    jsonObj.getString("name") + ", " + sys.getString("country") + " \uD83C\uDDEE\uD83C\uDDF9"
                val cloud = jsonObj.getJSONObject("clouds").getString("all") + "%"
                /* Populating extracted data into our views */
                if (place.toString().equals("Valfurva")) {
                    findViewById<TextView>(R.id.place).text =
                        place + ", IT \uD83C\uDDEE\uD83C\uDDF9"
                } else {
                    findViewById<TextView>(R.id.place).text = name
                }
                findViewById<TextView>(R.id.weather).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temperature).text = temp
                findViewById<TextView>(R.id.temperatureMin).text = tempMin
                findViewById<TextView>(R.id.temperatureMax).text = tempMax
                findViewById<TextView>(R.id.temperaturePer).text = percepita
                findViewById<TextView>(R.id.sunrise).text =
                    SimpleDateFormat("HH:mm", Locale.ITALIAN).format(Date(sunrise * 1000))
                findViewById<TextView>(R.id.sunset).text =
                    SimpleDateFormat("HH:mm", Locale.ITALIAN).format(Date(sunset * 1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity
                findViewById<TextView>(R.id.cloud).text = cloud
                findViewById<ImageView>(R.id.wIcon).setImageBitmap(MeteoIcon)

                findViewById<ProgressBar>(R.id.caricamento).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.meteoContainer).visibility = View.VISIBLE
            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.caricamento).visibility = View.GONE
                findViewById<TextView>(R.id.errore).append("/n" + e.printStackTrace().toString())
                findViewById<TextView>(R.id.errore).visibility = View.VISIBLE

                val eventopulsante: Button = findViewById(R.id.errore)
                eventopulsante.setOnClickListener {
                    WeatherTask().execute()
                }
            }
        }
    }
}
