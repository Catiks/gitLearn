package com.example.ubuntu.happyeliminating;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends Activity {

    SongAdapter adapter;
    ListView songList;
    SharedMediaPlayer myplayer;
    ImageButton play_pause_btn;
    public MediaPlayer musicPlayer;
    Song songPlaying;
    ImageButton importbtn;
    ImageView album_main;
    TextView musicText;
    //获取读取权限
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //动态申请储存权限
        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myplayer = (SharedMediaPlayer)getApplicationContext();
        final MainUIUpdater myUpdater = new MainUIUpdater();
        songPlaying = null;
        album_main = (ImageView)findViewById(R.id.imageplaybaricon);
        musicText = (TextView) findViewById(R.id.musicInfo);
        importbtn = (ImageButton) findViewById(R.id.importsong);
        importbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.disableAllItemChoser();
                UpdateListView updateListView = new UpdateListView();
                updateListView.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        play_pause_btn = (ImageButton)findViewById(R.id.play_pause_btn);
        play_pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myplayer.getMusicPlayer() == null) {
                    Toast.makeText(MainActivity.this, "No music playing",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (myplayer.isPlaying()) {
                        myplayer.getMusicPlayer().pause();
                        myplayer.setStatePlay(false);
                        play_pause_btn.setBackgroundResource(R.drawable.play);
                    } else {
                        myplayer.getMusicPlayer().start();
                        myplayer.setStatePlay(true);
                        play_pause_btn.setBackgroundResource(R.drawable.pause);
                    }
                }
            }
        });
        adapter = new SongAdapter(new ArrayList<Song>(), MainActivity.this);
        songList = (ListView) findViewById(R.id.musiclist);
        songList.setAdapter(adapter);
        musicPlayer = null;
        myplayer.setMusicPlayer(null);
        myplayer.setPlaygID(-1);

        myUpdater.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 500);

        final TextView musicInfo = (TextView)findViewById(R.id.musicInfo);
        musicInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
            }
        });

        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int id, long l) {

                //final ImageView imageView = (ImageView)findViewById(R.id.imageplaybaricon);
                //final TextView textView = (TextView) findViewById(R.id.musicInfo);
                Song clickedSong = (Song) myplayer.getPlayList().get(id);
                album_main.setImageBitmap(clickedSong.getAlbum());
                musicText.setText(clickedSong.getSongName() + "--" + clickedSong.getArtist());
                songPlaying = clickedSong;

                if (myplayer.getMusicPlayer() == null) {
                    myplayer.setMusicPlayer(new MediaPlayer());
                    try {
                        myplayer.getMusicPlayer().setDataSource(clickedSong.getPath());
                        myplayer.getMusicPlayer().prepare();
                        myplayer.setPlaygID(id);
                        myplayer.setPlayingPath(clickedSong.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myplayer.getMusicPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            System.out.println("music play completed");
                            mediaPlayer.reset();
                            int nextID = myplayer.seekToNext();
                            Song toPlay = myplayer.getPlayList().get(nextID);
                            try {
                                mediaPlayer.setDataSource(toPlay.getPath());
                                myplayer.setPlayingPath(toPlay.getPath());
                                myplayer.setPlaygID(nextID);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            album_main.setImageBitmap(toPlay.getAlbum());
                            musicText.setText(toPlay.getSongName() + "--" + toPlay.getArtist());
                            try {
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                                myplayer.setStatePlay(true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    myplayer.getMusicPlayer().stop();
                    myplayer.getMusicPlayer().reset();

                    try {
                        //myplayer.setMusicPlayer(new MediaPlayer());
                        myplayer.getMusicPlayer().setDataSource(clickedSong.getPath());
                        myplayer.getMusicPlayer().prepare();
                        myplayer.setPlaygID(id);
                        myplayer.setPlayingPath(clickedSong.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                myplayer.getMusicPlayer().start();
                myplayer.setStatePlay(true);
                Toast.makeText(MainActivity.this, "Click " + id, Toast.LENGTH_LONG).show();
            }
        });

        adapter.setOnItemDeleteClickListener(new SongAdapter.onItemDeleteListener() {
            @Override
            public void onDeleteClick(int position) {
                if (myplayer.getPlayingID() == -1 || myplayer.getPlayingID() != position) {
                    if (position < myplayer.getPlayingID()) {
                        myplayer.setPlaygID(myplayer.getPlayingID() - 1);
                    }
                    myplayer.getPlayList().remove(position);
                    //adapter.delete(position);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this,
                            "This Music is playing now, you can remove it when it is not playing", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }
    //异步进程更新list
    private class UpdateListView extends AsyncTask<Integer, Song, Integer > {
        public int preSize;
        private int ignored;
        @Override
        protected void onPreExecute() {
            preSize = adapter.getCount();
            ignored = 0;
        }
        @Override
        protected void onPostExecute(Integer newCount) {
            if (newCount - preSize> 0) {
                Toast.makeText(MainActivity.this, "新添加 "+ (newCount - preSize) +
                        " 首歌曲, " + "忽略 "+ ignored + " 音频文件",Toast.LENGTH_LONG).show();
            } else if (newCount == 0) {
                Toast.makeText(MainActivity.this, "没有发现歌曲"+ "忽略 "+ ignored +" 音频文件",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "没有发现新歌曲"+ "忽略 "+ ignored+ " 音频文件",Toast.LENGTH_LONG).show();
            }
            myplayer.setPlayList(adapter.getSongs());
            adapter.enableItemChoser();
        }

        @Override
        protected void onProgressUpdate(Song... values) {
            adapter.add(values[0]);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            ArrayList<Song> songsToAdd;
            songsToAdd = new ArrayList<>();
            System.out.println("scan");
            songsToAdd.clear();
            String name;   //歌名
            String artist;  //歌手
            Bitmap bitmap;  //专辑海报
            long duration;  //时长
            String path_music = null;  //当前播放的歌曲路径
            ContentResolver musicResolver;
            musicResolver = getContentResolver();
            Cursor cursor = null;
            cursor = musicResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null, null, null, MediaStore.Audio.AudioColumns.IS_MUSIC);
            //第一个开始构造
            cursor.moveToFirst();
            int count = 0;
            for (int i = 0; i < cursor.getCount(); i++) {
                if (cursor != null && cursor.getCount() > 0) {
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    //获取文件大小
                    long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    //专辑id
                    long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                    bitmap = getMusicAlbumPicture(getApplicationContext(), id, albumId);
                    Song songToAdd = new Song(name, artist, url, duration, bitmap);
                    if (duration < (2*60 + 10) *1000 || duration > 15*60*1000) {
                        ignored ++;
                    } else {
                        this.publishProgress(songToAdd);
                    }
                    count++;
                }
                //下一个数据
                cursor.moveToNext();
            }
            cursor.close();
            return adapter.getCount();
        }
    }
    //封面图片
    static final Uri strAlbum = Uri.parse("content://media/external/audio/albumart");
    Bitmap getMusicAlbumPicture(Context applicationContext, long id, long albumId) {
        Bitmap album = null;
        System.out.println(id + " + " + albumId);
        if (id < 0&&albumId < 0) {
            //表示专辑id和歌曲id不存在
            throw new IllegalArgumentException(
                    "Invalid Music Id or Album Id");
        }
        try {
            if (albumId < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + id + "/albumart");
                ParcelFileDescriptor parcelFileDescriptor = applicationContext.getApplicationContext()
                        .getContentResolver().openFileDescriptor(uri, "r");
                if (parcelFileDescriptor != null) {
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    //解析出封面
                    album = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(strAlbum, albumId);
                ParcelFileDescriptor parcelFileDescriptor = applicationContext.getApplicationContext()
                        .getContentResolver().openFileDescriptor(uri, "r" );
                //说明没有封面
                if (parcelFileDescriptor != null) {
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    album = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                } else {
                    return null;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (album == null) {
            System.out.println("album = null");
            Resources resources = applicationContext.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.song_icon);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            album = bitmapDrawable.getBitmap();
        }
        return album.createScaledBitmap(album, 300,300, true);
    }
    private class MainUIUpdater extends AsyncTask<Integer, Integer, String> {
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

        @Override
        protected void onPostExecute(String s) {
        }
        @Override
        protected void onProgressUpdate(Integer...values) {
            if (myplayer.isPlaying()) {
                play_pause_btn.setBackgroundResource(R.drawable.pause);
            } else {
                play_pause_btn.setBackgroundResource(R.drawable.play);
            }
            if (myplayer.getPlayingID() >= 0) {
                Song playerSong = myplayer.getPlaying(myplayer.getPlayingPath());
                if (!playerSong.equal(songPlaying)) {
                    songPlaying = playerSong;
                    album_main.setImageBitmap(songPlaying.getAlbum());
                    musicText.setText(songPlaying.getSongName() + "--" + songPlaying.getArtist());
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        myplayer.getMusicPlayer().release();
        myplayer.setMusicPlayer(null);
        super.onDestroy();
    }
}


