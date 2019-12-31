package com.groodysoft.exoplayerserviceexample

import android.annotation.SuppressLint
import android.content.*
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.util.RepeatModeUtil
import com.groodysoft.exoplayerserviceexample.MainActivity.Companion.playerServiceIsBound
import com.groodysoft.exoplayerserviceexample.service.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*

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
                sendServiceIntent(SERVICE_ACTION_CONTENT_TRACK_LIST, MainApplication.gson.toJson(SampleCatalog.tracks))
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

        DescriptionAdapter.useStreamExtraction = false

        playerView.controllerShowTimeoutMs = 0
        playerView.controllerHideOnTouch = false
        playerView.useArtwork = false
        playerView.setShowShuffleButton(false)
        playerView.setRepeatToggleModes(RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE)

        // bind to the service whether it's already running or not
        // save a flag so we know to initialize and play the content
        wasPlayerServiceBound = playerServiceIsBound
        val intent = Intent(this, PlayerService::class.java)
        bindService(intent, playerServiceConnection, Context.BIND_AUTO_CREATE)

        LocalBroadcastManager.getInstance(this).registerReceiver(metadataReceiver, metadataIntentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(metadataReceiver)
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

    private val metadataIntentFilter = IntentFilter(ACTION_METADATA)
    private val metadataReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_METADATA == intent.action) {

                playerService?.player?.let {

                    // based on the boolean in the DescriptionAdapter, this will either
                    // extract the metadata (title, album, cover art) from the embedded
                    // ID3 tags in the HTTP stream, or load it from the local data in the
                    // sample catalog
                    trackTitle.text = DescriptionAdapter.getCurrentContentTitle(it)
                    trackSubtitle.text = DescriptionAdapter.getCurrentContentText(it)
                    //DescriptionAdapter.setBitmapIntoImageView(it, coverArtImageView)

                    if (DescriptionAdapter.useStreamExtraction) {

                        Glide.with(coverArtImageView.context)
                            .load(DescriptionAdapter.getCurrentLargeIcon())
                            .into(coverArtImageView)
                    } else {

                        val track = SampleCatalog.tracks[it.currentWindowIndex]
                        Glide.with(coverArtImageView.context)
                            .load(track.frontCoverUrl)
                            .into(coverArtImageView)
                    }
                }
            }
        }
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
