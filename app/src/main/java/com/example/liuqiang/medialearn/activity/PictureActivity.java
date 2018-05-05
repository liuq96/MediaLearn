package com.example.liuqiang.medialearn.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.liuqiang.medialearn.R;
import com.example.liuqiang.medialearn.media.CameraHelper;
import com.example.liuqiang.medialearn.ui.CameraFragment;
import com.example.liuqiang.medialearn.ui.ChooseFragment;

import java.io.File;
import java.lang.reflect.Method;


/**
 * 使用系统相机和Camera进行拍照
 */
public class PictureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        startFragment();
    }


    private void startFragment(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        ChooseFragment fragment = new ChooseFragment();
        transaction.add(R.id.id_fragment, fragment);
        transaction.commit();
    }

}
