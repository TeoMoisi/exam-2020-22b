package com.example.marketapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.marketapp.adapters.EntityAdapter
import com.example.marketapp.adapters.EntityClientAdapter
import com.example.marketapp.adapters.SpecialAdapter
import com.example.marketapp.local_db.DbManager
import com.example.marketapp.models.Product
import kotlinx.android.synthetic.main.activity_elements.*
import kotlinx.android.synthetic.main.activity_entity_detail.*
import kotlinx.android.synthetic.main.activity_entity_detail.progressBarUpdate
import kotlinx.android.synthetic.main.activity_update.*
import kotlinx.android.synthetic.main.content_clerk.*

class UpdateAgeActivity : AppCompatActivity() {
    lateinit var adapter: EntityClientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        val id = intent.getStringExtra("id").toInt()
        val type = intent.getStringExtra("type")
        val name = intent.getStringExtra("name")
        val specs = intent.getStringExtra("specs")
        val height = intent.getStringExtra("height").toInt()

        val p = Product(id, name, specs, height, type, 0)

        val elementAdapter = EntityClientAdapter(this)

        updateAgeBtn.setOnClickListener {
            val age = ageUpdate.text.toString().toInt()
            p.age = age
            progressBarUpdateAge.visibility = View.VISIBLE

            val handler = Handler()
            handler.postDelayed( {
                elementAdapter.updateElement(id, p)
                elementAdapter.refreshElements()
                progressBarUpdateAge.visibility = View.GONE
                finish()
            }, 1000)
        }
    }
}