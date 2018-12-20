package com.example.ubuntu.happyeliminating;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class PlayActivity extends Activity {
    SharedMediaPlayer myplayer;
    Button previous;
    Button play_pause;
    Button next;
    boolean pauseFlag;
    Song songPlaying;
    Song songShowing;
    ImageButton back;
    TextView playSongName;
    ImageView album;
    TextView currentTime;
    TextView totalTime;
    SeekBar seekBar;
    UIUpdater myUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myplayer = (SharedMediaPlayer)getApplicationContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        back = (ImageButton) findViewById(R.id.back);
        previous = (Button)findViewById(R.id.previous_btn);
        play_pause = (Button)findViewById(R.id.play_pause_button);
        next = (Button)findViewById(R.id.next_btn);
        seekBar = (SeekBar)findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new MySeekBarChangeListener());
        songShowing = null;
        album = (ImageView)findViewById(R.id.album_big);
        currentTime = (TextView)findViewById(R.id.current_time);
        totalTime = (TextView)findViewById(R.id.total_time);
        playSongName = (TextView)findViewById(R.id.play_songname);
        myUpdater = new UIUpdater();

        myUpdater.execute(1000);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int playing_id = myplayer.getPlayingID();
                if (playing_id >= 0) {
                    int size = myplayer.getPlayList().size();
                    int nextID = (playing_id + 1) % size;
                    myplayer.getMusicPlayer().stop();
                    myplayer.getMusicPlayer().reset();
                    try {
                        Song next = myplayer.getPlayList().get(nextID);
                        myplayer.getMusicPlayer().setDataSource(next.getPath());
                        myplayer.getMusicPlayer().prepare();
                        myplayer.getMusicPlayer().start();
                        //set myplayer
                        myplayer.setPlayingPath(next.getPath());
                        myplayer.setPlaygID(nextID);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int playing_id = myplayer.getPlayingID();
                if (playing_id >= 0) {
                    int size = myplayer.getPlayList().size();
                    int previousID = (playing_id - 1 + size) % size;
                    myplayer.getMusicPlayer().stop();
                    myplayer.getMusicPlayer().reset();

                    Song next = myplayer.getPlayList().get(previousID);
                    try {
                        myplayer.getMusicPlayer().setDataSource(next.getPath());
                        myplayer.getMusicPlayer().prepare();
                        myplayer.getMusicPlayer().start();
                        //set myplayer
                        myplayer.setPlaygID(previousID);
                        myplayer.setPlayingPath(next.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myplayer.getPlayingID() >= 0) {
                    if (myplayer.getMusicPlayer().isPlaying()) {
                        myplayer.getMusicPlayer().pause();
                        myplayer.setStatePlay(false);
                        play_pause.setBackgroundResource(R.drawable.play_main);

                    } else {
                        myplayer.getMusicPlayer().start();
                        myplayer.setStatePlay(true);
                        play_pause.setBackgroundResource(R.drawable.stop_main);
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveTaskToBack(true);
            }
        });
    }

    private class UIUpdater extends AsyncTask<Integer, Integer, String> {
        @Override
        protected void onPreExecute() {
            if (myplayer.getMusicPlayer() != null) {
                seekBar.setProgress(myplayer.getMusicPlayer().getCurrentPosition());
                Song songPlaying = myplayer.getPlaying(myplayer.getPlayingPath());
                int min, sec;
                String toadd;
                if (!songPlaying.equal(songShowing)) {
                    playSongName.setText(songPlaying.getSongName());
                    album.setImageBitmap(songPlaying.getAlbum());
                    int wholeTime = myplayer.getMusicPlayer().getDuration();
                    seekBar.setMax((int) songPlaying.getDuration());
                    min = wholeTime / 1000 / 60;
                    sec = (wholeTime / 1000) % 60;
                    toadd = (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec;
                    totalTime.setText(toadd);
                    songShowing = songPlaying;
                }
                if (myplayer.isPlaying()) {
                    play_pause.setBackgroundResource(R.drawable.stop_main);
                } else {
                    play_pause.setBackgroundResource(R.drawable.play_main);
                }
                int timeNow = myplayer.getMusicPlayer().getCurrentPosition();
                min = timeNow / 1000 / 60;
                sec = (timeNow / 1000) % 60;
                toadd = (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec;
                currentTime.setText(toadd);
            }
        }
        @Override
        protected void onPostExecute(String s) {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (myplayer.getMusicPlayer() != null) {

                seekBar.setProgress(myplayer.getMusicPlayer().getCurrentPosition());
                Song songPlaying = myplayer.getPlaying(myplayer.getPlayingPath());
                int min, sec;
                String toadd;
                if (!songPlaying.equal(songShowing)) {
                    playSongName.setText(songPlaying.getSongName());
                    album.setImageBitmap(songPlaying.getAlbum());
                    int wholeTime = myplayer.getMusicPlayer().getDuration();
                    seekBar.setMax((int) songPlaying.getDuration());
                    min = wholeTime / 1000 / 60;
                    sec = (wholeTime / 1000) % 60;
                    toadd = (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec;
                    totalTime.setText(toadd);
                    songShowing = songPlaying;
                }
                if (myplayer.isPlaying()) {
                    play_pause.setBackgroundResource(R.drawable.stop_main);
                } else {
                    play_pause.setBackgroundResource(R.drawable.play_main);
                }
                int timeNow = myplayer.getMusicPlayer().getCurrentPosition();
                min = timeNow / 1000 / 60;
                sec = (timeNow / 1000) % 60;
                toadd = (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec;
                currentTime.setText(toadd);
            } else {
                seekBar.setProgress(0);
            }
        }
        @Override
        protected String doInBackground(Integer... integers) {
            while (true) {
                try {
                    Thread.sleep(integers[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.publishProgress();
            }
        }
    }
    private class MySeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (myplayer.getMusicPlayer()!=null)
                myplayer.getMusicPlayer().seekTo(seekBar.getProgress());
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK&&event.getRepeatCount() == 0) {
            moveTaskToBack(true);
        }
        return false;
    }

}