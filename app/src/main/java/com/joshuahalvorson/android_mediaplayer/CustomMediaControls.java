package com.joshuahalvorson.android_mediaplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class CustomMediaControls extends LinearLayout{

    private Button playPauseButton;

    public CustomMediaControls(Context context) {
        super(context);
        init(null);
    }

    public CustomMediaControls(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomMediaControls(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public CustomMediaControls(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public void enablePlayPause(final MediaPlayer mediaPlayer){
        playPauseButton = new Button(getContext());
        addView(playPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }else{
                    mediaPlayer.start();
                }
            }
        });
    }

    public void init(AttributeSet attrs){
        setOrientation(HORIZONTAL);

    }
}
