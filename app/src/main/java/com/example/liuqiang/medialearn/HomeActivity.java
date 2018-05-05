package com.example.liuqiang.medialearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.liuqiang.medialearn.activity.CameraActivity;
import com.example.liuqiang.medialearn.activity.PictureActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activty);
    }

    public void jumpToCameraActivity(View view){
        startActivity(new Intent(this, CameraActivity.class));
    }

    public void jumpToPictureActivity(View view){
        startActivity(new Intent(this, PictureActivity.class));
    }



}
