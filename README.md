# ExoPlayerServiceExample

Google's ExoPlayer library is a tremendous alternative to MediaPlayer on the Android platform. I have read that ExoPlayer music applications have increased battery usage over those using MediaPlayer, but the plethora of other benefits more than outweighs this for me. And of course Google may improve the battery usage in subsequent versions of the library.

Building a simple music player using ExoPlayer is a trivial exercise. See their blog and online documentation. Building out a proper music service (to support background playing when the activity is destroyed, and foreground notification support) is a bit more effort, but this project provides a very streamlined Kotlin implementation with (hopefully) best practices demonstrated.

The project provides a small set of URL references to remotely hosted (on my site) public domain MP3 files. These music files embed ID3 metadata tags to supply title, subtitle and cover art. I heavily endorse the use of Mp3Tag (https://www.mp3tag.de/en) for batch editing the metadata of small or large MP3 collections. I have no affiliation with this product other than I've been using it on both Mac and Windows for a decade or so to curate and maintain my ever-growing MP3 collection. Mp3Tag is a free download, but of course donations are welcome and most deserved by the German developer Florian Heidenreich. 

A fuller implementation of the ExoPlayer and music service would likely pull this metadata from a local or online database instead of relying on the embedded MP3 tags, but for demonstration purposes the project utilizes a simple metadata extraction mechanism derived from the library's EventLogger class.

If my hosted files disappear over time for whatever reason, it is a trivial change to point at other remote files or supply locally available music instead. The library accepts source objects built from Uris which can of course be local or remote. If you don't embed the title and album art tags, you will see default values in the notification UI provided by the library. Other GitHub sample code is available to demonstrate much more complicated implementations of playlist support, MediaSession, MediaBrowser, LiveData, etc. That is not the goal of this simple example.

ExoPlayer supports many (most?) video formats in addition to streaming music. See their publicly available Github repositories for the library modules and much more elaborate sample implementations. But of the many that I have cloned, built, and examined, none demonstrate a proper and simple persistent service integration.

Please provide any feedback or pull requests toward the goal of improving and/or updating this simple example. And please pay it forward where appropriate by posting your own repositories of higher level functionality built upon this simple architecture.

