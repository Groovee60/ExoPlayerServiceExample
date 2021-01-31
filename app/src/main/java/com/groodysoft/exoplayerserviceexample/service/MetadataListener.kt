package com.groodysoft.exoplayerserviceexample.service

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.analytics.AnalyticsListener.EventTime
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.groodysoft.exoplayerserviceexample.MainApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MetadataListener(private val trackSelector: MappingTrackSelector?) : AnalyticsListener {
    override fun onTracksChanged(
        eventTime: EventTime, ignored: TrackGroupArray, trackSelections: TrackSelectionArray
    ) {
        if (!DescriptionAdapter.useStreamExtraction) {
            LocalBroadcastManager.getInstance(MainApplication.context).sendBroadcast(Intent(ACTION_METADATA))
            return
        }

        GlobalScope.launch(Dispatchers.Default) {

            val mappedTrackInfo = trackSelector?.currentMappedTrackInfo
            if (mappedTrackInfo != null) { // Log tracks associated to renderers.
                val rendererCount = mappedTrackInfo.rendererCount
                for (rendererIndex in 0 until rendererCount) {
                    val rendererTrackGroups = mappedTrackInfo.getTrackGroups(rendererIndex)
                    val trackSelection = trackSelections[rendererIndex]
                    if (rendererTrackGroups.length > 0) { // Log metadata for at most one of the tracks selected for the renderer.
                        if (trackSelection != null) {
                            for (selectionIndex in 0 until trackSelection.length()) {
                                val metadata = trackSelection.getFormat(selectionIndex).metadata
                                if (metadata != null) {

                                    // update the current metadata and send a local broadcast that it's available
                                    DescriptionAdapter.currentMetadata = metadata
                                    LocalBroadcastManager.getInstance(MainApplication.context).sendBroadcast(Intent(ACTION_METADATA))

                                    // log the metadata for debugging purposes if desired
//                                    logd("    Metadata [")
//                                    for (i in 0 until metadata.length()) {
//                                        logd("      " + metadata[i])
//                                    }
//                                    logd("    ]")
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
    }

//    private fun logd(msg: String?) {
//        Log.d("MetadataListener", msg!!)
//    }
}