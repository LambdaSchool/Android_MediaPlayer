package com.example.mediaplayer.mediaPlayer;

public interface PlayerAdapter {
    void loadSong(int songId);
    void release();
    boolean isPlaying();
    void play();
    void pause();
    void stop();
    void initializeProgress();
    void seekTo(int position);
}
