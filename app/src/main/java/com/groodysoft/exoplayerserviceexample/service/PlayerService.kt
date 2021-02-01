package com.groodysoft.exoplayerserviceexample.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.gson.reflect.TypeToken
import com.groodysoft.exoplayerserviceexample.MainApplication
import com.groodysoft.exoplayerserviceexample.R
import com.groodysoft.exoplayerserviceexample.model.TrackData

private val PACKAGE = MainApplication.context.packageName

val ACTION_METADATA = "$PACKAGE.ACTION_METADATA"
val NOTIFICATION_ACTION = "$PACKAGE.NOTIFICATION_ACTION"
val SERVICE_ACTION_CONTENT_TRACK = "$PACKAGE.SERVICE_ACTION_CONTENT_TRACK"
val SERVICE_ACTION_CONTENT_TRACK_LIST = "$PACKAGE.SERVICE_ACTION_CONTENT_TRACK_LIST"
val SERVICE_ACTION_PLAY = "$PACKAGE.SERVICE_ACTION_PLAY"
val SERVICE_ACTION_START = "$PACKAGE.SERVICE_ACTION_START"

val SERVICE_EXTRA_STRING = "$PACKAGE.SERVICE_EXTRA_STRING"

const val FOREGROUND_SERVICE_NOTIFICATION_ID = 101

fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

@SuppressLint("NewApi")
fun Context.sendServiceIntent(action: String, stringExtra: String? = null) {
    Intent(this, PlayerService::class.java).apply {

        this.action = action

        if (stringExtra != null) {
            this.putExtra(SERVICE_EXTRA_STRING, stringExtra)
            startService(this)
        } else {

            if (isOreoPlus()) {
                startForegroundService(this)
            } else {
                startService(this)
            }
        }
    }
}

class PlayerService : Service() {

    private val logtag: String = PlayerService::class.java.simpleName

    lateinit var player: SimpleExoPlayer

    private lateinit var playerNotificationManager: PlayerNotificationManager

    private val binder = MyLocalBinder()

    override fun onCreate() {
        super.onCreate()

        val trackSelector = DefaultTrackSelector( /* context= */this,
            AdaptiveTrackSelection.Factory()
        )

        player = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()

        player.addListener(PlayerEventListener())

        registerReceiver(audioNoisyReceiver, noisyAudioIntentFilter)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            metadataReceiver,
            metadataIntentFilter
        )

        playerNotificationManager = PlayerNotificationManager(
            this, getChannelId(),
            FOREGROUND_SERVICE_NOTIFICATION_ID,
            DescriptionAdapter
        )
        playerNotificationManager.setPlayer(player)

        // define notification behavior
        //playerNotificationManager.setUseNavigationActions(true)
        //playerNotificationManager.setFastForwardIncrementMs(0)
        //playerNotificationManager.setRewindIncrementMs(0)
        playerNotificationManager.setUseStopAction(false)
        playerNotificationManager.setColorized(true)
        playerNotificationManager.setColor(
            ContextCompat.getColor(
                MainApplication.context,
                R.color.bkgd_notification
            )
        )
        playerNotificationManager.setUsePlayPauseActions(true)
        playerNotificationManager.setUseChronometer(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(audioNoisyReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(metadataReceiver)

        playerNotificationManager.setPlayer(null)
        player.release()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        Log.i(logtag, intent.action!!)
        when (intent.action) {

            SERVICE_ACTION_CONTENT_TRACK -> {
                val jsonTrack = intent.getStringExtra(SERVICE_EXTRA_STRING)
                val track = MainApplication.gson.fromJson(jsonTrack, TrackData::class.java)


                val mediaItem1 = MediaItem.fromUri(track.url)
                player.setMediaItem(mediaItem1)
                player.playWhenReady = false
                player.seekTo(0, 0)
                player.prepare()
            }
            SERVICE_ACTION_CONTENT_TRACK_LIST -> {
                val type = object : TypeToken<List<TrackData>>() {}.type
                val jsonTrackList = intent.getStringExtra(SERVICE_EXTRA_STRING)
                val tracks: List<TrackData> = MainApplication.gson.fromJson(jsonTrackList, type)
                for ((index, track) in tracks.withIndex()) {
                    if (index == 0) {
                        player.setMediaItem(MediaItem.fromUri(track.url))
                    } else {
                        player.addMediaItem(MediaItem.fromUri(track.url))
                    }
                }

                player.playWhenReady = false
                player.seekTo(0, 0)
                player.prepare()
            }
            SERVICE_ACTION_PLAY -> {
                player.playWhenReady = true
            }
            SERVICE_ACTION_START -> {
                val notification = NotificationCompat.Builder(MainApplication.context, getChannelId()).build()
                this.startForeground(FOREGROUND_SERVICE_NOTIFICATION_ID, notification)
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class MyLocalBinder : Binder() {
        fun getService() : PlayerService {
            return this@PlayerService
        }
    }

    private fun getChannelId(): String {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("my_service", "My Background Service")
        } else {
            // If earlier version channel ID is not used
            // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
            ""
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("SameParameterValue")
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    private val noisyAudioIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val audioNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action) {
                if (player.isPlaying) {
                    player.playWhenReady = false
                }
            }
        }
    }

    private val metadataIntentFilter = IntentFilter(ACTION_METADATA)
    private val metadataReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_METADATA == intent.action) {
                playerNotificationManager.invalidate()
            }
        }
    }
}
