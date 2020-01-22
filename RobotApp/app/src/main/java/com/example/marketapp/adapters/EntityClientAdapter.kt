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
import com.example.marketapp.UpdateAgeActivity
import com.example.marketapp.local_db.DbManager
import com.example.marketapp.models.Product
import com.example.marketapp.networking.ApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_clerk_view.view.*
import kotlinx.android.synthetic.main.item_clerk_view.view.name
import kotlinx.android.synthetic.main.item_clerk_view.view.type
import kotlinx.android.synthetic.main.item_client_view.view.*
import kotlinx.android.synthetic.main.special_view.view.*
import okhttp3.ResponseBody
import retrofit2.HttpException

class EntityClientAdapter(val context: Context) :
    RecyclerView.Adapter<EntityClientAdapter.ElementViewAdapter>() {

    val client by lazy { ApiClient.create() }
    var elementsList: ArrayList<Product> = ArrayList()
    var robotsList: ArrayList<Product> = ArrayList()
    var isLoaded: Boolean = false
    private val dbManager = DbManager(context)

    init {
        refreshElements()
    }


    class ElementViewAdapter(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EntityClientAdapter.ElementViewAdapter {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.special_view, parent, false)

        return ElementViewAdapter(view)
    }

    override fun onBindViewHolder(holder: ElementViewAdapter, position: Int) {
        holder.view.name.text = elementsList[position].name
        holder.view.specs.text = elementsList[position].specs
        //add height
        holder.view.type.text = elementsList[position].type
        holder.view.age.text = elementsList[position].age.toString()

        holder.view.setOnClickListener {
            Log.d("Update the next item: ", elementsList[position].toString())
            val aux = Intent(context, UpdateAgeActivity::class.java)
            aux.putExtra("id", elementsList[position].id.toString())
            aux.putExtra("type", elementsList[position].type)
            aux.putExtra("name", elementsList[position].name)
            aux.putExtra("specs", elementsList[position].specs)
            aux.putExtra("height", elementsList[position].height.toString())
            aux.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(aux)
        }
    }

    override fun getItemCount() = elementsList.size

    fun refreshElements() {
        if (checkOnline()) {
            client.getElements()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        robotsList.clear()
                        robotsList.addAll(result)
                        robotsList.sortWith(compareByDescending { it.age })
                        elementsList.clear()
                        for (robot in 0 until 10) {
                            elementsList.add(robotsList[robot])
                        }
                        notifyDataSetChanged()
                        Log.d("Elements -> ", elementsList.toString())
                    },
                    { throwable ->
                        if (throwable is HttpException) {
                            val body: ResponseBody = throwable.response().errorBody()!!
                            Toast.makeText(
                                context,
                                "Error: ${body.string()}",
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
            client.updateAge(element)
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
    private fun rateElement(element: Product) {
        if (checkOnline()) {
            client.rateElement(element.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        refreshElements()
                        Log.d("Element rated -> ", element.toString())
                    },
                    { throwable ->
                        Toast.makeText(context, "Rate error: ${throwable.message}", Toast.LENGTH_LONG).show()
                    }
                )
        } else {
            Toast.makeText(context, "Not online!", Toast.LENGTH_LONG).show()
        }

    }

    private fun sync() {
        for (element in elementsList) {
            val values = ContentValues()
            values.put("name", element.name)
            values.put("specs", element.specs)
            values.put("height", element.height.toString())
            values.put("type", element.type)
            values.put("age", element.age.toString())
            dbManager.insert(values)
        }
        isLoaded = true
    }

    private fun loadQueryAll() {
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
                elementsList.add(Product(id.toInt(),name, specs, height, type, age))
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

    @SuppressLint("CheckResult")
    private fun deleteElement(element: Product) {
        Toast.makeText(context, "Not implemented!", Toast.LENGTH_LONG).show()

    }

    private fun showDeleteDialog(holder: EntityAdapter.ElementViewAdapter, element: Product) {
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
}