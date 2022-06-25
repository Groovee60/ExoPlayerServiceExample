
import android.content.Intent
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Tracks
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.groodysoft.exoplayerserviceexample.MainApplication
import com.groodysoft.exoplayerserviceexample.service.ACTION_METADATA

class PlayerEventListener : Player.Listener {

    override fun onPlayerError(e: PlaybackException) {
        if (isBehindLiveWindow(e)) {
            Toast.makeText(MainApplication.context, "isBehind TRUE", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(MainApplication.context, "isBehind FALSE", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTracksChanged(tracks: Tracks) {
        super.onTracksChanged(tracks)
        LocalBroadcastManager.getInstance(MainApplication.context).sendBroadcast(Intent(ACTION_METADATA))
    }

    private fun isBehindLiveWindow(e: PlaybackException): Boolean {
//        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
//            return false
//        }
        var cause: Throwable? = e.cause
        while (cause != null) {
            if (cause is BehindLiveWindowException) {
                return true
            }
            cause = cause.cause
        }
        return false
    }
}