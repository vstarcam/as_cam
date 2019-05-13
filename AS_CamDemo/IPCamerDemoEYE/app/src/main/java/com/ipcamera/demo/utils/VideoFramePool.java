package com.ipcamera.demo.utils;

import android.opengl.GLSurfaceView;

import java.util.concurrent.LinkedBlockingQueue;

import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;

public class VideoFramePool extends Thread {
    boolean mExitFlag = false;

    private GLSurfaceView surfaceView;
    private MyRender myRender;
    private  int mframeSize;

    public VideoFramePool(GLSurfaceView surfaceView, MyRender render) {
        this.surfaceView = surfaceView;
        this.myRender = render;
        //this.surfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);
        //surfaceView.setRenderer(render);
    }

    public void exit() {
        mExitFlag = true;
        framePool.clear();
    }

    private LinkedBlockingQueue<byte[]> framePool = new LinkedBlockingQueue<>(50);

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
        this.defaultRate = frameRate;
        this.delayTime = 1000 / defaultRate;
    }

    private int frameRate = 15;
    private int defaultRate = 15;
    private int delayTime = 66;
    private long startDate;
    private long endDate;
    int mHeight = 0;
    int mWidth = 0;

    @Override
    public void run() {
        android.os.Process
                .setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        while (!mExitFlag) {
            startDate = System.currentTimeMillis();

            byte[] frameData = framePool.poll();
            if (framePool.size() <= 5) {
                frameRate = 10;
            } else if (framePool.size() >= 10) {
                frameRate = defaultRate;
            }
            if (frameData != null) {
                this.myRender.writeSample(frameData,mframeSize,mWidth,mHeight);
            }
            Log.e("pool","poolsize "+framePool.size() +"frameRate***"+frameRate);
            try {
                endDate = System.currentTimeMillis();
                if (frameRate == 10) {
                    Thread.sleep(Math.max(0, 100 - (endDate - startDate)), 1000);
                } else if (frameRate == defaultRate) {
                    Thread.sleep(Math.max(0, delayTime - (endDate - startDate)), 1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void pushBytes(byte[] bytes,int frameSize,int w,int h) {
        mWidth = w;
        mHeight = h;
        mframeSize =  frameSize;
        if (mExitFlag) {
            return;
        }
        while (framePool.size() > defaultRate) {
            framePool.poll();
        }
        framePool.offer(bytes);
    }

    public int getFramePoolSize()
    {
        return framePool.size();
    }

    public  void clearBuffer()
    {
        framePool.clear();
    }
}
