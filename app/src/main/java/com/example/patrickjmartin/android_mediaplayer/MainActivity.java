package com.example.patrickjmartin.android_mediaplayer;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    VideoView video;
    SeekBar seekBar;
    ImageButton rewButton, playButton, pauseButton, ffButton;
    Handler handler;
    double startTime, endTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        mediaPlayer = new MediaPlayer();
        video = findViewById(R.id.video_view);
        seekBar = findViewById(R.id.seekBar);
        rewButton = findViewById(R.id.media_rew);
        playButton = findViewById(R.id.media_play);
        pauseButton = findViewById(R.id.media_pause);
        ffButton = findViewById(R.id.media_ff);

        seekBar.setClickable(false);


        FloatingActionButton musicFab = findViewById(R.id.add_media_playable);
        musicFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/* video/*");
                startActivityForResult(intent, 1);
            }
        });

        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                seekBar.setClickable(true);

                startTime = mediaPlayer.getCurrentPosition();
                endTime = mediaPlayer.getDuration();

                seekBar.setMax((int) endTime);
                seekBar.setProgress((int)startTime);
                handler.postDelayed(updateSeekBar, 70);

            }
        });

        pauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
            }
        });


    }

    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            seekBar.setProgress((int)startTime);
            handler.postDelayed(this, 70);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        ContentResolver resolver = this.getContentResolver();
        String type = resolver.getType(uri);

        if(type.contains("audio")) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);



        } else {

        }



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
