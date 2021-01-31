package com.groodysoft.exoplayerserviceexample.service

import android.content.Intent
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.groodysoft.exoplayerserviceexample.MainApplication

class PlayerEventListener : Player.EventListener {

    override fun onPlayerError(e: ExoPlaybackException) {
        if (isBehindLiveWindow(e)) {
            Toast.makeText(MainApplication.context, "isBehind TRUE", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(MainApplication.context, "isBehind FALSE", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
        LocalBroadcastManager.getInstance(MainApplication.context).sendBroadcast(Intent(ACTION_METADATA))
    }

    private fun isBehindLiveWindow(e: ExoPlaybackException): Boolean {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false
        }
        var cause: Throwable? = e.sourceException
        while (cause != null) {
            if (cause is BehindLiveWindowException) {
                return true
            }
            cause = cause.cause
        }
        return false
    }
}