package com.example.weronika_wisz_cz_9_30_

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class CurrenciesListAdapter (var dataSet: Array<CurrencyDetails>, val context: Context) : RecyclerView.Adapter<CurrenciesListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val currencyCodeTextView: TextView
        val currencyRateTextView: TextView
        val flagView: ImageView
        val increaseArrow: TextView

        init {
            // Define click listener for the ViewHolder's View.
            increaseArrow = view.findViewById(R.id.increaseTextView)
            currencyCodeTextView = view.findViewById(R.id.codeView)
            currencyRateTextView = view.findViewById(R.id.rateTextView)
            flagView = view.findViewById(R.id.imageView)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.currency_rate, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val currency = dataSet[position]
        viewHolder.currencyCodeTextView.text = currency.currencyCode
        viewHolder.currencyRateTextView.text = currency.rate.toString()
        viewHolder.flagView.setImageResource(currency.flag)
        if(currency.increase){
            viewHolder.increaseArrow.text = "⬆"
            viewHolder.increaseArrow.setTextColor("#008000".toColorInt())
        }else{
            viewHolder.increaseArrow.text = "⬇"
            viewHolder.increaseArrow.setTextColor("#ff0000".toColorInt())
        }
        viewHolder.itemView.setOnClickListener{goToDetails(currency.currencyCode, position)}
    }

    private fun goToDetails(currencyCode: String, position: Int) {
        val intent = Intent(context, Second_Activity::class.java).apply {
            putExtra("currencyCode", currencyCode)
            putExtra("currencyTableName", dataSet[position].tableName)
        }
        context.startActivity(intent)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}