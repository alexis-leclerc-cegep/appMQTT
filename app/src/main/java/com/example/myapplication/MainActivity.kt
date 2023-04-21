package com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.mqtt.MqttClientHelper
import com.example.myapplication.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.*
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity(){


    private val mqttClient by lazy {
        MqttClientHelper(this)
    }


    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences : SharedPreferences= getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        if(sharedPreferences.getString("token", null) == null){
            Log.w("AndroidRuntime", "Token null, starting login")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val API_URL: String = "http://172.16.6.110:6969"

        getIP("$API_URL/brokerIp")

        // sub button
        btnSub.setOnClickListener { view ->
            var snackbarMsg : String
            val topic = "alexis/co2"
            val topic2 = "alexis/tvoc"
            snackbarMsg = "Cannot subscribe to empty topic!"
            if(mqttClient.isConnected()){
                snackbarMsg = try {
                    mqttClient.subscribe(topic)
                    mqttClient.subscribe(topic2)
                    "Subscribed to topic '$topic'"
                } catch (ex: MqttException) {
                    "Error subscribing to topic: $topic"
                }
            }
            else{
                snackbarMsg = "Not connected to broker, try again"
            }
            Snackbar.make(view, snackbarMsg, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
        }

        Timer("CheckMqttConnection", false).schedule(3000) {
            if (!mqttClient.isConnected()) {
                Snackbar.make(findViewById(android.R.id.content), "Failed to connect to: '$SOLACE_MQTT_HOST' within 3 seconds", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null).show()
            }
        }

    }

    private fun changeBrokerIP(ip: String){
        SOLACE_MQTT_HOST = "tcp://$ip"
        println("joe")
        println(SOLACE_MQTT_HOST)

        setMqttCallBack()
    }

    private fun getIP(url: String){
        val sharedPreferences : SharedPreferences= getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val token : String? = sharedPreferences.getString("token", null)

        Log.i("HTTP", "getting ip")

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .build()

        var reponse: String = "pasteur"

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("failure")
            }
            override fun onResponse(call: Call, response: Response) {
                val obj = JSONObject(response.body()?.string())
                println("la reponse")
                reponse = obj.getString("ip")
                changeBrokerIP(reponse)
            }
        })
    }

    fun run(url: String){
        val request = Request.Builder()
            .url(url)
            .build()

        val reponse: String

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("failure")
            }
            override fun onResponse(call: Call, response: Response) {

            }

        })
    }


    private fun setMqttCallBack() {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                val snackbarMsg = "Connected to host:\n'$SOLACE_MQTT_HOST'."
                Log.w("Debug", snackbarMsg)
                Snackbar.make(findViewById(android.R.id.content), snackbarMsg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
            override fun connectionLost(throwable: Throwable) {
                val snackbarMsg = "Connection to host lost:\n'$SOLACE_MQTT_HOST'"
                Log.w("Debug", snackbarMsg)
                Snackbar.make(findViewById(android.R.id.content), snackbarMsg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                Log.w("Debug", "Message received from host '$SOLACE_MQTT_HOST': $mqttMessage")
                when (topic){
                    "alexis/co2" -> textViewCO2.text = "$mqttMessage ppm"
                    "alexis/tvoc" -> textViewTVOC.text = "$mqttMessage ppm"
                }
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                Log.w("Debug", "Message published to host '$SOLACE_MQTT_HOST'")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        mqttClient.destroy()
        super.onDestroy()
    }

}
