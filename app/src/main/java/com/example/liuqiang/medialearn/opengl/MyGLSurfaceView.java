package com.example.liuqiang.medialearn.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {
    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);
        //设置OpenGL版本号
        setEGLContextClientVersion(2);

        mRenderer = new MyGLRenderer();
        //设置renderer对象
        setRenderer(mRenderer);
        //设置为只有数据变化时才会进行渲染，除非调用requestRender()
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    //重写onTouch事件，来实现监听
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:{
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                if (y > getHeight() / 2){
                    dx = dx * -1;
                }
                if (x < getWidth() / 2){
                    dy = dy * -1;
                }

                mRenderer.setAngle(mRenderer.getAngle() +
                        ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender(); //请求重新渲染
            }
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
}
