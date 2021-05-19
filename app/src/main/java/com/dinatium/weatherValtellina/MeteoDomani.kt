@file:Suppress("DEPRECATION")

package com.dinatium.weatherValtellina

import android.annotation.SuppressLint
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

const val INT_AD_UNIT_ID = "ca-app-pub-9907554154077581/9651981709"

@Suppress("SpellCheckingInspection", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MeteoDomani : AppCompatActivity() {

    /* Inizializzazione città e codice api */
    var place: String? = "Sondrio"
    var meteoIcon: Array<Bitmap?> = arrayOfNulls(7)
    private val wA = WeatherApi()
    val apiKey: String = wA.getApi()

    /* Ad After back pressed */
    private var mInterstitialAd: InterstitialAd? = null
    private val TAG = "MeteoApp"
    private var mAdIsLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meteo_domani)

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

        WeatherTomorrowTask().execute()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}

        if (!mAdIsLoading && mInterstitialAd == null) {
            mAdIsLoading = true
            loadAd()
        } else {
            showInterstitial()
        }

        showInterstitial()


    }

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this, INT_AD_UNIT_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    mInterstitialAd = null
                    mAdIsLoading = false
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mAdIsLoading = false
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
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class WeatherTomorrowTask : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()

            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.caricamento).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.meteoContainer2).visibility = View.GONE
            findViewById<TextView>(R.id.errore).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response: String?

            val defaultPlace = ""
            val currentPlace = place ?: defaultPlace

            try {
                response = URL(
                    when (currentPlace) {
                        "Valfurva" -> {
                            "https://api.openweathermap.org/data/2.5/onecall?lat=46.414303131132186&lon=10.491103526898536&exclude=current,minutely,hourly,alerts&appid=$apiKey&lang=it&units=metric"
                        }
                        "Bormio" -> {
                            "https://api.openweathermap.org/data/2.5/onecall?lat=46.4684&lon=10.3721&exclude=current,minutely,hourly,alerts&appid=$apiKey&lang=it&units=metric"
                        }
                        "Valdidentro" -> {
                            "https://api.openweathermap.org/data/2.5/onecall?lat=46.4833&lon=10.2833&exclude=current,minutely,hourly,alerts&appid=$apiKey&lang=it&units=metric"
                        }
                        "Valdisotto" -> {
                            "https://api.openweathermap.org/data/2.5/onecall?lat=46.4344&lon=10.357&exclude=current,minutely,hourly,alerts&appid=$apiKey&lang=it&units=metric"
                        }
                        "Sondalo" -> {
                            "https://api.openweathermap.org/data/2.5/onecall?lat=46.3301&lon=10.3248&exclude=current,minutely,hourly,alerts&appid=$apiKey&lang=it&units=metric"
                        }
                        "Livigno" -> {
                            "https://api.openweathermap.org/data/2.5/onecall?lat=46.5347&lon=10.1337&exclude=current,minutely,hourly,alerts&appid=$apiKey&lang=it&units=metric"
                        }
                        else -> {
                            "https://api.openweathermap.org/data/2.5/onecall?lat=46.169&lon=9.8692&exclude=current,minutely,hourly,alerts&appid=$apiKey&lang=it&units=metric"
                        }
                    }
                ).readText(Charsets.UTF_8)
            } catch (e: Exception) {
                response = null
                e.printStackTrace()
            }

            for (x in 0..6) {
                val icon = JSONObject(response).getJSONArray("daily").getJSONObject(x)
                    .getJSONArray("weather").getJSONObject(0).getString("icon")
                val urldisplay = "https://openweathermap.org/img/wn/$icon@2x.png"
                try {
                    val `in` = URL(urldisplay).openStream()
                    meteoIcon[x] = BitmapFactory.decodeStream(`in`)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            return response
        }

        @SuppressLint("SetTextI18n", "CutPasteId")
        override fun onPostExecute(result: String?) {
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)

                val dayString: Array<String?> = arrayOfNulls(7)
                val sunriseString: Array<String?> = arrayOfNulls(7)
                val sunsetString: Array<String?> = arrayOfNulls(7)

                val temp = IntArray(7)
                val fellsLike = IntArray(7)

                val weather: Array<String?> = arrayOfNulls(7)

                val rainfall = IntArray(7)

                for (x in 0..6) {
                    dayString[x] = SimpleDateFormat(
                        "EEEE dd/MM/yyyy",
                        Locale.ITALIAN
                    ).format(
                        Date(
                            jsonObj.getJSONArray("daily").getJSONObject(x).getLong("dt") * 1000
                        )
                    )
                    sunriseString[x] = SimpleDateFormat(
                        "HH:mm",
                        Locale.ITALIAN
                    ).format(
                        Date(
                            jsonObj.getJSONArray("daily").getJSONObject(x).getLong("sunrise") * 1000
                        )
                    )
                    sunsetString[x] = SimpleDateFormat(
                        "HH:mm",
                        Locale.ITALIAN
                    ).format(
                        Date(
                            jsonObj.getJSONArray("daily").getJSONObject(x).getLong("sunset") * 1000
                        )
                    )

                    temp[x] = jsonObj.getJSONArray("daily").getJSONObject(x).getJSONObject("temp")
                        .getInt("day")
                    fellsLike[x] =
                        jsonObj.getJSONArray("daily").getJSONObject(x).getJSONObject("feels_like")
                            .getInt("day")

                    weather[x] =
                        jsonObj.getJSONArray("daily").getJSONObject(x).getJSONArray("weather")
                            .getJSONObject(0).getString("description")

                    var rain = 0
                    try {
                        rain = jsonObj.getJSONArray("daily").getJSONObject(x).getInt("rain")
                    } catch (e: Exception) {
                        try {
                            rain = jsonObj.getJSONArray("daily").getJSONObject(x).getInt("snow")
                        } catch (e: Exception) {
                        }
                    }

                    rainfall[x] = rain
                }

                /*Inserimento nella grafica first day*/
                findViewById<TextView>(R.id.place).text = "$place, IT \uD83C\uDDEE\uD83C\uDDF9"

                findViewById<TextView>(R.id.day0).text = dayString[0]
                findViewById<TextView>(R.id.weather0).text = weather[0]
                findViewById<TextView>(R.id.temperature0).text = temp[0].toString() + "°C"
                findViewById<TextView>(R.id.feelsLike0).text = fellsLike[0].toString() + "°C"
                findViewById<TextView>(R.id.sunrise0).text = sunriseString[0]
                findViewById<TextView>(R.id.sunset0).text = sunsetString[0]
                findViewById<TextView>(R.id.rainfall0).text = rainfall[0].toString() + " mm"
                findViewById<ImageView>(R.id.wIcon0).setImageBitmap(meteoIcon[0])

                /*Second day*/
                findViewById<TextView>(R.id.day1).text = dayString[1]
                findViewById<TextView>(R.id.weather1).text = weather[1]
                findViewById<TextView>(R.id.temperature1).text = temp[1].toString() + "°C"
                findViewById<TextView>(R.id.feelsLike1).text = fellsLike[1].toString() + "°C"
                findViewById<TextView>(R.id.sunrise1).text = sunriseString[1]
                findViewById<TextView>(R.id.sunset1).text = sunsetString[1]
                findViewById<TextView>(R.id.rainfall1).text = rainfall[1].toString() + " mm"
                findViewById<ImageView>(R.id.wIcon1).setImageBitmap(meteoIcon[1])

                /*Third day*/
                findViewById<TextView>(R.id.day2).text = dayString[2]
                findViewById<TextView>(R.id.weather2).text = weather[2]
                findViewById<TextView>(R.id.temperature2).text = temp[2].toString() + "°C"
                findViewById<TextView>(R.id.feelsLike2).text = fellsLike[2].toString() + "°C"
                findViewById<TextView>(R.id.sunrise2).text = sunriseString[2]
                findViewById<TextView>(R.id.sunset2).text = sunsetString[2]
                findViewById<TextView>(R.id.rainfall2).text = rainfall[2].toString() + " mm"
                findViewById<ImageView>(R.id.wIcon2).setImageBitmap(meteoIcon[2])

                /*Fourth day*/
                findViewById<TextView>(R.id.day3).text = dayString[3]
                findViewById<TextView>(R.id.weather3).text = weather[3]
                findViewById<TextView>(R.id.temperature3).text = temp[3].toString() + "°C"
                findViewById<TextView>(R.id.feelsLike3).text = fellsLike[3].toString() + "°C"
                findViewById<TextView>(R.id.sunrise3).text = sunriseString[3]
                findViewById<TextView>(R.id.sunset3).text = sunsetString[3]
                findViewById<TextView>(R.id.rainfall3).text = rainfall[3].toString() + " mm"
                findViewById<ImageView>(R.id.wIcon3).setImageBitmap(meteoIcon[3])

                /*Fifth day*/
                findViewById<TextView>(R.id.day4).text = dayString[4]
                findViewById<TextView>(R.id.weather4).text = weather[4]
                findViewById<TextView>(R.id.temperature4).text = temp[4].toString() + "°C"
                findViewById<TextView>(R.id.feelsLike4).text = fellsLike[4].toString() + "°C"
                findViewById<TextView>(R.id.sunrise4).text = sunriseString[4]
                findViewById<TextView>(R.id.sunset4).text = sunsetString[4]
                findViewById<TextView>(R.id.rainfall4).text = rainfall[4].toString() + " mm"
                findViewById<ImageView>(R.id.wIcon4).setImageBitmap(meteoIcon[4])

                /*Sixth day*/
                findViewById<TextView>(R.id.day5).text = dayString[5]
                findViewById<TextView>(R.id.weather5).text = weather[5]
                findViewById<TextView>(R.id.temperature5).text = temp[5].toString() + "°C"
                findViewById<TextView>(R.id.feelsLike5).text = fellsLike[5].toString() + "°C"
                findViewById<TextView>(R.id.sunrise5).text = sunriseString[5]
                findViewById<TextView>(R.id.sunset5).text = sunsetString[5]
                findViewById<TextView>(R.id.rainfall5).text = rainfall[5].toString() + " mm"
                findViewById<ImageView>(R.id.wIcon5).setImageBitmap(meteoIcon[5])

                /*Seventh day*/
                findViewById<TextView>(R.id.day6).text = dayString[6]
                findViewById<TextView>(R.id.weather6).text = weather[6]
                findViewById<TextView>(R.id.temperature6).text = temp[6].toString() + "°C"
                findViewById<TextView>(R.id.feelsLike6).text = fellsLike[6].toString() + "°C"
                findViewById<TextView>(R.id.sunrise6).text = sunriseString[6]
                findViewById<TextView>(R.id.sunset6).text = sunsetString[6]
                findViewById<TextView>(R.id.rainfall6).text = rainfall[6].toString() + " mm"
                findViewById<ImageView>(R.id.wIcon6).setImageBitmap(meteoIcon[6])


                findViewById<ProgressBar>(R.id.caricamento).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.meteoContainer2).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.caricamento).visibility = View.GONE
                findViewById<TextView>(R.id.errore).visibility = View.VISIBLE

                e.printStackTrace()

                val eventopulsante: Button = findViewById(R.id.errore)
                eventopulsante.setOnClickListener {
                    WeatherTomorrowTask().execute()
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

