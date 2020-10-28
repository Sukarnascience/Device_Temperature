package com.example.devicetemperature

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.content.*
import android.graphics.BitmapFactory
import android.os.BatteryManager
import android.os.Build
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var intentfilter: IntentFilter

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: Notification.Builder
    private val channelID ="1234"
    private val description="Device Temperature"

    private val broadcastreceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val batteryTemp = (intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10
            val dataBT: TextView = findViewById(R.id.textView)
            dataBT.text = ("$batteryTemp ${0x00B0.toChar()}C")
            if (batteryTemp >= 40.0) {
                warning(batteryTemp)
                sendNotification(batteryTemp)
                dataBT.setTextColor(Color.RED)
            }
            else if(batteryTemp >=0.0 && batteryTemp <= 39.0){
                dataBT.setTextColor(Color.GREEN)
            }
            else if(batteryTemp<0){
                dataBT.setTextColor(Color.BLUE)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        intentfilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        Thread {
            this@MainActivity.registerReceiver(broadcastreceiver, intentfilter)
            Thread.sleep(60000)
        }.start()

    }

    private fun warning(datain: Int) {
        Toast.makeText(this, "Phone Temperature ${datain}${0x00B0.toChar()}C", Toast.LENGTH_SHORT).show()
    }

    private fun sendNotification(valueoftemp:Int){
        val intent=Intent(this,LauncherActivity::class.java)
        val pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelID, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor=Color.RED
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
            builder=Notification.Builder(this,channelID)
                .setContentTitle("Device Temperature")
                .setContentText("Device Temperature is $valueoftemp${0x00B0.toChar()}C")
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.ic_launcher))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        }
        else{
            builder=Notification.Builder(this)
                .setContentTitle("Device Temperature")
                .setContentText("Device Temperature is $valueoftemp${0x00B0.toChar()}C")
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.ic_launcher))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234,builder.build())
    }
}
