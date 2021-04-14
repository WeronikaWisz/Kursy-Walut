package com.example.weronika_wisz_cz_9_30_

import android.app.VoiceInteractor
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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

class First_Activity : AppCompatActivity() {
    internal lateinit var currenciesListRecyclerView: RecyclerView
    internal lateinit var adapter: CurrenciesListAdapter
    internal lateinit var dataSet: Array<CurrencyDetails>
    internal var dataSetA: Array<CurrencyDetails> = emptyArray()
    internal var dataSetB: Array<CurrencyDetails> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_)
        currenciesListRecyclerView = findViewById(R.id.currenciesListRescyclerView)
        currenciesListRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CurrenciesListAdapter(emptyArray(), this)
        currenciesListRecyclerView.adapter = adapter
        makeRequest("A")
        makeRequest("B")
    }

    fun makeRequest(tableName: String){
        val queue = DataHolder.getQueue()

        val url = "http://api.nbp.pl/api/exchangerates/tables/%s/last/2?format=json".format(tableName)
        val currenciesListRequest = JsonArrayRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    print("SUCCESS")
                    loadData(response, tableName)
                    dataSet = dataSetA + dataSetB
                    adapter.dataSet = dataSet
                    adapter.notifyDataSetChanged()
                },
                Response.ErrorListener {
                    print("ERROR")
                })
        queue.add(currenciesListRequest)
    }

    private fun loadData(response: JSONArray?, tableName: String) {
        response?.let{
            val rates = response.getJSONObject(1).getJSONArray("rates")
            val ratesCount = rates.length()
            val tmpData = arrayOfNulls<CurrencyDetails>(ratesCount)

            for(i in 0 until ratesCount){
                val currencyCode = rates.getJSONObject(i).getString("code")
                val currentRate = rates.getJSONObject(i).getDouble("mid")
                val flag = DataHolder.getFlagForCurrency(currencyCode)
                var increase = true
                if(currentRate < response.getJSONObject(0).getJSONArray("rates").getJSONObject(i).getDouble("mid")){
                    increase = false
                }
                val currencyObject = CurrencyDetails(currencyCode, currentRate, flag, tableName, increase)

                tmpData[i] = currencyObject
            }
            if(tableName == "A"){
                this.dataSetA = tmpData as Array<CurrencyDetails>
            }else{
                this.dataSetB = tmpData as Array<CurrencyDetails>
            }
        }
    }
}