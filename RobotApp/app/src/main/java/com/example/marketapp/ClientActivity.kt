package com.example.marketapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.marketapp.adapters.ClientAdapter
import com.example.marketapp.models.Product
import kotlinx.android.synthetic.main.activity_client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import java.util.concurrent.TimeUnit

private const val NORMAL_CLOSURE_STATUS = 1000

class ClientActivity : AppCompatActivity() {
    lateinit var adapter: ClientAdapter
    private lateinit var wsListener: EchoWebSocketListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)

        progressBarClient.visibility = View.VISIBLE
        val handler = Handler()
        handler.postDelayed({
            adapter = ClientAdapter(this@ClientActivity)
            val layoutManager = LinearLayoutManager(this)
            layoutManager.orientation = LinearLayoutManager.VERTICAL

            listView2.layoutManager = layoutManager
            listView2.adapter = adapter
            progressBarClient.visibility = View.GONE
        }, 1000)
        // ws test
        val clientWebsoket = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url("ws://10.0.2.2:2202")
            .build()
        wsListener = EchoWebSocketListener()
        clientWebsoket.newWebSocket(
            request,
            wsListener
        ) // this provide to make 'Open ws connection'

        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        clientWebsoket.dispatcher().executorService().shutdown()
    }

        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            val inflater = menuInflater
            inflater.inflate(R.menu.buttons, menu)
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.btn_refresh -> {
                    adapter.refreshElements()
                }

                R.id.btn_favourites -> {
                    startActivity(Intent(this@ClientActivity, SpecialActivity::class.java))
                }
            }
            return super.onOptionsItemSelected(item)
        }

//        override fun onDestroy() {
//            super.onDestroy()
//            wsListener.close()
//        }

    inner class EchoWebSocketListener() : WebSocketListener() {
        lateinit var webSocket: WebSocket
        override fun onOpen(webSocket: WebSocket, response: Response) {
            this.webSocket = webSocket
            webSocket.send("Hello, there!")
            webSocket.send("What's up?")
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            logd("Receiving : ${text!!}")
            GlobalScope.launch(Dispatchers.Main) {
                var p = Product(id = JSONObject(text).getString("id").toInt(), name = JSONObject(text).getString("name"), specs = JSONObject(text).getString("specs"),
                    height = JSONObject(text).getString("height").toInt(), type =  JSONObject(text).getString("type"), age =  JSONObject(text).getString("age").toInt())
                var finalString = "Product named " + p.name + " , having specifications: " + p.specs + " , from type " + p.type + " and age: " + p.age.toString() + " was added."
                Toast.makeText(this@ClientActivity, finalString, Toast.LENGTH_LONG).show()
                adapter.refreshElements()
                listView2.adapter = adapter
            }
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
            logd("Receiving bytes : ${bytes!!.hex()}")
        }

        override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
            webSocket!!.close(NORMAL_CLOSURE_STATUS, null)
            logd("Closing : $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            logd("Error : ${t.message}", t)
        }

        fun send(message: String) {
            webSocket.send(message)
        }

        fun close() {
            webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye!")
        }
    }

    fun Any.logd(message: Any? = "no message!", cause: Throwable? = null) {
        Log.d("\n\n\nWSS\n\n\n", message.toString(), cause)
    }
}
