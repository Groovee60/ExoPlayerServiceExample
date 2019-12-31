package com.groodysoft.exoplayerserviceexample

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.groodysoft.exoplayerserviceexample.MainActivity.Companion.playerServiceIsBound
import com.groodysoft.exoplayerserviceexample.service.PlayerService
import com.groodysoft.exoplayerserviceexample.service.SERVICE_ACTION_CONTENT_URL_LIST
import com.groodysoft.exoplayerserviceexample.service.SERVICE_ACTION_PLAY
import com.groodysoft.exoplayerserviceexample.service.SERVICE_EXTRA_STRING
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    object Companion {
        var playerServiceIsBound = false
    }

    var wasPlayerServiceBound = false
    var playerService: PlayerService? = null

    private val playerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            val binder = service as PlayerService.MyLocalBinder
            playerService = binder.getService()
            playerServiceIsBound = true
            bindPlayer()

            if (!wasPlayerServiceBound) {
                sendServiceIntent(SERVICE_ACTION_CONTENT_URL_LIST, MainApplication.gson.toJson(SampleCatalog.urls))
                sendServiceIntent(SERVICE_ACTION_PLAY)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            playerServiceIsBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView.controllerShowTimeoutMs = 0
        playerView.controllerHideOnTouch = false

        // bind to the service whether it's already running or not
        // save a flag so we know to initialize and play the content
        wasPlayerServiceBound = playerServiceIsBound
        val intent = Intent(this, PlayerService::class.java)
        bindService(intent, playerServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()

        if (playerServiceIsBound) {
            bindPlayer()
        }
    }

    private fun bindPlayer() {
        playerView.player = playerService?.player
        playerView.showController()
    }
}

fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

@SuppressLint("NewApi")
fun Context.sendServiceIntent(action: String, stringExtra: String? = null) {
    Intent(this, PlayerService::class.java).apply {

        this.action = action

        if (stringExtra != null) {
            this.putExtra(SERVICE_EXTRA_STRING, stringExtra)
        }

        try {
            if (isOreoPlus()) {
                startForegroundService(this)
            } else {
                startService(this)
            }
        } catch (ignored: Exception) {
        }
    }
}
