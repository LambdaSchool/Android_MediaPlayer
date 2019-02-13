package com.example.android_mediaplayer;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.im_yours);

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    ((Button)v).setText("Play");
                } else {
                    mediaPlayer.start();
                    ((Button)v).setText("Stop");
                }
            }
        });
    }
}
