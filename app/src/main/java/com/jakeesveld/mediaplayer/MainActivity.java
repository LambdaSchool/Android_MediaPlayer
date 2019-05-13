package com.jakeesveld.mediaplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.VideoView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final int AUDIO_REQUEST_CODE = 5;
    public static final int VIDEO_REQUEST_CODE = 14;
    Button requestButton, playButton, videoRequestButton, videoPlayButton;
    MediaPlayer audioPlayer;
    SeekBar audioSeekbar, videoSeekbar;
    VideoView videoView;
    Context context;
    Runnable audioProgressListenerRunnable, videoProgressListenerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        requestButton = findViewById(R.id.button_request);
        playButton = findViewById(R.id.button_play);
        audioSeekbar = findViewById(R.id.audio_seekbar);
        videoRequestButton = findViewById(R.id.button_video_request);
        videoPlayButton = findViewById(R.id.button_video_play);
        videoSeekbar = findViewById(R.id.video_seekbar);
        videoView = findViewById(R.id.video_view);
        videoPlayButton.setEnabled(false);
        playButton.setEnabled(false);
        audioPlayer = new MediaPlayer();

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent, AUDIO_REQUEST_CODE);
            }
        });

        videoRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, VIDEO_REQUEST_CODE);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audioPlayer.isPlaying()){
                    audioPlayer.pause();
                    playButton.setText("Resume Audio");
                }else {
                    audioPlayer.start();
                    playButton.setText("Pause Audio");
                    new Thread(audioProgressListenerRunnable).start();
                }
            }
        });

        videoPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoView.isPlaying()){
                    videoView.pause();
                    videoPlayButton.setText("Resume video");
                }else{
                    videoView.start();
                    videoPlayButton.setText("Pause Video");
                    new Thread(videoProgressListenerRunnable).start();
                }
            }
        });

        audioProgressListenerRunnable = new Runnable() {
            @Override
            public void run() {
                while(audioPlayer.isPlaying()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            audioSeekbar.setProgress(audioPlayer.getCurrentPosition());
                        }
                    });
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        videoProgressListenerRunnable = new Runnable() {
            @Override
            public void run() {
                while(videoView.isPlaying()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            videoSeekbar.setProgress(videoView.getCurrentPosition());
                        }
                    });
                    try {
                        Thread.sleep(videoView.getDuration() / videoView.getWidth());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        audioSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    audioPlayer.seekTo(audioSeekbar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                    audioPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                audioPlayer.start();
            }
        });

        videoSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    videoView.seekTo(videoSeekbar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUDIO_REQUEST_CODE) {
            if (data != null) {
                Uri audioUri = data.getData();
                if(audioUri != null) {
                    try {
                        audioPlayer.setDataSource(context, audioUri);
                        audioPlayer.prepare();
                        playButton.setEnabled(true);
                        audioSeekbar.setVisibility(View.VISIBLE);
                        audioSeekbar.setMax(audioPlayer.getDuration());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if(requestCode == VIDEO_REQUEST_CODE){
            if(data != null) {
                Uri videoUri = data.getData();
                videoView.setVideoURI(videoUri);
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        videoPlayButton.setEnabled(true);
                        videoSeekbar.setVisibility(View.VISIBLE);
                        videoSeekbar.setMax(videoView.getDuration());
                    }
                });
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        audioPlayer.release();
        videoView.stopPlayback();
    }
}
