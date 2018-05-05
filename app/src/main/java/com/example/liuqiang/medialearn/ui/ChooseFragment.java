package com.example.liuqiang.medialearn.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.liuqiang.medialearn.R;
import com.example.liuqiang.medialearn.media.CameraHelper;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ChooseFragment extends Fragment implements View.OnClickListener {

    private Button systemCameraBtn, customCameraBtn, customCamera2Btn;
    private ImageView imageView;
    private File imageFile;

    private static final int IMAGE_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 0;
    private String imagePath;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getArguments() != null){
            Bundle bundle = getArguments();
            imagePath = bundle.getString("image");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose, container, false);
        systemCameraBtn = view.findViewById(R.id.id_button_use_system_camera);
        customCameraBtn = view.findViewById(R.id.id_button_use_custom_camera);
        customCamera2Btn = view.findViewById(R.id.id_button_use_custom_camera2);
        imageView = view.findViewById(R.id.id_image_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        systemCameraBtn.setOnClickListener(this);
        customCameraBtn.setOnClickListener(this);
        customCamera2Btn.setOnClickListener(this);
        if (imagePath != null && !TextUtils.isEmpty(imagePath)){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
            imageView.setImageBitmap(bitmap);
        }
    }

    private boolean checkPermissions(){
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this.getContext()), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Objects.requireNonNull(this.getActivity()), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void takePictureBySystemCamera(){
        //针对API24以上的情况，Uri必须使用Content:，不能使用File:，这里采用运行时忽略该检查方法
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        imageFile = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_IMAGE);
        Uri uri = Uri.fromFile(imageFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_button_use_system_camera:
                if (checkPermissions()){
                    takePictureBySystemCamera();
                }
                break;
            case R.id.id_button_use_custom_camera:
                if (checkPermissions()){
                    startFragment(new CameraFragment());
                }
                break;
            case R.id.id_button_use_custom_camera2:
                if (checkPermissions()){
                    startFragment(Camera2Fragment.newInstance());
                }
                break;
        }
    }

    private void startFragment(Fragment fragment){
        if (getActivity() != null){
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.id_fragment, fragment)
                    .commit();
        }
    }
}
