package com.groodysoft.exoplayerserviceexample

import android.content.*
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import coil.api.load
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

        playerView.controllerShowTimeoutMs = 0
        playerView.controllerHideOnTouch = false
        playerView.useArtwork = false
        playerView.setShowShuffleButton(true)
        playerView.setRepeatToggleModes(RepeatModeUtil.REPEAT_TOGGLE_MODE_ALL)

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
        LocalBroadcastManager.getInstance(MainApplication.context).sendBroadcast(Intent(ACTION_METADATA))
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

                    if (DescriptionAdapter.useStreamExtraction) {

                        coverArtImageView.load(DescriptionAdapter.getCurrentLargeIcon()) {
                            placeholder(R.drawable.album_art_placeholder)
                            crossfade(true)
                            fallback(R.drawable.album_art_placeholder)
                            error(R.drawable.album_art_placeholder)
                        }
                    } else {

                        val track = SampleCatalog.tracks[it.currentWindowIndex]
                        coverArtImageView.load(track.frontCoverUrl) {
                            placeholder(R.drawable.album_art_placeholder)
                            crossfade(true)
                            fallback(R.drawable.album_art_placeholder)
                            error(R.drawable.album_art_placeholder)
                        }
                    }
                }
            }
        }
    }
}
