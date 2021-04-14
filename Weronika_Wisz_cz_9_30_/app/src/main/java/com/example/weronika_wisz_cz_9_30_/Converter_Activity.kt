package com.example.weronika_wisz_cz_9_30_

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray

class Converter_Activity : AppCompatActivity() {

    internal lateinit var currencyPicker : NumberPicker
    internal lateinit var directionPicker : NumberPicker
    internal lateinit var firstCurrencyImage : ImageView
    internal lateinit var secondCurrencyImage : ImageView
    internal lateinit var firstCurrencyCode : TextView
    internal lateinit var secondCurrencyCode : TextView
    internal lateinit var inputPriceText : EditText
    internal lateinit var convertedPrice : TextView
    internal lateinit var convertButton : Button
    internal var dataSet: Array<CurrencyDetails> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_converter_)
        currencyPicker = findViewById(R.id.currencyPicker)
        directionPicker = findViewById(R.id.directionPicker)
        firstCurrencyImage = findViewById(R.id.firstCurrencyImageView)
        secondCurrencyImage = findViewById(R.id.secondCurrencyImageView)
        firstCurrencyCode = findViewById(R.id.firstCurrencyCode)
        secondCurrencyCode = findViewById(R.id.secondCurrencyCode)
        inputPriceText = findViewById(R.id.inputPriceToConvert)
        convertedPrice = findViewById(R.id.priceToDisplay)
        convertButton = findViewById(R.id.calculateButton)
        makeRequest("A")
    }

    private fun conversionDirectionChange(newVal: Int) {
        if(newVal == 0){
            firstCurrencyCode.text = "PLN"
            firstCurrencyImage.setImageResource(DataHolder.getFlagForCurrency("PLN"))
            secondCurrencyCode.text = dataSet[currencyPicker.value].currencyCode
            secondCurrencyImage.setImageResource(dataSet[currencyPicker.value].flag)
        }else{
            firstCurrencyCode.text = dataSet[currencyPicker.value].currencyCode
            firstCurrencyImage.setImageResource(dataSet[currencyPicker.value].flag)
            secondCurrencyCode.text = "PLN"
            secondCurrencyImage.setImageResource(DataHolder.getFlagForCurrency("PLN"))
        }
        convertedPrice.text = "0.0"
    }

    private fun currencyCodeChange(newVal: Int) {
        if(directionPicker.value == 0){
            secondCurrencyCode.text = dataSet[newVal].currencyCode
            secondCurrencyImage.setImageResource(dataSet[newVal].flag)
        }else{
            firstCurrencyCode.text = dataSet[newVal].currencyCode
            firstCurrencyImage.setImageResource(dataSet[newVal].flag)
        }
        convertedPrice.text = "0.0"
    }

    fun makeRequest(tableName: String){
        val queue = DataHolder.getQueue()

        val url = "http://api.nbp.pl/api/exchangerates/tables/%s/last/2?format=json".format(tableName)
        val currenciesListRequest = JsonArrayRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    print("SUCCESS")
                    loadData(response, tableName)
                    if(tableName == "A"){
                        makeRequest("B")
                    }
                    if(tableName == "B"){
                        initializePickers()
                    }
                },
                Response.ErrorListener {
                    print("ERROR")
                })
        queue.add(currenciesListRequest)
    }

    private fun initializePickers() {
        currencyPicker.minValue = 0
        currencyPicker.maxValue = dataSet.size - 1
        currencyPicker.wrapSelectorWheel = true
        currencyPicker.displayedValues = dataSet.map{it.currencyCode}.toTypedArray()
        directionPicker.minValue = 0
        directionPicker.maxValue = 1
        directionPicker.displayedValues = arrayOf("PLN -> INNA", "INNA -> PLN")
        if(directionPicker.value == 0){
            firstCurrencyCode.text = "PLN"
            firstCurrencyImage.setImageResource(DataHolder.getFlagForCurrency("PLN"))
            secondCurrencyCode.text = dataSet[currencyPicker.value].currencyCode
            secondCurrencyImage.setImageResource(dataSet[currencyPicker.value].flag)
        }else{
            secondCurrencyCode.text = "PLN"
            secondCurrencyImage.setImageResource(DataHolder.getFlagForCurrency("PLN"))
            firstCurrencyCode.text = dataSet[currencyPicker.value].currencyCode
            firstCurrencyImage.setImageResource(dataSet[currencyPicker.value].flag)
        }
        currencyPicker.setOnValueChangedListener{picker, oldVal, newVal -> currencyCodeChange(newVal) }
        directionPicker.setOnValueChangedListener{picker, oldVal, newVal -> conversionDirectionChange(newVal)}
        convertButton.setOnClickListener{convertValue()}
    }

    private fun convertValue() {
        val inputValue = inputPriceText.text
        if(inputValue.toString().matches("[1-9]+\\d*\\.?\\d*$".toRegex()) || inputValue.toString().matches("0\\.?\\d*".toRegex())){
            if(directionPicker.value == 0){
                if(inputValue.toString().matches("0*\\.?0*".toRegex())){
                    convertedPrice.text = "0.0"
                }else{
                    convertedPrice.text = (inputValue.toString().toDouble() / dataSet[currencyPicker.value].rate).toString()
                }
            }else{
                convertedPrice.text = (inputValue.toString().toDouble() * dataSet[currencyPicker.value].rate).toString()
            }
        } else{
            val message = "Błędnie wpisana kwota"
            Toast.makeText(this@Converter_Activity, message, Toast.LENGTH_LONG).show()
            convertedPrice.text = "0.0"
        }
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
            this.dataSet += tmpData as Array<CurrencyDetails>
        }
    }
}