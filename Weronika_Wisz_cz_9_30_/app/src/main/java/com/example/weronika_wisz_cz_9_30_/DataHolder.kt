package com.example.weronika_wisz_cz_9_30_

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley.newRequestQueue
import com.blongho.country_data.Country
import com.blongho.country_data.World
import org.json.JSONArray

object DataHolder {
//    private var data: Array<CurrencyDetails>? = null
    private lateinit var queue: RequestQueue
    private lateinit var countries: List<Country>

    fun prepare(context: Context){
        queue = newRequestQueue(context)
        World.init(context)
        countries = World.getAllCountries().distinctBy { it.currency.code }
    }

    fun getFlagForCurrency(currencyCode: String): Int{
        if(currencyCode == "USD"){
            return R.drawable.us
        } else if (currencyCode == "GBP"){
            return R.drawable.gb
        } else if (currencyCode == "CHF"){
            return R.drawable.ch
        } else if (currencyCode == "EUR"){
            return R.drawable.eu
        } else if (currencyCode == "HKD"){
            return R.drawable.hk
        } else{
            return countries.find { it.currency.code == currencyCode }?.flagResource
                ?: World.getWorldFlag()
        }
    }

    fun getQueue():RequestQueue{
        return queue
    }

}