package com.example.ubuntu.happyeliminating;

import android.app.Application;
import android.media.MediaPlayer;
import android.provider.MediaStore;

import java.util.ArrayList;

public class SharedMediaPlayer extends Application {
    private boolean playing;
    ArrayList<Song> playList;
    private MediaPlayer musicPlayer;
    private int playingID;
    private String playingPath;
    public SharedMediaPlayer() {
        this.playList = new ArrayList<>();
        playing = false;
    }
    public MediaPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public void setMusicPlayer(MediaPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    public int getPlayingID() {
        return playingID;
    }

    public void setPlaygID(int id) {
        playingID = id;
    }

    public String getPlayingPath() {
        return playingPath;
    }

    public void setPlayingPath(String playingPath) {
        this.playingPath = playingPath;
    }

    public void addSong(Song song) {
        playList.add(song);
    }

    public void removeSong(int position) {
        playList.remove(position);
    }

    public ArrayList<Song> getPlayList() {
        return playList;
    }
    public int seekToNext() {

        int findedID = -1;
        for(int i = 0; i< playList.size();i++) {
            if (playList.get(i).getPath().equals(playingPath)) {
                findedID = i;
                break;
            }
        }
        return (findedID +1)%playList.size();
    }
    public void setStatePlay(boolean isPlay) {
        this.playing = isPlay;
    }
    public boolean isPlaying() {
        return this.playing;
    }
    public Song getPlaying(String path) {
        for (int i = 0; i < playList.size();i++) {
            if (playList.get(i).getPath().equals(path))
                return playList.get(i);
        }
        return null;
    }
    public void setPlayList(ArrayList<Song> songs) {
        playList = songs;
    }
}
