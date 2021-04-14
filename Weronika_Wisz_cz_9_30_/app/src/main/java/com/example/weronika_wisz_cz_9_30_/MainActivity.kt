package com.example.weronika_wisz_cz_9_30_

/* Weronika Wisz, grupa: czwartek 9:30
Wszystkie podpunkty zostały wykonane */

import android.app.VoiceInteractor
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    internal lateinit var buttonCurRate: Button
    internal lateinit var buttonConverter: Button
    internal lateinit var buttonGold: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonCurRate = findViewById(R.id.buttonCurRates)
        buttonConverter = findViewById(R.id.buttonConverter)
        buttonGold = findViewById(R.id.buttonGold)
        DataHolder.prepare(applicationContext)
        buttonCurRate.setOnClickListener{buttonPressed("CurRate")}
        buttonConverter.setOnClickListener{buttonPressed("Converter")}
        buttonGold.setOnClickListener{buttonPressed("Gold")}
    }

    private fun buttonPressed(s: String) {
        if(s=="Converter"){
            val intent = Intent(this, Converter_Activity::class.java).apply {
            }
            startActivity(intent)
        }else if(s=="Gold"){
            val intent = Intent(this, Gold_Activity::class.java).apply {
            }
            startActivity(intent)
        }else{
            val intent = Intent(this, First_Activity::class.java).apply {
            }
            startActivity(intent)
        }
    }
}