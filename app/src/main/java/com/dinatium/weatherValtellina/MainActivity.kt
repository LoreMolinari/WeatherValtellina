package com.dinatium.weatherValtellina

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mAdView = findViewById<AdView>(R.id.adView)

        MobileAds.initialize(this) {}

        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        val adView = AdView(this)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-9907554154077581/5165075814"

        mAdView.loadAd(adRequest)


        val bormioEvent: Button = findViewById(R.id.bormioButton)
        bormioEvent.setOnClickListener {
            val i = Intent(applicationContext, MeteoActivity::class.java)
            i.putExtra("city", "Bormio")
            startActivity(i)
        }

        val valfurvaEvent: Button = findViewById(R.id.valfurvaButton)
        valfurvaEvent.setOnClickListener {
            val i = Intent(applicationContext, MeteoActivity::class.java)
            i.putExtra("city", "Valfurva")
            startActivity(i)
        }

        val valdisottoEvent: Button = findViewById(R.id.valdisottoButton)
        valdisottoEvent.setOnClickListener {
            val i = Intent(applicationContext, MeteoActivity::class.java)
            i.putExtra("city", "Valdisotto")
            startActivity(i)
        }

        val valdidentroEvent: Button = findViewById(R.id.valdidentroButton)
        valdidentroEvent.setOnClickListener {
            val i = Intent(applicationContext, MeteoActivity::class.java)
            i.putExtra("city", "Valdidentro")
            startActivity(i)
        }

        val sondaloEvent: Button = findViewById(R.id.sondaloButton)
        sondaloEvent.setOnClickListener {
            val i = Intent(applicationContext, MeteoActivity::class.java)
            i.putExtra("city", "Sondalo")
            startActivity(i)
        }


        val sondrioEvent: Button = findViewById(R.id.sondrioButton)
        sondrioEvent.setOnClickListener {
            val i = Intent(applicationContext, MeteoActivity::class.java)
            i.putExtra("city", "Sondrio")
            startActivity(i)
        }

        val livignoEvent: Button = findViewById(R.id.livignoButton)
        livignoEvent.setOnClickListener {
            val i = Intent(applicationContext, MeteoActivity::class.java)
            i.putExtra("city", "Livigno")
            startActivity(i)
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