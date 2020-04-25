package com.kwesiwelbred.mediaplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
   /* private MediaPlayer mediaPlayer ;
    private Button button;
    private SeekBar seekBar;
    private String mediaDur;

    */

   private MediaPlayerService mediaPlayer; // instant of the mediaService
   boolean serviceBound = false;// media service status: bound or not bound to the activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     /*
        button = findViewById(R.id.playbtnID);
        seekBar = findViewById(R.id.seekBar);

        mediaPlayer = new MediaPlayer();
       // mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.godkeeps);
        seekBar.setMax(mediaPlayer.getDuration());
        
        //structuring your seek bar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
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
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //getting the duration in seconds of the media player
                int dur = mp.getDuration();
                 mediaDur = String.valueOf(dur/1000);
                Toast.makeText(MainActivity.this, mediaDur, Toast.LENGTH_SHORT).show();
            }
        });
       // seekBar.setMax(Integer.parseInt(mediaDur));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    //pause and start again
                    playPause();

                }else {
                    playStart();
                }
            }
        });
   */
    playAudio(String.valueOf(" "));// can add url to be streamed media online
    playAudio(audioLocalFilesArrayList.get(0).getData());// play from the local files
        loadAudio();
    }

    //binding this client to the AudioPlayer service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //MediaPlayerService.LOCATION_SERVICE binder = (MediaPlayerService.LOCATION_SERVICE) service;
            //mediaPlayer = Binder.getService();
            serviceBound = true;
            Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
    /*
    public void playPause(){
        if (mediaPlayer !=null){
        mediaPlayer.pause();
        button.setText(R.string.mediastart);
        }
    }
    public void playStart() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            button.setText(R.string.mediapause);
        }
    }
    public void playStop(){
        if (mediaPlayer !=null){
            mediaPlayer.stop();
            button.setText(R.string.mediastop);
        }
    }

    @Override
    protected void onDestroy() {
        //freeing up the memory
        if (mediaPlayer != null && mediaPlayer.isPlaying() ){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;

        }
        super.onDestroy();
    }

     */
    // creating an instance of the media service and send it to the main activity for play
    private void playAudio(String media){
        //check if service is active
        if (! serviceBound){
            Intent intent = new Intent(this,MediaPlayerService.class);
            intent.putExtra("media",media);
            startService(intent);

            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else {
            // service is active
            // send media with broadcastReceiver
        }
    }
        // just calling the playAudio function on the onCreateMethod would work fine; the app can crash so easily. so therefore apply these override methods
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("serviceState", serviceBound);
        super.onSaveInstanceState(outState);
    }

    /**
     * This method is called after {@link #onStart} when the activity is
     * being re-initialized from a previously saved state, given here in
     * <var>savedInstanceState</var>.  Most implementations will simply use {@link #onCreate}
     * to restore their state, but it is sometimes convenient to do it here
     * after all of the initialization has been done or to allow subclasses to
     * decide whether to use your default implementation.  The default
     * implementation of this method performs a restore of any view state that
     * had previously been frozen by {@link #onSaveInstanceState}.
     *
     * <p>This method is called between {@link #onStart} and
     * {@link #onPostCreate}.
     *
     * @param savedInstanceState the data most recently supplied in {@link #onSaveInstanceState}.
     * @see #onCreate
     * @see #onPostCreate
     * @see #onResume
     * @see #onSaveInstanceState
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("serviceState", serviceBound);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound){
            unbindService(serviceConnection);
            //active service
            mediaPlayer.stopSelf();
        }
    }
    // Extras: loading Local Audio Files using contentResolver
    ArrayList<AudioLocalFiles> audioLocalFilesArrayList;
    public void loadAudio() {

        //load the audio files to recyclerView
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        String sortOrder = MediaStore.Audio.Media.TITLE + "ASC";

        try {

            Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);
            if (cursor != null && cursor.getCount() > 0) {
                audioLocalFilesArrayList = new ArrayList<>();
                while (cursor.moveToNext()) {

                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    // save to the audioArrayList
                    audioLocalFilesArrayList.add(new AudioLocalFiles(data, title, album, artist));
                }
            }

            assert cursor != null;
            cursor.close();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

}
