package com.example.shoppinglist.presentation

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R

class ShopItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var textViewName = view.findViewById<TextView>(R.id.textViewName)
    var textViewCount = view.findViewById<TextView>(R.id.textViewCount)

}