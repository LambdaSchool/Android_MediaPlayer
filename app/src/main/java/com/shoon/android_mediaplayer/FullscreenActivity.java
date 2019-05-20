package com.shoon.android_mediaplayer;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.VideoView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler  mHideHandler  = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                                               | View.SYSTEM_UI_FLAG_FULLSCREEN
                                               | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                               | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                               | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                               | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
//    private View    mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
//            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private static final String TAG = "FullscreenActivity";

    VideoView videoView;
    Button        controlButton;
    SeekBar       videoSeekBar;
    Runnable      progressListenerRunnable;
    View controlsLayout;
    private Runnable hideControlsRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
//        mControlsView = findViewById(R.id.fullscreen_content_controls);
        controlsLayout = findViewById(R.id.player_control_layout);
        hideControlsRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(videoView.isPlaying()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            controlsLayout.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        };

        // Set up the user interaction to manually show or hide the system UI.
        findViewById(R.id.fullscreen_toggle_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        videoView = findViewById(R.id.video_player);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(controlsLayout.getVisibility() == View.INVISIBLE) {
                    controlsLayout.setVisibility(View.VISIBLE);
                    new Thread(hideControlsRunnable).start();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        controlButton = findViewById(R.id.start_stop_button);
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    // stop
                    videoView.pause();
                    controlButton.setText(R.string.play);
                } else {
                    // start
                    videoView.start();
                    controlButton.setText(R.string.stop);
                    new Thread(progressListenerRunnable).start();
                    new Thread(hideControlsRunnable).start();
                }

            }
        });

        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.live_views_of_starman));
        final long startPrep = System.nanoTime();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                videoView.start();
                Log.i(TAG, "Video Prepared in: " + (System.nanoTime() - startPrep) / 1000000);

                videoSeekBar.setMax(mp.getDuration());

                controlButton.setText(R.string.play);
                controlButton.setEnabled(true);
            }
        });

        videoSeekBar = findViewById(R.id.video_seekbar);
        videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                /*if(fromUser) {
                    videoView.seekTo(progress);
                }*/

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                videoView.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoView.seekTo(seekBar.getProgress());
                videoView.start();
                new Thread(progressListenerRunnable).start();
            }
        });

        progressListenerRunnable = new Runnable() {
            @Override
            public void run() {
                while (videoView.isPlaying()) {
                    final int currentPosition = videoView.getCurrentPosition();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            videoSeekBar.setProgress(currentPosition);
                        }
                    });
                    try {
                        Thread.sleep(videoView.getDuration() / videoView.getWidth());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        controlsLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
//        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                           | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
