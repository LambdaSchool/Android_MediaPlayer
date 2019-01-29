package com.example.mediaplayer.mediaPlayer;

public abstract class PlaybackInfoListener {
    void onDurationChanged(int duration){}
    void onPositionChanged(int position){}
    void onPlaybackCompleted(){}
}
