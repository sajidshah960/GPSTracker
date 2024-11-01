package ai.minute_marketing.gps_tracker


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast

class MyReceiver : BroadcastReceiver() {
    var gpsCoordinates = Pair(0.0, 0.0)

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                val i = Intent(context, MainActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(i)
            }

            "com.xixun.joey.gpsinfo" -> {
                val latitude = intent.getDoubleExtra("latitude", 0.0)
                val longitude = intent.getDoubleExtra("longitude", 0.0)
                gpsCoordinates = Pair(latitude, longitude)
                Log.d("GPS Info", "Latitude: $latitude, Longitude: $longitude")
                //Toast.makeText(context, "GPS Coordinates - Lat: $latitude, Long: $longitude", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun registerGPSReceiver(context: Context) {
        val filter = IntentFilter("com.xixun.joey.gpsinfo")
        context.registerReceiver(this, filter)
    }

    fun unregisterGPSReceiver(context: Context) {
        context.unregisterReceiver(this)
    }
}
