package com.example.liuqiang.medialearn.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.liuqiang.medialearn.R;
import com.example.liuqiang.medialearn.media.AudioCapture;
import com.example.liuqiang.medialearn.media.AudioPlayer;

public class MainActivity extends AppCompatActivity {
    private final int permissionRequestCode = 1;
    private AudioCapture audioCapture;
    private AudioPlayer audioPlayer;
    private String audioPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkPermission()){
            init();
        }
    }

    private void init(){
        audioCapture = new AudioCapture();
        audioPlayer = new AudioPlayer();
        audioPath = Environment.getExternalStorageDirectory() + "/audio.pcm";
    }

    private boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionRequestCode);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permissionRequestCode && grantResults.length != 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            init();
        } else {
            Toast.makeText(this, "未授权", Toast.LENGTH_LONG).show();
        }
    }

    public void startRecord(View view){
        if (audioCapture != null && audioPath != null){
            audioCapture.startRecord(audioPath);
        }
    }

    public void stopRecord(View view){
        if (audioCapture != null){
            audioCapture.stopRecord();
        }
    }

    public void initPlayer(View view){
        if (audioPlayer != null){
            audioPlayer.startPlayer();
        }
    }

    public void startPlay(View view){
        if (audioPlayer != null && audioPath != null){
            audioPlayer.play(audioPath);
        }
    }

    public void stopPlay(View view){
        if (audioPlayer != null){
            audioPlayer.stop();
        }
    }
}
