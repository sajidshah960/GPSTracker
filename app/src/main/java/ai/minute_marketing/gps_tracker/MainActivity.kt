package ai.minute_marketing.gps_tracker

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.xixun.joey.aidlset.CardService
import com.xixun.joey.aidlset.CommunicationJoey

class MainActivity : Activity() {

    private var card: CardService? = null
    private lateinit var linLayout: LinearLayout
    private lateinit var resolutionTextView: TextView
    private lateinit var gpsTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val myReceiver = MyReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        setContentView(R.layout.activity_main)

        // Get references to the layout and TextViews from the XML
        linLayout = findViewById(R.id.linLayout)
        resolutionTextView = findViewById(R.id.resolutionTextView)
        gpsTextView = findViewById(R.id.gpsCoordinates)

        // Register GPS broadcast receiver
        myReceiver.registerGPSReceiver(this)

        // Start a new thread to bind to the service and fetch screen resolution
        Thread {
            try {
                // Attempt to bind the service
                card = CommunicationJoey.autoBind(this)

                // Check if the service is bound successfully
                card ?: throw Exception("CardService is not bound. Check the service binding.")

                // Fetch screen dimensions from the CardService
                val screenWidth = card?.screenWidth ?: 0
                val screenHeight = card?.screenHeight ?: 0

                Log.d("ResolutionCheck", "Screen height: $screenHeight, Screen width: $screenWidth")

                // Update the layout with the fetched resolution
                runOnUiThread {
                    setLayoutResolution(screenWidth, screenHeight)
                }

                // Start updating GPS data every second
                handler.post(gpsUpdateRunnable)

            } catch (e: Exception) {
                Log.e("ResolutionCheck", "Error: ${e.message}")
                e.printStackTrace()

                // Display an error message to the user if there's an issue
                runOnUiThread {
                    Toast.makeText(this, "Failed to get screen resolution.", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun setLayoutResolution(width: Int, height: Int) {
        // Set layout parameters with desired dimensions from CardService
        val layoutParams = FrameLayout.LayoutParams(width, height)

        // Apply layout parameters to the LinearLayout
        linLayout.layoutParams = layoutParams

        // Update TextView with the new resolution for display
        resolutionTextView.text = "Layout Resolution: ${width} x ${height}"

        // Provide feedback to the user
        Toast.makeText(this, "Layout set to: ${width} x ${height}", Toast.LENGTH_SHORT).show()
    }

    // Runnable to update GPS data every second
    private val gpsUpdateRunnable = object : Runnable {
        override fun run() {
            // Update GPS coordinates on the UI
            gpsTextView.text = "${myReceiver.gpsCoordinates.first}, ${myReceiver.gpsCoordinates.second}"

            // Re-post this runnable after 1 second
            handler.postDelayed(this, 2000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(gpsUpdateRunnable)
        myReceiver.unregisterGPSReceiver(this)
    }
}
