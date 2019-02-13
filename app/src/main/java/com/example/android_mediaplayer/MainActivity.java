package com.example.android_mediaplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /** final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.im_yours);

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();

               // System.out.println(mediaPlayer.isPlaying());

                if (mediaPlayer.isPlaying()) {
                   mediaPlayer.stop();
                    ((Button)v).setText("Play");
                } else {
                   mediaPlayer.start();
                    ((Button)v).setText("Stop");
                }
              //  System.out.println(mediaPlayer.isPlaying());
            }
        });
    }**/
       MediaPlayer mediaPlayer = new MediaPlayer();
       mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource("https://open.spotify.com/album/5i0gpUvEkQtwHWVkqh7MZK");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
