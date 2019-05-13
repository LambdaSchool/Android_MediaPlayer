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

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final int AUDIO_REQUEST_CODE = 5;
    Button requestButton, playButton;
    MediaPlayer mediaPlayer;
    SeekBar audioSeekbar;
    Context context;
    Runnable audioProgressListenerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        requestButton = findViewById(R.id.button_request);
        playButton = findViewById(R.id.button_play);
        audioSeekbar = findViewById(R.id.audio_seekbar);
        playButton.setEnabled(false);
        mediaPlayer = new MediaPlayer();

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent, AUDIO_REQUEST_CODE);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    playButton.setText("Resume Audio");
                }else {
                    mediaPlayer.start();
                    playButton.setText("Pause Audio");
                    new Thread(audioProgressListenerRunnable).start();
                }
            }
        });

        audioProgressListenerRunnable = new Runnable() {
            @Override
            public void run() {
                while(mediaPlayer.isPlaying()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            audioSeekbar.setProgress(mediaPlayer.getCurrentPosition());
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

        audioSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(audioSeekbar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                    mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUDIO_REQUEST_CODE) {
            if (data != null) {
                Uri audioUri = data.getData();
                try {
                    mediaPlayer.setDataSource(context, audioUri);
                    mediaPlayer.prepare();
                    playButton.setEnabled(true);
                    audioSeekbar.setVisibility(View.VISIBLE);
                    audioSeekbar.setMax(mediaPlayer.getDuration());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.release();
    }
}
