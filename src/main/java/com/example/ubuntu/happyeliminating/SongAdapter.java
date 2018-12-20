package com.example.ubuntu.happyeliminating;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class SongAdapter extends BaseAdapter {
    private ArrayList<Song> Songs;
    private Context myContext;
    private onItemDeleteListener myOnItemDeleteListener;
    boolean isAllItemEnable=true;
    public SongAdapter(ArrayList<Song> songs, Context context) {
        this.Songs = songs;
        this.myContext = context;
    }
    @Override
    public boolean isEnabled(int position) {
        return isAllItemEnable;
    }
    public void disableAllItemChoser() {
        isAllItemEnable = false;
        notifyDataSetChanged();
    }
    public void enableItemChoser() {
        isAllItemEnable = true;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return Songs.size();
    }
    public int getID(Song song) {
        for (int i = 0; i < this.Songs.size(); i++) {
            if (song.equal(this.Songs.get(i))){
                return i;
            }
        }
        return -1;
    }
    @Override
    public Object getItem(int position) {return Songs.get(position);}
    @Override
    public long getItemId(int position) {return position;}
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(myContext).inflate(R.layout.activity_musicview, parent, false);
        ImageView album= (ImageView)convertView.findViewById(R.id.music_icon);
        TextView musicinfo = (TextView)convertView.findViewById(R.id.one_music_info);
        //the delete button
        ImageButton deleteBtn = (ImageButton)convertView.findViewById(R.id.delete);
        deleteBtn.setFocusable(false);
        album.setImageBitmap(this.Songs.get(position).getAlbum());
        musicinfo.setText(this.Songs.get(position).getSongName() + "\n"
        + this.Songs.get(position).getArtist());
        /*if (!isAllItemEnable) {
            album.setEnabled(false);
            deleteBtn.setEnabled(false);
            musicinfo.setEnabled(false);
        }*/
        //set delete button
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myOnItemDeleteListener.onDeleteClick(position);
            }
        });
        return convertView;
    }
    //定义接口
    public interface onItemDeleteListener {
        void onDeleteClick(int position);
    }
    ArrayList<Song> getSongs() {
        return Songs;
    }
    public void setOnItemDeleteClickListener(onItemDeleteListener myOnItemDeleteListener) {
        this.myOnItemDeleteListener = myOnItemDeleteListener;
    }
    public void add(Song song) {
        boolean finded = false;
        for (int i = 0; i < Songs.size(); i++) {
            if (Songs.get(i).equal(song)) {
                finded = true;
                break;
            }
        }
        if (finded == false) {
            Songs.add(song);

        }
    }
    public void add(ArrayList<Song> songs) {
        boolean finded;
        for (int i = 0; i < songs.size(); i++) {
            finded = false;
            for (int j = 0; j < Songs.size(); j++) {
                if (Songs.get(j).equal( songs.get(i))){
                    finded = true;
                    break;
                }
            }
            if (finded != true) {
                Songs.add(songs.get(i));
            }
        }
    }
    public void delete(int position) {
        if (position < Songs.size()) {
            Songs.remove(position);
        }
    }
}

    