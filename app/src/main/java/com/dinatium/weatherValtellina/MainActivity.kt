package com.dinatium.weatherValtellina

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var bormioEvent: Button = findViewById(R.id.bormioButton)
        bormioEvent.setOnClickListener {
            val i = Intent(applicationContext, MeteoActivity::class.java)
            i.putExtra("city", "Bormio")
            startActivity(i)
        }

        var valfurvaEvent: Button = findViewById(R.id.valfurvaButton)
        valfurvaEvent.setOnClickListener {
            val i = Intent(applicationContext, MeteoActivity::class.java)
            i.putExtra("city", "Valfurva")
            startActivity(i)
        }

        var valdisottoEvent: Button = findViewById(R.id.valdisottoButton)
        valdisottoEvent.setOnClickListener {
            val i = Intent(applicationContext, MeteoActivity::class.java)
            i.putExtra("city", "Valdisotto")
            startActivity(i)
        }

        var valdidentroEvent: Button = findViewById(R.id.valdidentroButton)
        valdidentroEvent.setOnClickListener {
            val i = Intent(applicationContext, MeteoActivity::class.java)
            i.putExtra("city", "Valdidentro")
            startActivity(i)
        }

        var sondaloEvent: Button = findViewById(R.id.sondaloButton)
        sondaloEvent.setOnClickListener {
            val i = Intent(applicationContext, MeteoActivity::class.java)
            i.putExtra("city", "Sondalo")
            startActivity(i)
        }


        var SondrioEvent: Button = findViewById(R.id.sondrioButton)
        SondrioEvent.setOnClickListener {
            val i = Intent(applicationContext, MeteoActivity::class.java)
            i.putExtra("city", "Sondrio")
            startActivity(i)
        }

        var livignoEvent: Button = findViewById(R.id.livignoButton)
        livignoEvent.setOnClickListener {
            val i = Intent(applicationContext, MeteoActivity::class.java)
            i.putExtra("city", "Livigno")
            startActivity(i)
        }
    }
}