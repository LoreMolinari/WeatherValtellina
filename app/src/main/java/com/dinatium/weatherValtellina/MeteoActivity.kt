package com.dinatium.weatherValtellina

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

const val INT_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MeteoActivity : AppCompatActivity() {

    /*Inizializzazione città e codice api*/
    var place: String? = "Sondrio"
    var meteoIcon: Bitmap? = null
    private val wA = WeatherApi()
    val apiKey: String = wA.getApi()

    /* Ad After back pressed */
    private var mInterstitialAd: InterstitialAd? = null
    private var TAG = "MeteoSettimana"
    private var mAdIsLoading: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meteo)

        loadAd()

        val mAdView = findViewById<AdView>(R.id.adView)

        MobileAds.initialize(this) {}

        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        val adView = AdView(this)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-9907554154077581/5165075814"

        mAdView.loadAd(adRequest)

        val extras = intent.extras
        if (extras != null) {
            place = extras.getString("city").toString()
        }

        WeatherTask().execute()

        val domaniButton: Button = findViewById(R.id.meteoDomani)
        domaniButton.setOnClickListener {
            val i = Intent(applicationContext, MeteoDomani::class.java)
            i.putExtra("city", place)
            startActivity(i)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}

        if (!mAdIsLoading && mInterstitialAd == null) {
            mAdIsLoading = true
            loadAd()
        }else{
            showInterstitial()
        }

        showInterstitial()


    }

    private fun loadAd() {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this, INT_AD_UNIT_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError?.message)
                    mInterstitialAd = null
                    mAdIsLoading = false
                    val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                            "message: ${adError.message}"
                    Toast.makeText(
                        this@MeteoActivity,
                        "onAdFailedToLoad() with error $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mAdIsLoading = false
                    Toast.makeText(this@MeteoActivity, "onAdLoaded()", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Log.d(TAG, "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                    // Called when ad is dismissed.
                }
            }
            mInterstitialAd?.show(this)
        } else {
            Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
            showInterstitial()
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
                    URL("https://api.openweathermap.org/data/2.5/weather?lat=46.414303131132186&lon=10.491103526898536&appid=$apiKey&lang=it&units=metric").readText(
                            Charsets.UTF_8
                    )
                } else {
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$place&appid=$apiKey&lang=it&units=metric").readText(
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
                meteoIcon = BitmapFactory.decodeStream(`in`)
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
                val name = jsonObj.getString("name") + ", " + sys.getString("country") + " \uD83C\uDDEE\uD83C\uDDF9"
                val cloud = jsonObj.getJSONObject("clouds").getString("all") + "%"
                /* Populating extracted data into our views */
                if (place.toString().equals("Valfurva")) {
                    findViewById<TextView>(R.id.place).text =
                            "$place, IT \uD83C\uDDEE\uD83C\uDDF9"
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
                findViewById<ImageView>(R.id.wIcon).setImageBitmap(meteoIcon)

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

    // Called when leaving the activity
    public override fun onPause() {
        adView.pause()
        super.onPause()
    }

    // Called when returning to the activity
    public override fun onResume() {
        super.onResume()
        adView.resume()
    }

    // Called before the activity is destroyed
    public override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }
}
