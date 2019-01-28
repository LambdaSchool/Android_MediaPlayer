package com.example.android_mediaplayer;

import android.database.Cursor;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class PlayerActivity extends AppCompatActivity {

    public static final String SELECTED_MEDIA = "selection";
    private Uri uri;
    private MediaPlayer mediaPlayer;
    private ImageButton playButton;
    private Drawable buttonDrawable;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        uri = getIntent().getParcelableExtra(SELECTED_MEDIA);
        String displayText = "";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        displayText = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
        cursor.close();

        ((TextView) findViewById(R.id.player_text_name)).setText(displayText);

        playButton = findViewById(R.id.player_button_play);
        playButton.setEnabled(false);

        seekBar = findViewById(R.id.player_seekbar);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(this, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playButton.setEnabled(true);
                seekBar.setMax(mp.getDuration());
//                playButton.performClick();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    buttonDrawable = getDrawable(R.drawable.avd_anim_stop_to_play);
                    mediaPlayer.pause();
                } else {
                    buttonDrawable = getDrawable(R.drawable.avd_anim_play_to_stop);
                    mediaPlayer.start();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (mediaPlayer.isPlaying()) {
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }
                playButton.setImageDrawable(buttonDrawable);
                if (buttonDrawable instanceof Animatable) {
                    ((Animatable) buttonDrawable).start();
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                buttonDrawable = getDrawable(R.drawable.avd_anim_stop_to_play);
                playButton.setImageDrawable(buttonDrawable);
                if (buttonDrawable instanceof Animatable) {
                    ((Animatable) buttonDrawable).start();
                }
                seekBar.setProgress(0);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
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

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.release();
    }

}
