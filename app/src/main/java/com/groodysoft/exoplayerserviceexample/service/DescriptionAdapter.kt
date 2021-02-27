package com.groodysoft.exoplayerserviceexample.service

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.id3.ApicFrame
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame
import com.google.android.exoplayer2.ui.PlayerNotificationManager.BitmapCallback
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter
import com.groodysoft.exoplayerserviceexample.MainActivity
import com.groodysoft.exoplayerserviceexample.MainApplication.Companion.context
import com.groodysoft.exoplayerserviceexample.R
import com.groodysoft.exoplayerserviceexample.SampleCatalog

object DescriptionAdapter : MediaDescriptionAdapter {

    const val useStreamExtraction = false

    private var currentMetadata: Metadata? = null

    override fun getCurrentContentTitle(player: Player): String =
        if (useStreamExtraction) {
            currentMetadata?.findTextValue("TIT2") ?: context.getString(R.string.default_notification_title)
        } else {
            val track = SampleCatalog.tracks[player.currentWindowIndex]
            track.trackTitle
        }

    override fun getCurrentContentText(player: Player): String =
        if (useStreamExtraction) {
            currentMetadata?.findTextValue("TALB") ?: context.getString(R.string.default_notification_subtitle)
        } else {
            val track = SampleCatalog.tracks[player.currentWindowIndex]
            track.albumTitle
        }

    override fun getCurrentLargeIcon(player: Player, callback: BitmapCallback) = getCurrentLargeIcon()
    fun getCurrentLargeIcon() = currentMetadata?.findBitmapValue("APIC")

    override fun createCurrentContentIntent(player: Player): PendingIntent? {

        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.action = NOTIFICATION_ACTION
        return PendingIntent.getActivity(context, 0, notificationIntent, 0)
    }

    private fun Metadata.findTextValue(key: String): String {

        for (i in 0 until length()) {
            val entry = this[i]
            if (entry is TextInformationFrame) {
                if (entry.id == key) {
                    return entry.value
                }
            }
        }
        return "not found"
    }

    private fun Metadata.findBitmapValue(key: String): Bitmap? {

        for (i in 0 until length()) {
            val entry = this[i]
            if (entry is ApicFrame) {
                if (entry.id == key) {
                    val bytes = entry.pictureData
                    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
            }
        }
        return BitmapFactory.decodeResource(context.resources,
            R.drawable.album_art_placeholder
        )
    }
}