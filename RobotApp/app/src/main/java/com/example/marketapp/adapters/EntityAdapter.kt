package com.example.marketapp.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.example.marketapp.R
import com.example.marketapp.local_db.DbManager
import com.example.marketapp.models.Product
import com.example.marketapp.networking.ApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_clerk_view.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException



class EntityAdapter(val context: Context, val type: String) :
    RecyclerView.Adapter<EntityAdapter.ElementViewAdapter>() {

    val client by lazy { ApiClient.create() }
    var elementsList: ArrayList<Product> = ArrayList()
    private val dbManager = DbManager(context)

    init {
        refreshElements()
    }

    class ElementViewAdapter(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EntityAdapter.ElementViewAdapter {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_clerk_view, parent, false)

        return ElementViewAdapter(view)
    }

    override fun onBindViewHolder(holder: ElementViewAdapter, position: Int) {

        holder.view.name.text = elementsList[position].name
        holder.view.url.text = elementsList[position].specs
        holder.view.type.text = elementsList[position].type
        holder.view.rating.text = elementsList[position].height.toString()
        holder.view.btnDelete.setOnClickListener { showDeleteDialog(holder, elementsList[position]) }

        holder.view.setOnClickListener {
            Log.d("Update the next item: ", elementsList[position].toString())
            val aux = Intent(context, EntityDetailActivity::class.java)
            aux.putExtra("id", elementsList[position].id.toString())
            aux.putExtra("type", elementsList[position].type)
            aux.putExtra("name", elementsList[position].name)
            aux.putExtra("specs", elementsList[position].specs)
            aux.putExtra("age", elementsList[position].age.toString())
            aux.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(aux)
        }
    }

    override fun getItemCount() = elementsList.size

    @SuppressLint("CheckResult")
    fun refreshElements() {
        if (checkOnline()) {
            client.getBookmarks(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        elementsList.clear()
                        elementsList.addAll(result)
                        elementsList.sortWith(compareBy ({it.name}))
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
            loadQueryAll(type)
            Toast.makeText(context, "Not online!", Toast.LENGTH_LONG).show()
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

    @SuppressLint("CheckResult")
    fun addElement(element: Product) {
        if (checkOnline()) {
            client.addElement(element)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        refreshElements()
                        Log.d("Element added -> ", element.toString())
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
                Toast.makeText(context, "Not online!", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("CheckResult")
    fun updateElement(id: Int, element: Product) {
        if (checkOnline()) {
            client.updateHeight(element)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        refreshElements()
                        Log.d("Element updated : ", element.toString())
                    },
                    { throwable ->
                        Toast.makeText(context, "Update error: ${throwable.message}", Toast.LENGTH_LONG).show()
                    }
                )
        } else {
            Toast.makeText(context, "Not online! The update was insuccessful!", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("CheckResult")
    private fun deleteElement(element: Product) {
        if (checkOnline()) {
            client.deleteElement(element.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        refreshElements()
                        Log.d("Element deleted -> ", element.toString())
                    },
                    { throwable ->
                        Toast.makeText(context, "Delete error: ${throwable.message}", Toast.LENGTH_LONG).show()
                    }
                )
        } else {
            Toast.makeText(context, "Not online!", Toast.LENGTH_LONG).show()
        }

    }

    private fun showDeleteDialog(holder: ElementViewAdapter, element: Product) {
        val dialogBuilder = AlertDialog.Builder(holder.view.context)
        dialogBuilder.setTitle("Delete")
        dialogBuilder.setMessage("Confirm delete?")
        dialogBuilder.setPositiveButton("Delete") { _, _ ->
            deleteElement(element)
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        val b = dialogBuilder.create()
        b.show()
    }

    private fun sync() {
        dbManager.deleteWhereType(type)
        for (element in elementsList) {
            val values = ContentValues()
            values.put("name", element.name)
            values.put("specs", element.specs)
            values.put("height", element.height.toString())
            values.put("type", element.type)
            values.put("age", element.age.toString())
            Log.d("INSERT", values.toString())
            dbManager.insert(values)
        }
    }

    private fun loadQueryAll(typeGiven: String) {
        val cursor = dbManager.queryAll()
        elementsList.clear()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val specs = cursor.getString(cursor.getColumnIndex("specs"))
                val height = cursor.getString(cursor.getColumnIndex("height")).toInt()
                val type = cursor.getString(cursor.getColumnIndex("type"))
                val age = cursor.getString(cursor.getColumnIndex("age")).toInt()
                if (type == typeGiven) {
                    Log.d("ADDED", type)
                    elementsList.add(Product(id.toInt(), name, specs, height, type, age))
                }
            } while (cursor.moveToNext())
        }
    }
}