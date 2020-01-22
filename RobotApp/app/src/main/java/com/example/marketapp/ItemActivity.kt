package com.example.marketapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.marketapp.adapters.EntityAdapter
import com.example.marketapp.models.Product
import kotlinx.android.synthetic.main.activity_item.*

class ItemActivity : AppCompatActivity() {
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            id = bundle.getInt("MainActId", 0)
            if (id != 0) {
                name.setText(bundle.getString("MainActTitle"))
            }
        }

        sendButton.setOnClickListener {

            val elementAdapter = EntityAdapter(this,  intent.getStringExtra("type"))

            if (id == 0) {
                val item = Product(1,"","", 0, "", 0)
                item.name = name.text.toString()
                item.specs = specs.text.toString()
                item.height = height.text.toString().toInt()
                item.type = type.text.toString()
                item.age = age.text.toString().toInt()
                progressBar.visibility = View.VISIBLE

                val handler = Handler()
                handler.postDelayed( {
                    elementAdapter.addElement(item)
                    elementAdapter.refreshElements()
                    progressBar.visibility = View.GONE
                    finish()
                }, 1000)


            }
        }
    }
}
