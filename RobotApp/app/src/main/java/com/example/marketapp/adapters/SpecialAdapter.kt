package com.example.marketapp.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.marketapp.EntityDetailActivity
import com.example.marketapp.R
import com.example.marketapp.local_db.DbManager
import com.example.marketapp.models.Product
import com.example.marketapp.networking.ApiClient
import kotlinx.android.synthetic.main.special_view.view.*

class SpecialAdapter(val context: Context) :
    RecyclerView.Adapter<SpecialAdapter.SpecialViewAdapter>() {

    private val client by lazy { ApiClient.create() }
    var elementsList: ArrayList<Product> = ArrayList()
    private var dbManager: DbManager

    init {
        dbManager = DbManager(context)
        loadQueryAll()
    }

    private fun loadQueryAll() {

    }


    class SpecialViewAdapter(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SpecialAdapter.SpecialViewAdapter {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.special_view, parent, false)

        return SpecialViewAdapter(view)
    }

    override fun onBindViewHolder(holder: SpecialViewAdapter, position: Int) {

        holder.view.name.text = elementsList[position].name
        holder.view.specs.text = elementsList[position].specs
        //add height
        holder.view.type.text = elementsList[position].type
        holder.view.age.text = elementsList[position].age.toString()

    }

    override fun getItemCount() = elementsList.size

}