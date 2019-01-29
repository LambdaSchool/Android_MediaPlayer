package com.example.mediaplayer.mediaPlayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;

public class MyMediaPlayer implements PlayerAdapter {
    private int songId;

    public enum State{PLAYING, PAUSED, STOP, COMPLETED}

    private Context context;
    private MediaPlayer mediaPlayer;
    private PlaybackInfoListener playbackInfoListener;

    public void setPlaybackInfoListener(PlaybackInfoListener listener){
        this.playbackInfoListener = listener;
    }

    private void initializePlayer(){
        if (mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (playbackInfoListener != null){
                        playbackInfoListener.onPlaybackCompleted();
                    }
                }
            });
        }
    }

    @Override
    public void loadSong(int songId) {
        this.songId = songId;
        initializePlayer();

        AssetFileDescriptor assetFile = context.getResources().openRawResourceFd(songId);

        try {
            mediaPlayer.setDataSource(assetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
