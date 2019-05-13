package com.example.android_mediaplayer;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;

public class PlayerActivity extends AppCompatActivity {

    public static final String SELECTED_MEDIA = "selection";
    private Uri uri;
    private MediaPlayer mediaPlayer;
    private ImageButton playButton;
    private Drawable buttonDrawable;
    private SeekBar seekBar;
    boolean isVideo = false;
    boolean isPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        uri = getIntent().getParcelableExtra(SELECTED_MEDIA);

        if (uri.toString().contains("video")) {
            isVideo = true;
        }

        String displayText = "";
        String albumId = "";
        String albumImagePath = "";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            displayText = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
        }
        cursor.close();


/*        cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + "=?",
               null,
                null);
        if (cursor.moveToFirst()) {
            albumImagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
        }
        cursor.close();
        if (albumImagePath != "") {
            final Bitmap bitmap = BitmapFactory.decodeFile(albumImagePath);
            if (bitmap!=null){
                ((ImageView) findViewById(R.id.player_image_album)).setImageBitmap(bitmap);
            }
        }*/

        final VideoView videoView = findViewById(R.id.player_video);
        mediaPlayer = new MediaPlayer();

        final MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                buttonDrawable = getDrawable(R.drawable.avd_anim_stop_to_play);
                playButton.setImageDrawable(buttonDrawable);
                if (buttonDrawable instanceof Animatable) {
                    ((Animatable) buttonDrawable).start();
                }
                seekBar.setProgress(0);
                isPlaying = false;
            }
        };

        final MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playButton.setEnabled(true);
                seekBar.setMax(mp.getDuration());
                mediaPlayer.start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isPlaying) {
                            int position = isVideo ? videoView.getCurrentPosition() : mediaPlayer.getCurrentPosition();
                            seekBar.setProgress(position);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        };

        if (isVideo) {
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(uri);
//            MediaController mediaController = new MediaController(this);
//            mediaController.setAnchorView(videoView);
//            videoView.setMediaController(mediaController);
            videoView.setOnCompletionListener(completionListener);
            videoView.setOnPreparedListener(preparedListener);
        } else {
            videoView.setVisibility(View.GONE);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(this, uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(preparedListener);

            mediaPlayer.setOnCompletionListener(completionListener);

        }

        ((TextView) findViewById(R.id.player_text_name)).setText(displayText);

        playButton = findViewById(R.id.player_button_play);
        playButton.setEnabled(false);

        seekBar = findViewById(R.id.player_seekbar);


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    buttonDrawable = getDrawable(R.drawable.avd_anim_stop_to_play);
                    if (isVideo) {
                        videoView.pause();
                    } else {
                        mediaPlayer.pause();
                    }
                    isPlaying = false;
                } else {
                    buttonDrawable = getDrawable(R.drawable.avd_anim_play_to_stop);
                    if (isVideo) {
                        videoView.start();
                    } else {
                        mediaPlayer.start();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (isPlaying) {
                                int position = isVideo ? videoView.getCurrentPosition() : mediaPlayer.getCurrentPosition();
                                seekBar.setProgress(position);
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    isPlaying = true;
                }
                playButton.setImageDrawable(buttonDrawable);
                if (buttonDrawable instanceof Animatable) {
                    ((Animatable) buttonDrawable).start();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (isVideo) {
                        videoView.seekTo(progress);
                    } else {
                        mediaPlayer.seekTo(progress);
                    }
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
