package com.groodysoft.exoplayerserviceexample

object SampleCatalog {

    // these remote MP3 files are curated with ID3 tags including,
    // Title, Album, and Cover Image - these are extracted via the
    // MetadataListener class which is a stripped down version of
    // ExoPlayer.EventLogger - this metadata should instead be supplied
    // by higher-level metadata in the application's catalog DB

    // if these remote files become unavailable, they can be replaced
    // with any other remote files with or without these ID3 tags

    val urls = listOf(
        "http://groodysoft.com/music/samples/01.mp3",
        "http://groodysoft.com/music/samples/02.mp3",
        "http://groodysoft.com/music/samples/03.mp3",
        "http://groodysoft.com/music/samples/04.mp3",
        "http://groodysoft.com/music/samples/05.mp3",
        "http://groodysoft.com/music/samples/06.mp3",
        "http://groodysoft.com/music/samples/07.mp3",
        "http://groodysoft.com/music/samples/08.mp3",
        "http://groodysoft.com/music/samples/09.mp3",
        "http://groodysoft.com/music/samples/10.mp3",
        "http://groodysoft.com/music/samples/11.mp3",
        "http://groodysoft.com/music/samples/12.mp3",
        "http://groodysoft.com/music/samples/13.mp3"
    )
}

