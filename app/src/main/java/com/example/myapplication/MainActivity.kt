package com.example.myapplication

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.mqtt.MqttClientHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.*
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {

    private val mqttClient by lazy {
        MqttClientHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        textViewCO2.movementMethod = ScrollingMovementMethod()

        setMqttCallBack()

        // initialize 'num msgs received' field in the view


        /*
        // pub button
        btnPub.setOnClickListener { view ->
            var snackbarMsg : String
            val topic = editTextPubTopic.text.toString().trim()
            snackbarMsg = "Cannot publish to empty topic!"
            if (topic.isNotEmpty()) {
                snackbarMsg = try {
                    "Published to topic '$topic'"
                } catch (ex: MqttException) {
                    "Error publishing to topic: $topic"
                }
            }
            Snackbar.make(view, snackbarMsg, 300)
                .setAction("Action", null).show()

        }*/

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
