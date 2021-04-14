package com.example.weronika_wisz_cz_9_30_

import android.app.VoiceInteractor
import android.media.audiofx.AudioEffect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.json.JSONObject

class Second_Activity : AppCompatActivity() {
    internal lateinit var todayRate: TextView
    internal lateinit var yesterdayRate: TextView
    internal lateinit var currencyCodeText : TextView
    internal lateinit var lineChart: LineChart
    internal lateinit var lineChart7: LineChart
//    internal lateinit var currency: CurrencyDetails
    internal lateinit var currencyCode: String
    internal lateinit var currencyTableName: String
    internal lateinit var historicRates: Array<Pair<String,Double>>
    internal lateinit var historicRates7: Array<Pair<String,Double>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_)

        todayRate = findViewById(R.id.todayRate)
        yesterdayRate = findViewById(R.id.yesterdayRate)
        currencyCodeText = findViewById(R.id.currencyDetailsText)
        lineChart = findViewById(R.id.historicRatesChart)
        lineChart7 = findViewById(R.id.historicRatesChart7)

        currencyCode = intent.getStringExtra("currencyCode") ?: "USD"

        currencyTableName = intent.getStringExtra("currencyTableName") ?: "A"


        getData()

    }

    private fun getData() {
        val queue = DataHolder.getQueue()
        val url = "http://api.nbp.pl/api/exchangerates/rates/%s/%s/last/30/".format(currencyTableName, currencyCode)

        val historicRatesRequest = JsonObjectRequest( Request.Method.GET, url, null,
            Response.Listener { response ->
                println("Success!")
                loadDetails(response)
                setDetails()
                setDetails7()
            },
            Response.ErrorListener {  }
        )
        queue.add(historicRatesRequest)
    }

    private fun setDetails7() {

        var entries = ArrayList<Entry>()

        for((index, element) in historicRates7.withIndex()){
            entries.add(Entry(index.toFloat(), element.second.toFloat()))
        }

        val entriesDataSet = LineDataSet(entries, "Kurs")
        entriesDataSet.lineWidth = 3f
        entriesDataSet.circleRadius = 4f
        entriesDataSet.color = R.color.purple_500
        entriesDataSet.setDrawValues(false)

        val lineData = LineData(entriesDataSet)

        lineChart7.data = lineData
        val description = Description()
        description.text = "Kurs %s z ostatnich 7 dni".format(currencyCode)
        lineChart7.description = description
        lineChart7.xAxis.valueFormatter = IndexAxisValueFormatter(historicRates7.map{it.first.substring(8)+"."+it.first.substring(5,7)}.toTypedArray())
        lineChart7.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart7.axisRight.isEnabled = false

        lineChart7.invalidate()
    }

    private fun setDetails() {
        todayRate.text = getString(R.string.todayRateText, historicRates.last().second)
        yesterdayRate.text = getString(R.string.yesterdayRateText, historicRates[historicRates.size - 2].second)
        currencyCodeText.text = currencyCode

        var entries = ArrayList<Entry>()

        for((index, element) in historicRates.withIndex()){
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
        description.text = "Kurs %s z ostatnich 30 dni".format(currencyCode)
        lineChart.description = description
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(historicRates.map{it.first.substring(8)+"."+it.first.substring(5,7)}.toTypedArray())
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.axisRight.isEnabled = false

        lineChart.invalidate()
    }

    private fun loadDetails(response: JSONObject?) {
        response?.let{
            val rates = response.getJSONArray("rates")
            val ratesCount = rates.length()
            val tmpData = arrayOfNulls<Pair<String, Double>>(ratesCount)

            for(i in 0 until ratesCount){
                val date = rates.getJSONObject(i).getString("effectiveDate")
                val currencyRate = rates.getJSONObject(i).getDouble("mid")
                tmpData[i] = Pair(date, currencyRate)
            }

            this.historicRates = tmpData as Array<Pair<String, Double>>
            this.historicRates7 = historicRates.sliceArray(23..29)
        }
    }
}