package com.example.marketapp

import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.marketapp.adapters.EntityAdapter
import com.example.marketapp.local_db.DbManager
import com.example.marketapp.models.Product
import kotlinx.android.synthetic.main.activity_entity_detail.*
import kotlinx.android.synthetic.main.activity_item.*

class EntityDetailActivity : AppCompatActivity() {
    private val dbManager = DbManager(this)
    lateinit var adapter: EntityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entity_detail)
        val id = intent.getStringExtra("id").toInt()
        val type = intent.getStringExtra("type")
        val name = intent.getStringExtra("name")
        val specs = intent.getStringExtra("specs")
        val age = intent.getStringExtra("age").toInt()
        //val height = heightUpdate.text.toString().toInt()
        //val height = 10;

        val p = Product(id, name, specs, 0, type, age)

        val elementAdapter = EntityAdapter(this, type)

        favBtn.setOnClickListener {
            val height = heightUpdate.text.toString().toInt()
            p.height = height
            progressBarUpdate.visibility = View.VISIBLE

            val handler = Handler()
            handler.postDelayed( {
                elementAdapter.updateElement(id, p)
                elementAdapter.refreshElements()
                progressBarUpdate.visibility = View.GONE
                finish()
            }, 1000)
 //           val values = ContentValues()
//            values.put("title", intent.getStringExtra("title"))
//            values.put("description", intent.getStringExtra("description"))
//            values.put("album", intent.getStringExtra("album"))
//            values.put("genre", intent.getStringExtra("genre"))
//            values.put("year", intent.getStringExtra("year").toInt())
//
//            dbManager.insert(values)
        }
    }
}
