package com.example.liuqiang.medialearn;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "VideoRecord";
    private final int PERMISSION_REQUEST_CODE = 1;
    private SurfaceView surfaceView;
    private MediaRecorder mediaRecorder;
    private Button startRecordButton, stopRecordButton;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        surfaceView = findViewById(R.id.id_surface_view);
        startRecordButton = findViewById(R.id.id_button_start_record_video);
        stopRecordButton = findViewById(R.id.id_button_stop_record_video);
        startRecordButton.setOnClickListener(this);
        stopRecordButton.setOnClickListener(this);
        stopRecordButton.setEnabled(false);
    }

    private boolean checkPermission(){
        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            return false;
        }*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length != 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startRecord();
        } else {
            Toast.makeText(this, "未授权", Toast.LENGTH_LONG).show();
        }
    }

    public void startRecord(){
        if (checkPermission()){
            String path = this.getFilesDir()
                    + "/test.3gp";
            File videoFile = new File(path);
            surfaceView.getHolder().setKeepScreenOn(true);
            mediaRecorder = new MediaRecorder();
            mediaRecorder.reset();
            //必须定义这两个设置
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            //使用系统配置
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
            //使用自定义配置，注意系统配置和自定义配置两种会冲突
            /*mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.setVideoFrameRate(16);
            mediaRecorder.setVideoSize(1280, 720);*/
            mediaRecorder.setOrientationHint(90);
            mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
            mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = true;
                startRecordButton.setEnabled(false);
                stopRecordButton.setEnabled(true);
            } catch (IOException e) {
                Log.d(TAG, "error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "未授权", Toast.LENGTH_LONG).show();
        }
    }

    public void stopRecord(){
        if (mediaRecorder != null && isRecording){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            stopRecordButton.setEnabled(false);
            startRecordButton.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_button_start_record_video:{
                startRecord();
                break;
            }
            case R.id.id_button_stop_record_video:{
                stopRecord();
                startActivity(new Intent(this, VideoActivity.class));
                break;
            }
            default:
                break;
        }
    }
}
