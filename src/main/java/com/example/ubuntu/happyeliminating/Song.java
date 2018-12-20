package com.example.ubuntu.happyeliminating;

import android.graphics.Bitmap;

public class Song {
    private String songName;
    private String Artist;
    private String Path;
    private long Duration;
    private Bitmap Album;

    public Song() {}
    public Song(String sn, String art, String path, long duration, Bitmap album) {
        this.songName = sn;
        this.Artist = art;
        this.Album = album;
        this.Path = path;
        this.Duration = duration;
    }
    public String getSongName() {
        return songName;
    }
    public String getArtist() {
        return Artist;
    }
    public String getPath() {
        return Path;
    }
    public Bitmap getAlbum() {
        return Album;
    }
    public long getDuration() {return Duration;}
    public void setSongName(String sn) {
        this.songName = sn;
    }
    public void setArtist(String art) {
        this.Artist = art;
    }
    public void setDuration(long duration) {
        this.Duration = duration;
    }
    public void setPath(String path) {
        this.Path = path;
    }
    public void setAlbum(Bitmap album) {
        this.Album = album;
    }
    public boolean equal(Song song) {
        return (song != null&&song.getSongName().equals(this.songName)&&song.getArtist().equals(this.Artist));
    }
}
