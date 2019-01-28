package com.example.android_mediaplayer;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PlayerActivity extends AppCompatActivity {

    public static final String SELECTED_MEDIA = "selection";
    Uri uri;

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
    }
}
