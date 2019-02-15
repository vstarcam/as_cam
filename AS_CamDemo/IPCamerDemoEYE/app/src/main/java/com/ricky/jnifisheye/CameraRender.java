package com.ricky.jnifisheye;

import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.ricky.jnifisheye.FisheyeAPI;

public class CameraRender implements GLSurfaceView.Renderer {
    private String did;
    private long renderPtr = 0;
    private GLSurfaceView mTargetSurface;

    public CameraRender(String did,GLSurfaceView surface) {

        this.did = did;
        mTargetSurface = surface;
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (renderPtr == 0) {
            return;
        }

        FisheyeAPI.Draw(renderPtr);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        if (renderPtr == 0) {
            return;
        }

        FisheyeAPI.ChangedRender(renderPtr, width, height);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        renderPtr = FisheyeAPI.CreateViewRender();
    }

    public void update(byte[] data, int nW,int nH, int nSize)
    {
        if (renderPtr == 0) {
            return;
        }

        FisheyeAPI.Display(renderPtr,data,nW, nH);
        mTargetSurface.requestRender();
    }
}
