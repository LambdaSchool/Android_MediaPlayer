package com.joshuahalvorson.android_mediaplayer;

import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private LinearLayout parent_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parent_view = findViewById(R.id.parent_view);

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
        mediaPlayer = new MediaPlayer();
        Uri uri = null;
        String fileName = "";
        if (data != null) {
            uri = data.getData();
            if(mediaPlayer != null){
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(getApplicationContext(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (uri.toString().startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                }
            }
            CustomMediaControls customMediaControls = new CustomMediaControls(getApplicationContext(), mediaPlayer, fileName);
            parent_view.removeAllViews();
            parent_view.addView(customMediaControls);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
