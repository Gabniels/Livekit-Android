package com.example.gabniellivekit

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.gabniellivekit.databinding.ActivityMainBinding
import com.example.gabniellivekit.util.requestNeededPermissions
import io.livekit.android.AudioOptions
import io.livekit.android.AudioType
import io.livekit.android.LiveKit
import io.livekit.android.LiveKitOverrides
import io.livekit.android.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var room: Room
    private val _connected = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        room = LiveKit.create(
            appContext = application,
            overrides = LiveKitOverrides(
                audioOptions = AudioOptions(
                    audioOutputType = AudioType.MediaAudioType()
                )
            )
        )
        requestNeededPermissions()

        observeConnectionState()

        binding.btnConnect.setOnClickListener {
            val token = binding.edtToken.text.toString()
            if (_connected.value) {
                disconnectRoom()
            } else {
                Toast.makeText(this, token, Toast.LENGTH_SHORT).show()
                connectToRoom(token)
            }
        }
    }

    private fun observeConnectionState() {
        lifecycleScope.launch(Dispatchers.IO) {
            connected.collect { isConnected ->
                Log.d("gabniel-state", "observeConnectionState: $isConnected")
                withContext(Dispatchers.Main) {
                    binding.txtStatus.text = if (isConnected) "Connected" else "Disconnected"
                    binding.btnConnect.text = if (isConnected) "Disconnect" else "Connect"
                }
            }
        }
    }


    private fun connectToRoom(token: String) {
        /* use your url project and token */
        val url = ""

        lifecycleScope.launch {
            try {
                room.connect(url, token)
                room.localParticipant.setMicrophoneEnabled(true)
                _connected.value = true
            } catch (e: Exception) {
                Log.d("gabniel-state", "connectToRoom: ${e.message}")
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
                _connected.value = false
            }
        }
    }

    private fun disconnectRoom() {
        room.disconnect()
        _connected.value = false
    }

}