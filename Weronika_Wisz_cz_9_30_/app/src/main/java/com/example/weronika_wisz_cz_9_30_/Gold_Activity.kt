package com.example.weronika_wisz_cz_9_30_

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.json.JSONArray
import org.json.JSONObject

class Gold_Activity : AppCompatActivity() {
    internal lateinit var todayGoldRate: TextView
    internal lateinit var lineChart: LineChart
    internal lateinit var arrowGold: TextView
    internal lateinit var historicGoldRates: Array<Pair<String,Double>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gold_)

        todayGoldRate = findViewById(R.id.todayGoldRate)
        arrowGold = findViewById(R.id.arrowGold)
        lineChart = findViewById(R.id.historicGoldChart)

        getData()

    }

    private fun getData() {
        val queue = DataHolder.getQueue()
        val url = "http://api.nbp.pl/api/cenyzlota/last/30"

        val historicGoldRequest = JsonArrayRequest( Request.Method.GET, url, null,
                Response.Listener { response ->
                    println("Success!")
                    loadDetails(response)
                    setDetails()
                },
                Response.ErrorListener {  }
        )
        queue.add(historicGoldRequest)
    }

    private fun setDetails() {
        if(historicGoldRates.last().second < historicGoldRates[historicGoldRates.count() - 2].second){
            arrowGold.text = "⬇"
            arrowGold.setTextColor("#ff0000".toColorInt())
        }else{
            arrowGold.text = "⬆"
            arrowGold.setTextColor("#008000".toColorInt())
        }
        todayGoldRate.text = getString(R.string.todayGoldText, historicGoldRates.last().second)
        var entries = ArrayList<Entry>()

        for((index, element) in historicGoldRates.withIndex()){
            entries.add(Entry(index.toFloat(), element.second.toFloat()))
        }

        val entriesDataSet = LineDataSet(entries, "Kurs")
        entriesDataSet.lineWidth = 3f
        entriesDataSet.circleRadius = 4f
        entriesDataSet.color = R.color.purple_500
        entriesDataSet.setDrawValues(false)


        val lineData = LineData(entriesDataSet)

        lineChart.data = lineData
        val description = Description()
        description.text = "Kurs złota za 1g z ostatnich 30 dni"
        lineChart.description = description
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(historicGoldRates.map{it.first.substring(8)+"."+it.first.substring(5,7)}.toTypedArray())
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.axisRight.isEnabled = false

        lineChart.invalidate()
    }

    private fun loadDetails(response: JSONArray?) {
        response?.let{
            val goldCount = response.length()

            val tmpData = arrayOfNulls<Pair<String, Double>>(goldCount)

            for(i in 0 until goldCount){
                val date = response.getJSONObject(i).getString("data")
                val price = response.getJSONObject(i).getDouble("cena")
                tmpData[i] = Pair(date, price)
            }
            this.historicGoldRates = tmpData as Array<Pair<String, Double>>
        }
    }
}