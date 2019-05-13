package com.example.mediaplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
	
	
	public final static int FILE_REQUEST_CODE = 1;
	Button btnPlay;
	Button btnLoad;
	SeekBar seekBar;
	VideoView videoView;
	Runnable progressListenerRunnable;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK) {
			videoView.setVideoURI(data.getData());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnPlay = findViewById(R.id.btn_play);
		btnLoad = findViewById(R.id.btn_load);
		seekBar = findViewById(R.id.seekbar);
		videoView = findViewById(R.id.video_view);
		
		
		btnLoad.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("*/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, FILE_REQUEST_CODE);
			}
		});
		
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			
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
		
		videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				seekBar.setMax(videoView.getDuration());
			}
		});
		
		btnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (videoView.isPlaying()) {
					// stop
					videoView.pause();
					btnPlay.setText("Play");
				} else {
					// start
					videoView.start();
					btnPlay.setText("Stop");
					new Thread(progressListenerRunnable).start();
				}
				
			}
		});
		
		progressListenerRunnable = new
				
				Runnable() {
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
		
	}
}
