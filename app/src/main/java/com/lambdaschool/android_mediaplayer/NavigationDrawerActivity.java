package com.lambdaschool.android_mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.text.MessageFormat;

public class NavigationDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    TextView textView;
    VideoView videoView;
    ImageButton imageButton;
    Runnable audioProgressListenerRunnable;
    Runnable videoProgressListenerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        textView = findViewById(R.id.text_view);

        videoView = findViewById(R.id.video_view);

        seekBar = findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (mediaPlayer != null)
                        mediaPlayer.seekTo(progress);
                    else if (videoView != null)
                        videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                    new Thread(audioProgressListenerRunnable).start();
                } else if (videoView!=null) {
                    videoView.seekTo(seekBar.getProgress());
                    new Thread(videoProgressListenerRunnable).start();
                }
            }
        });
        audioProgressListenerRunnable = new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer.isPlaying()) {
                    final int currentPosition = mediaPlayer.getCurrentPosition();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setProgress(currentPosition);
                        }
                    });
                    try {
                        Thread.sleep(mediaPlayer.getDuration() / 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        videoProgressListenerRunnable = new Runnable() {
            @Override
            public void run() {
                while (videoView.isPlaying()) {
                    final int currentPosition = videoView.getCurrentPosition();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setProgress(currentPosition);
                        }
                    });
                    try {
                        Thread.sleep(videoView.getDuration() / videoView.getWidth());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        imageButton = findViewById(R.id.image_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer!=null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        imageButton.setImageResource(android.R.drawable.ic_media_play);
                        new Thread(audioProgressListenerRunnable).interrupt();
                    } else {
                        mediaPlayer.start();
                        imageButton.setImageResource(android.R.drawable.ic_media_pause);
                        new Thread(audioProgressListenerRunnable).start();
                    }
                } else if (videoView!=null) {
                    if (videoView.isPlaying()) {
                        videoView.pause();
                        imageButton.setImageResource(android.R.drawable.ic_media_play);
                        new Thread(videoProgressListenerRunnable).interrupt();
                    } else {
                        videoView.start();
                        imageButton.setImageResource(android.R.drawable.ic_media_pause);
                        new Thread(videoProgressListenerRunnable).start();
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_audio) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(intent, 1);
        } else if (id == R.id.nav_video) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            startActivityForResult(intent, 2);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {

            Uri pickedMedia = data.getData();

            if (pickedMedia != null) {

                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer=null;
                }

                if (videoView!=null)
                    videoView.stopPlayback();

                if (requestCode == 1) {

                    mediaPlayer = MediaPlayer.create(this, pickedMedia);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            imageButton.setImageResource(android.R.drawable.ic_media_play);
                        }
                    });

                    videoView.setVisibility(View.INVISIBLE);

                    textView.setText(MessageFormat.format("Loaded: {0}", new File(pickedMedia.getPath()).getName()));
                    textView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);

                    seekBar.setVisibility(View.VISIBLE);
                    seekBar.setMax(mediaPlayer.getDuration());
                    seekBar.setProgress(0);

                    imageButton.setVisibility(View.VISIBLE);
                    imageButton.setImageResource(android.R.drawable.ic_media_play);

                } else if (requestCode == 2) {

                    if (videoView != null) {
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setVideoURI(pickedMedia);
                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                seekBar.setVisibility(View.VISIBLE);
                                seekBar.setMax(videoView.getDuration());
                                seekBar.setProgress(0);
                            }
                        });
                        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                imageButton.setImageResource(android.R.drawable.ic_media_play);
                            }
                        });
                    }

                    textView.setText(MessageFormat.format("Loaded: {0}", new File(pickedMedia.getPath()).getName()));
                    textView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);

                    imageButton.setVisibility(View.VISIBLE);
                    imageButton.setImageResource(android.R.drawable.ic_media_play);
                }
            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mediaPlayer != null)
            mediaPlayer.reset();

        if (videoView != null)
            videoView.stopPlayback();
    }
}
