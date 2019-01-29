package com.example.mediaplayer.mediaPlayer;

import android.content.Context;
import android.media.MediaPlayer;

public class MyMediaPlayer implements PlayerAdapter {
    public enum State{PLAYING, PAUSED, STOP, COMPLETED}

    private Context context;
    private MediaPlayer mediaPlayer;

    @Override
    public void loadSong(int songId) {

    }

    @Override
    public void release() {
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean isPlaying() {
        if (mediaPlayer != null){
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    public MyMediaPlayer(Context context) {
        this.context = context;
    }

    @Override
    public void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    @Override
    public void stop() {
        if (mediaPlayer != null){
            mediaPlayer.stop();
        }
    }

    @Override
    public void initializeProgress() {

    }

    @Override
    public void seekTo(int position) {
        if (mediaPlayer != null){
            mediaPlayer.seekTo(position);
        }
    }
}
