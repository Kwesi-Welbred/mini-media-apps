package com.kwesiwelbred.kwplayer;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MediaPlayer mediaPlayer;

    private TextView songTitle;
    private TextView artistName;
    private TextView leftTime;
    private TextView rightTime;

    private Button preButton;
    private Button playButton;
    private Button nextButton;

    private SeekBar seekBar;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         setupView();
         seekBar.setMax(mediaPlayer.getDuration());

         seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                    timer();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            //mediaPlayer.start();
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            //mediaPlayer.stop();
            }
        });



    }

    //method for setting up the views
    public void setupView(){
        //finding view by id for the text views
        artistName = findViewById(R.id.artistName);
        songTitle  = findViewById(R.id.songTitle);
        leftTime   = findViewById(R.id.leftTime);
        rightTime  = findViewById(R.id.rightTime);

        //view by id for the button
        preButton  = findViewById(R.id.pre_button);
        playButton = findViewById(R.id.play_button);
        nextButton = findViewById(R.id.next_button);
        seekBar    = findViewById(R.id.seekBar2);

        preButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer  = MediaPlayer.create(getApplicationContext(), R.raw.dhtgirl);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_button:
                //do something
                if (mediaPlayer.isPlaying()) {
                    pauseMusic();
                }else playMusic();

                break;
            case R.id.pre_button:

                break;

            case R.id.next_button:

        }
    }

    public void playMusic(){
        if (mediaPlayer != null){
            mediaPlayer.start();
            durationTimer();
            playButton.setBackground(getResources().getDrawable(R.drawable.pause_play));
        }
    }

    public void pauseMusic(){
        if (mediaPlayer != null){
            mediaPlayer.pause();
            playButton.setBackground(getResources().getDrawable(R.drawable.circled_play));
        }
    }

    public void mediaSeekBack(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(0);
        }
    }

    public void mediaSeekForward() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }
    public void timer(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        int curPos = mediaPlayer.getCurrentPosition();
        int lastPos = mediaPlayer.getDuration();

        seekBar.setMax(lastPos);
        seekBar.setProgress(curPos);

        leftTime.setText(simpleDateFormat.format(new Date(curPos)));
        rightTime.setText(simpleDateFormat.format(new Date(lastPos-curPos)));
    }

    public void durationTimer(){
        thread = new Thread(){
            /**
             * If this thread was constructed using a separate
             * <code>Runnable</code> run object, then that
             * <code>Runnable</code> object's <code>run</code> method is called;
             * otherwise, this method does nothing and returns.
             * <p>
             * Subclasses of <code>Thread</code> should override this method.
             *
             * @see #start()
             * @see #stop()
             */
            @Override
            public void run() {
                try {
                    while (mediaPlayer.isPlaying()  && mediaPlayer != null){
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timer();
                            }
                        });
                    }
                }catch ( InterruptedException e){
                    e.getStackTrace();
                }
            }
        };
        thread.start();

    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer !=null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        thread.interrupt();
        thread =null;
        super.onDestroy();
    }
}
