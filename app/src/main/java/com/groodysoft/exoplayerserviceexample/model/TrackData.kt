package com.groodysoft.exoplayerserviceexample.model

import kotlinx.serialization.Serializable

@Serializable
data class TrackData(val trackTitle: String, val albumTitle: String, val frontCoverUrl: String, val url: String)