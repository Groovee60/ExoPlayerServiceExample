package com.groodysoft.exoplayerserviceexample

import com.groodysoft.exoplayerserviceexample.model.TrackData

object SampleCatalog {

    // the remote MP3 files contain embedded MP3 tags for track title, album title, and cover art
    // those values are redundantly defined here so we can demonstrate use of both local (probably
    // DB-defined) metadata as well as extraction of the metadata from the HTTP stream

    // if these remote files become unavailable, they can be replaced with any other remote files

    val tracks = listOf(
        TrackData("Desolate Field", "Sample Tunes", "http://groodysoft.com/music/samples/01.jpg", "http://groodysoft.com/music/samples/01.mp3"),
        TrackData("Dramatic Ambient", "Sample Tunes", "http://groodysoft.com/music/samples/02.jpg", "http://groodysoft.com/music/samples/02.mp3"),
        TrackData("Dynamite", "Sample Tunes", "http://groodysoft.com/music/samples/03.jpg", "http://groodysoft.com/music/samples/03.mp3"),
        TrackData("Epic Heroic", "Sample Tunes", "http://groodysoft.com/music/samples/04.jpg", "http://groodysoft.com/music/samples/04.mp3"),
        TrackData("Fish Room", "Sample Tunes", "http://groodysoft.com/music/samples/05.jpg", "http://groodysoft.com/music/samples/05.mp3"),
        TrackData("Greedy", "Sample Tunes", "http://groodysoft.com/music/samples/06.jpg", "http://groodysoft.com/music/samples/06.mp3"),
        TrackData("How It Began", "Sample Tunes", "http://groodysoft.com/music/samples/07.jpg", "http://groodysoft.com/music/samples/07.mp3"),
        TrackData("Jazz In Paris", "Sample Tunes", "http://groodysoft.com/music/samples/08.jpg", "http://groodysoft.com/music/samples/08.mp3"),
        TrackData("Mournful", "Sample Tunes", "http://groodysoft.com/music/samples/09.jpg", "http://groodysoft.com/music/samples/09.mp3"),
        TrackData("No Favours", "Sample Tunes", "http://groodysoft.com/music/samples/10.jpg", "http://groodysoft.com/music/samples/10.mp3"),
        TrackData("Impact Moderato", "Sample Tunes", "http://groodysoft.com/music/samples/11.jpg", "http://groodysoft.com/music/samples/11.mp3"),
        TrackData("The Coldest Shoulder", "Sample Tunes", "http://groodysoft.com/music/samples/12.jpg", "http://groodysoft.com/music/samples/12.mp3"),
        TrackData("Touch", "Sample Tunes", "http://groodysoft.com/music/samples/13.jpg", "http://groodysoft.com/music/samples/13.mp3")
    )
}

