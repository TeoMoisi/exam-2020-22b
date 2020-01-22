package com.example.marketapp.adapters

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.marketapp.EntityDetailActivity
import com.example.marketapp.GenreActivity
import com.example.marketapp.R
import com.example.marketapp.local_db.DbManager
import com.example.marketapp.models.Product
import com.example.marketapp.networking.ApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_client_view.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException

class ClientAdapter(val context: Context) :
    RecyclerView.Adapter<ClientAdapter.ElementViewAdapter>() {

    val client by lazy { ApiClient.create() }
    var elementsList: ArrayList<String> = ArrayList()
    var isLoaded: Boolean = false
    private val dbManager = DbManager(context)

    init {
        refreshElements()
        //sync()
    }


    class ElementViewAdapter(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClientAdapter.ElementViewAdapter {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_client_view, parent, false)

        return ElementViewAdapter(view)
    }

    override fun onBindViewHolder(holder: ElementViewAdapter, position: Int) {
        holder.view.title.text = elementsList[position]

        holder.view.setOnClickListener {
            Log.d("element", elementsList[position])
            val aux = Intent(context, GenreActivity::class.java)
            aux.putExtra("type", elementsList[position])
            aux.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(aux)
        }
    }

    override fun getItemCount() = elementsList.size

    @SuppressLint("CheckResult")
    fun refreshElements() {
        if (checkOnline()) {
            client.getTypes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        elementsList.clear()
                        elementsList.addAll(result)
                        notifyDataSetChanged()
                        sync()
                        Log.d("Elements -> ", elementsList.toString())
                    },
                    { throwable ->
                        if (throwable is HttpException) {
                            val body: ResponseBody = throwable.response().errorBody()!!
                            Toast.makeText(
                                context,
                                "Error: ${JSONObject(body.string()).getString("text")}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )
        } else {
            loadQueryAll()
            Toast.makeText(context, "Not online!", Toast.LENGTH_LONG).show()
        }
    }

    private fun sync() {
        dbManager.deleteAllTypes()
        for (element in elementsList) {
            val values = ContentValues()
            values.put("type", element)
            dbManager.add(values)
        }
        isLoaded = true
    }

    private fun loadQueryAll() {
        val cursor = dbManager.queryAllTypes()
        elementsList.clear()
        if (cursor.moveToFirst()) {
            do {
                val type = cursor.getString(cursor.getColumnIndex("type"))
                elementsList.add(type)
            } while (cursor.moveToNext())
        }
    }

    private fun checkOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            return true
        }
        return false

    }

}