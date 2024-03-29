# Music-Barrel


## A web interfaced jukebox for Raspberry Pi, or a similar HTPC
This is a web front end to MediaChest (aka Photoorganizer). 
Therefore, if you notice any bug in it, it can be a problem of MediaChest and you need to updated its jar.

### Features set

1. Auto scan local and remote storage for music file and store them in database with
  separated type, quality, year, genre, album, artist, composer, song, duration, tags,
  and original location
2. Manually adding directories or songs in the database above
3. Create playlists (static)
4. Create ad hoc playlist  by a query or manually
5. Add songs to static playlists
6. Navigate over file system with filtering
7. Play playlist in modes a sequential, a random, a repeating
8. Edit song tags with corresponding a DB record update
9. Support formats MP3, WAV, WV(ISO), FLAC, APE, AAC, DSD, OGG in quality up to 32bits/192,000 (when applied) and DSDx64, DSDx128
10. Break down album FLAC/APE/WV/DSF/DFF upon CUE sheet
11. Support search back, forward, skip in individual or list playback
12. Displaying playback progress
13. Show song/album artwork
14. Research on net for song, album, artist details
15. Adding new songs by uploading 
16. Collaborative editing a dynamic playlist (no one software provides the feature yet)
17. Playback of samba v1 shared music

### Technologies
1. [MediaChest](https://github.com/drogatkin/MediaChest)/[MediaUtil](https://github.com/drogatkin/mediautil) media format library
2. <a href="https://github.com/drogatkin/Webbee"><strong>WebBee</strong></a> rapid web application building blocks
3. <a href="http://tjws.sf.net"><strong>TJWS</strong></a> at [GitHub](https://github.com/drogatkin/TJWS2)
4. Java 8 or later

![Music Barrel look](https://raw.githubusercontent.com/drogatkin/music-barrel/master/doc/music-barrel-screenshot.png?raw=true)

### Releases

Music - Barrel, the latest release is 1.6. You can download .war file from the Releases section.

It is also available as a paid app at <a target="_blank" href="https://play.google.com/store/apps/details?id=rogatkin.mobile.app.lialichka"><img src="https://github.com/drogatkin/music-barrel/blob/master/mockups/appgoogleplay.png?raw=true"></a>
