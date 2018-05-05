package com.example.liuqiang.medialearn.ui;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.liuqiang.medialearn.R;
import com.example.liuqiang.medialearn.media.CameraHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CameraFragment extends Fragment implements View.OnClickListener, Camera.PictureCallback {

    private static final String TAG = "CameraFragment";
    private Camera mCamera;
    private CameraPreview mPreview;
    private Button takePhotoBtn, resetBtn,confirmBtn;
    private LinearLayout cameraLayout;
    private FrameLayout frameLayout;
    private byte[] mData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        mPreview = new CameraPreview(this.getContext(), mCamera);
        View view = inflater.inflate(R.layout.fragment_capture, container, false);
        frameLayout = view.findViewById(R.id.id_preview);
        frameLayout.addView(mPreview);
        cameraLayout = view.findViewById(R.id.id_layout_camera);
        takePhotoBtn = view.findViewById(R.id.id_button_take_photo);
        resetBtn = view.findViewById(R.id.id_button_reset);
        confirmBtn = view.findViewById(R.id.id_button_confirm);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        takePhotoBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
    }

    private Camera getCameraInstance(){
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e){
            Log.e(TAG, "open camera fail: " + e.getMessage());
        }
        return camera;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.id_button_take_photo){
            takePhotoBtn.setVisibility(View.GONE);
            cameraLayout.setVisibility(View.VISIBLE);
            mCamera.takePicture(null, null, this);
        } else if (v.getId() == R.id.id_button_reset){
            cameraLayout.setVisibility(View.GONE);
            takePhotoBtn.setVisibility(View.VISIBLE);
            mCamera.startPreview();
        } else if (v.getId() == R.id.id_button_confirm){
            String path = saveFile();
            startFragment(path);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCamera == null){
            mCamera = getCameraInstance();
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        mData = new byte[data.length];
        System.arraycopy(data, 0, mData, 0, data.length);
    }

    private String saveFile(){
        File pictureFile = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_IMAGE);
        if (pictureFile == null){
            Log.e(TAG, "error creating media file, check storage permissions");
            return null;
        }
        if (mData == null || mData.length == 0){
            Log.e(TAG, "data is null");
        }
        try {
            FileOutputStream fis = new FileOutputStream(pictureFile);
            fis.write(mData);
            fis.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "error: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "error: " + e.getMessage());
            e.printStackTrace();
        }
        return pictureFile.getPath();
    }

    private void startFragment(String path){
        if (getActivity() != null){
            ChooseFragment chooseFragment = new ChooseFragment();
            Bundle bundle = new Bundle();
            bundle.putString("image", path);
            chooseFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.id_fragment, chooseFragment)
                    .commit();
        }
    }

}
