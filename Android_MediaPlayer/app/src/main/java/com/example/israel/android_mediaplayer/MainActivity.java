package com.example.israel.android_mediaplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private Button playPauseButton;
    private SeekBar mediaPlayerSeekBar;
    private TextView timeDurationTextView;
    private TextView timeCurrentTextView;

    private boolean isPlaying;
    private MediaPlayer mediaPlayer;
    private Uri audioUri;
    private Timer mediaPlayerPositionTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button getAudioButton = findViewById(R.id.button_get_audio);
        getAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");

                ComponentName a = intent.resolveActivity(getPackageManager());
                if (a != null) {
                    startActivityForResult(intent, 1);
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, "No activity", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        playPauseButton = findViewById(R.id.button_play_pause);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    pause();
                } else {
                    play();
                }
            }
        });

        mediaPlayerSeekBar = findViewById(R.id.seek_bar_media_player);
        mediaPlayerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
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

        timeDurationTextView = findViewById(R.id.text_time_duration);
        timeCurrentTextView = findViewById(R.id.text_time_current);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            if (mediaPlayerPositionTimer != null) {
                mediaPlayerPositionTimer.cancel();
            }

            audioUri = data.getData();
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            mediaPlayer = MediaPlayer.create(MainActivity.this, audioUri);
            int duration = mediaPlayer.getDuration();
            mediaPlayerSeekBar.setMax(duration);
            mediaPlayerSeekBar.setProgress(0);
            setDurationTextViewTime(duration);

            mediaPlayerPositionTimer = new Timer();
            mediaPlayerPositionTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int currentPosition = mediaPlayer.getCurrentPosition();
                            mediaPlayerSeekBar.setProgress(currentPosition);
                            setCurrentTextViewTime(currentPosition);

                            if (currentPosition >= mediaPlayer.getDuration()) {
                                pause();
                            }
                        }
                    });
                }
            }, 0, 1000);
        }
    }

    private void play() {
        if (mediaPlayer == null || isPlaying) {
            return;
        }
        isPlaying = true;

        playPauseButton.setText("Pause");

        if (mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration()) {
            mediaPlayer.seekTo(0);
            setCurrentTextViewTime(0);
        }
        mediaPlayer.start();
    }

    private void pause() {
        if (mediaPlayer == null || !isPlaying) {
            return;
        }
        isPlaying = false;

        playPauseButton.setText("Play");

        mediaPlayer.pause();
    }

    private void setDurationTextViewTime(int duration) {
        String timeStr = String.format(Locale.US, "/%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));
        timeDurationTextView.setText(timeStr);
    }

    private void setCurrentTextViewTime(int current) {
        String timeStr = String.format(Locale.US, "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(current),
                TimeUnit.MILLISECONDS.toMinutes(current) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(current) % TimeUnit.MINUTES.toSeconds(1));
        timeCurrentTextView.setText(timeStr);
    }


}
