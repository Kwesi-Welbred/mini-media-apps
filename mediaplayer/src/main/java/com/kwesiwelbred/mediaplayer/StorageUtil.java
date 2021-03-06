package com.kwesiwelbred.mediaplayer;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class StorageUtil {
    private final String STORAGE = "com.valdioveliu.audioplayer.STORAGE";
    private SharedPreferences preferences;
    private Context context;

    public StorageUtil(Context context){
        this.context = context;
    }

    public void storeAudio(ArrayList<AudioLocalFiles> audioLocalFilesArrayList){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson;
        String json = gson.toJson(audioLocalFilesArrayList);
        editor.putString("audioArrayList", json);
        editor.apply();
    }

    public ArrayList<AudioLocalFiles> loadAudio(){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson;
        String json = preferences.getString("audioArrayList", null);
        Type type = new TypeToken<ArrayList<AudioLocalFiles>>() {
        }.getType();
        return gson.fromJson(json , type);
    }

    public void storeAudioIndex(int index){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("audioIndex", index);
        editor.apply();
    }

    public int loadAudioIndex(){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getInt("audioIndex", -1);//return -ve 1 if no dat found
    }
    public void clearCachedAudioPlaylist(){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

}
