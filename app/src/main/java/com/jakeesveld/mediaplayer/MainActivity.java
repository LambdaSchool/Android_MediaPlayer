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

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final int AUDIO_REQUEST_CODE = 5;
    Button requestButton, playButton;
    MediaPlayer mediaPlayer;
Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        requestButton = findViewById(R.id.button_request);
        playButton = findViewById(R.id.button_play);
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
                mediaPlayer.start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == AUDIO_REQUEST_CODE){
            if(data != null) {
                Uri audioUri = data.getData();
                try {
                    mediaPlayer.setDataSource(context, audioUri);
                    mediaPlayer.prepare();
                    playButton.setEnabled(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
