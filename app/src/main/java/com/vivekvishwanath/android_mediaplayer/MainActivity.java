package com.vivekvishwanath.android_mediaplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    Button audioRequestButton;
    Button videoRequestButton;
    VideoView videoView;
    Button controlButton;
    SeekBar progressBar;
    Thread progressListenerThread;
    Runnable progressListenerRunnable;
    private static final int MEDIA_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.media_player);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                controlButton.setText(R.string.play);
                controlButton.setEnabled(true);
                progressBar.setMax(mp.getDuration());
                progressListenerThread.start();
            }
        });

        audioRequestButton = findViewById(R.id.audio_request_button);
        audioRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent, MEDIA_REQUEST_CODE);
            }
        });

        videoRequestButton = findViewById(R.id.video_request_button);
        videoRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, MEDIA_REQUEST_CODE);
            }
        });

        controlButton = findViewById(R.id.pause_play_button);
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    controlButton.setText(R.string.play);
                } else {
                    videoView.start();
                    controlButton.setText(R.string.pause);
                    new Thread(progressListenerRunnable).start();
                }
            }
        });

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoView.seekTo(seekBar.getProgress());
                new Thread(progressListenerRunnable).start();
            }
        });

        progressListenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (videoView.isPlaying()) {
                    final int currentPosition = videoView.getCurrentPosition();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(currentPosition);
                        }
                    });
                    try {
                        Thread.sleep(videoView.getDuration() / 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        progressListenerRunnable = new Runnable() {
            @Override
            public void run() {
                while (videoView.isPlaying()) {
                    final int currentPosition = videoView.getCurrentPosition();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(currentPosition);
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MEDIA_REQUEST_CODE && resultCode == RESULT_OK) {
            videoView.setVideoURI(data.getData());
            videoView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.suspend();
    }

    @Override
    protected void onStart() {
        super.onStart();
        videoView.resume();
    }
}
