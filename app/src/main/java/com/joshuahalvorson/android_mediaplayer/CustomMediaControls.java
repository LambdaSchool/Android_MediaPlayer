package com.joshuahalvorson.android_mediaplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class CustomMediaControls extends LinearLayout{

    private Button playPauseButton;
    private SeekBar seekBar;

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

    public void enableMediaControl(final MediaPlayer mediaPlayer){
        playPauseButton = new Button(getContext());
        playPauseButton.setText("Play");
        addView(playPauseButton);
        playPauseButton.setEnabled(false);

        seekBar = new SeekBar(getContext());
        seekBar.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(seekBar);

        mediaPlayer.prepareAsync();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playPauseButton.setEnabled(true);
                seekBar.setMax(mp.getDuration());
            }
        });

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                seekBar.setSecondaryProgress(percent);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    playPauseButton.setText("Pause");
                    mediaPlayer.stop();
                }else{
                    playPauseButton.setText("Play");
                    mediaPlayer.start();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (mediaPlayer.isPlaying()){
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                try {
                                    Thread.sleep(250);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }
            }
        });
    }

    public void init(AttributeSet attrs){
        setOrientation(HORIZONTAL);
        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
