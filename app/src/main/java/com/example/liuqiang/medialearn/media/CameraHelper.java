package com.example.liuqiang.medialearn.media;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraHelper {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;


    //获取到最合适的宽高
    public static Camera.Size getOptimalVideoSize(List<Camera.Size> supportedVideoSizes,
                                                  List<Camera.Size> previewSizes, int width, int height) {
        int w, h;
        if (width > height) {
            w = width;
            h = height;
        } else {
            w = height;
            h = width;
        }

        //设置一个可接受的范围
        final double ASPECT_TOLERANCE = 0.1;
        //当前屏幕尺寸比例
        double targetRadio = (double) w / h;

        List<Camera.Size> videoSizes;
        if (supportedVideoSizes != null) {
            videoSizes = supportedVideoSizes;
        } else {
            videoSizes = previewSizes;
        }

        Camera.Size optimalSize = null;

        double minDiff = Double.MAX_VALUE;
        for (Camera.Size size : videoSizes) {
            double radio = (double) size.width / size.height;
            //超过可接受范围，则跳过
            if (Math.abs(radio - targetRadio) > ASPECT_TOLERANCE)
                continue;
            //获取最小差距的宽高
            if (Math.abs(radio - targetRadio) < minDiff && previewSizes.contains(size)) {
                optimalSize = size;
                minDiff = Math.abs(radio - targetRadio);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : videoSizes) {
                if (Math.abs(size.height - h) < minDiff && previewSizes.contains(size)) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        return optimalSize;
    }

    public static Camera.Size getOptimalImageSize(List<Camera.Size> supportedPreviewSizes, int width, int height){
        int w,h;
        w = Math.max(width, height);
        h = Math.min(width, height);

        double minDiff = Double.MAX_VALUE;
        Camera.Size optimalSize = null;
        for (Camera.Size size : supportedPreviewSizes){
            double radio = (double) w / h;
            if (radio < minDiff){
                minDiff = radio;
                optimalSize = size;
            }
        }
        return optimalSize;
    }

    public static Camera getDefaultCameraInstance() {
        return Camera.open();
    }

    //获取后摄像头
    public static Camera getDefaultBackFacingCameraInstance() {
        return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    //获取前摄像头
    public static Camera getDefaultFontFacingCameraInstance() {
        return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static Camera getDefaultCamera(int position) {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == position) {
                return Camera.open(i);
            }
        }
        return null;
    }

    //获取视频保存的文件
    public static File getOutputMediaFile(int type) {
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraSample");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraSample", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp
                    + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp
                    + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    public static void setCameraDisplayOrientation(Camera camera, Context context, int cameraId) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (manager == null) {
            Log.e("CameraSample", "get window manager fail!");
            return;
        }
        Display display = manager.getDefaultDisplay();
        int rotation = display.getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        int result;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
            result = (cameraInfo.orientation + degree) % 360;
            result = (360 - result) % 360;
        } else {
            result = (cameraInfo.orientation - degree + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

}
