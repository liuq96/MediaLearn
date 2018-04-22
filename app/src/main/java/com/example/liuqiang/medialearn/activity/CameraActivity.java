package com.example.liuqiang.medialearn.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.liuqiang.medialearn.R;
import com.example.liuqiang.medialearn.media.CameraHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private Camera mCamera;
    private TextureView mPreview;
    private MediaRecorder mMediaRecorder;
    private File mOutputFile;
    private boolean isRecording = false;
    private static final String TAG = "Recorder";
    private Button captureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mPreview = findViewById(R.id.id_texture_view);
        captureButton = findViewById(R.id.id_button_capture);
        mPreview.setSurfaceTextureListener(this);
    }

    private boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            return false;
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

    public void onCaptureClick(View view){
        if (checkPermission()) {
            startRecord();
        }
    }

    private void startRecord(){
        if (isRecording){
            try {
                mMediaRecorder.stop();
            } catch (RuntimeException e){
                Log.d(TAG, "RuntimeException: stop() is called immediately after start()");
                mOutputFile.delete();
            }
            releaseMediaRecorder();
            mCamera.lock();
            setCaptureButtonText("Capture");
            isRecording = false;
            releaseCamera();
            Intent intent = new Intent(this, VideoActivity.class);
            intent.putExtra("video_path", mOutputFile.getPath());
            startActivity(intent);
        } else {
            new MediaPrepareTask().execute(null, null, null);
        }
    }

    private boolean prepareVideoRecorder(){
        mCamera = CameraHelper.getDefaultFontFacingCameraInstance();
        //获取最佳的视频宽高
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();
        int width = Math.max(mPreview.getWidth(), mPreview.getHeight());
        int height = Math.min(mPreview.getWidth(), mPreview.getHeight());
        Camera.Size optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes, mSupportedPreviewSizes,
                width, height);

        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        profile.videoFrameWidth = optimalSize.width;
        profile.videoFrameHeight = optimalSize.height;
        //设置视频宽高
        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
        mCamera.setParameters(parameters);

        try {
            mCamera.setPreviewTexture(mPreview.getSurfaceTexture());
        } catch (IOException e) {
            Log.e(TAG, "Surface texture is unavailable or unsuitable: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        mMediaRecorder = new MediaRecorder();
        //设置摄像头
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        //设置Source
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //设置Profile
        mMediaRecorder.setProfile(profile);

        mOutputFile = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO);
        if (mOutputFile == null){
            return false;
        }
        //设置路径
        mMediaRecorder.setOutputFile(mOutputFile.getPath());

        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Exception preparing MediaRecorder: " + e.getMessage());
            e.printStackTrace();
            releaseMediaRecorder();
            return false;
        }

        return true;
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null){
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    private void setCaptureButtonText(String s){
        captureButton.setText(s);
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaRecorder();
        releaseCamera();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        if (mMediaRecorder != null){
            CameraHelper.setCameraDisplayOrientation(mCamera, CameraActivity.this, Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
    }

    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (prepareVideoRecorder()){
                mMediaRecorder.start();
                isRecording = true;
            } else {
                releaseMediaRecorder();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!aBoolean){
                CameraActivity.this.finish();
            }
            setCaptureButtonText("Stop");

        }
    }
}
