package com.joshuahalvorson.android_mediaplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private CustomMediaControls customMediaControls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customMediaControls = findViewById(R.id.media_controls);


        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent, 1);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        customMediaControls.enablePlayPause(mediaPlayer);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mediaPlayer.release();
    }
}
