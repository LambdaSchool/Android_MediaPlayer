package com.joshuahalvorson.android_mediaplayer;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class CustomMediaControls extends LinearLayout{

    private ImageView playPause;
    private SeekBar seekBar;
    private int currentPos;

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

    public void enableMediaControl(final MediaPlayer mediaPlayer, String trackInfo){
        TextView titleText = new TextView(getContext());
        titleText.setText(trackInfo);
        addView(titleText);
        playPause = new ImageView(getContext());
        playPause.setImageDrawable(getResources().getDrawable(R.drawable.avd_anim_play_pause));
        playPause.setLayoutParams(new ViewGroup.LayoutParams(
                200,
                200));
        Drawable drawable = playPause.getDrawable();
        if(drawable instanceof Animatable){
            ((Animatable) drawable).start();
        }

        addView(playPause);

        seekBar = new SeekBar(getContext());
        seekBar.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(seekBar);

        mediaPlayer.prepareAsync();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playPause.setEnabled(true);
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

        playPause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    playPause.setImageDrawable(getResources().getDrawable(R.drawable.avd_anim_play_pause));
                    currentPos = mediaPlayer.getCurrentPosition();
                    mediaPlayer.pause();
                }else {
                    playPause.setImageDrawable(getResources().getDrawable(R.drawable.avd_anim_pause_play));
                    mediaPlayer.seekTo(currentPos);
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
                Drawable drawable = playPause.getDrawable();
                if(drawable instanceof Animatable){
                    ((Animatable) drawable).start();
                }
            }
        });
    }

    public void init(AttributeSet attrs){
        setOrientation(VERTICAL);
        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
