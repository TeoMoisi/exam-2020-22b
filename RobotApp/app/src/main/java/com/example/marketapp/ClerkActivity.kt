package com.example.marketapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.example.marketapp.adapters.EntityAdapter
import com.example.marketapp.adapters.EntityClientAdapter
import kotlinx.android.synthetic.main.activity_clerk.*
import kotlinx.android.synthetic.main.activity_elements.*
import kotlinx.android.synthetic.main.content_clerk.*

class  ClerkActivity : AppCompatActivity() {
    lateinit var adapter: EntityClientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elements)
        progressBar.visibility = View.VISIBLE
        val handler = Handler()
        handler.postDelayed( {
            adapter = EntityClientAdapter(this@ClerkActivity)
            val layoutManager = LinearLayoutManager(this)
            layoutManager.orientation = LinearLayoutManager.VERTICAL

            listView3.layoutManager = layoutManager
            listView3.adapter = adapter
            progressBar.visibility = View.GONE
        }, 1000)

//
//        updateAgeBtn.setOnClickListener {
//            startActivityForResult(Intent(this@ClerkActivity, UpdateAgeActivity::class.java), 0)
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        progressBarClerk.visibility = View.VISIBLE
        val handler = Handler()
        handler.postDelayed( {
            adapter = EntityClientAdapter(this)
            adapter.refreshElements()
            listView1.adapter = adapter

            if (listView1.adapter != null) {
                listView1.adapter!!.notifyDataSetChanged()
            }
            progressBarClerk.visibility = View.GONE
        }, 1000)
    }

}
