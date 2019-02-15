package com.ricky.jnifisheye;

public class FisheyeAPI {
    public static native int  getVersion();

    public static native long CreateViewRender();
    public static native int FreeViewRender(long RenderAddr);

    public static native int ChangedRender(long RenderAddr,int width,int height);
    public static native int Draw(long RenderAddr);
    public static native int Display(long RenderAddr,byte[] yuvArray,int width,int height);

    //c60
    public static native long  CreateFisheye60Render();
    public static native int   setAngle(long RenderAddr,float fX, boolean bUpdateX,float fY,boolean bUpdateY);

    //C61创建窗口
    public static native long  CreateFisheye61Render();
    public static native float GeteyeZ(long RenderAddr,int position);
    public static native float SeteyeZ(long RenderAddr,int position, float fValue);
    //4画面绘制模式
    public static native int   SetDrawPosition(long RenderAddr,int mode);

    //c60与C61公共接口
    //巡航接口
    public static native int StartCruise(long RenderAddr,int position);
    public static native int StopCruise(long RenderAddr,int position);
    public static native float SetCruiseAngle(long RenderAddr,int position, float fOffset);
    //滑动接口
    public static native int  moveAngle(long RenderAddr,float fx,float fy,int position);
    public static native int  moveAngleEnd(long RenderAddr,int position);
    //展开与合并不带效果接口
    public static native int  ExpandView(long RenderAddr,boolean isExpand);
    //展开与合并带效果接口
    public static native int StartExpandViewIng(long RenderAddr,boolean isExPand,int count);
    public static native int StopExpandViewIng(long RenderAddr,int index,boolean isExPand);
    public static native int ExpandViewIngStep(long RenderAddr,int index, int count,boolean isExPand);

    //缩放接口
    public static native int ScaleOpenViewBeg(long RenderAddr,int position);
    public static native int ScaleOpenViewEnd(long RenderAddr,int position);
    public static native float ScaleOpenView(long RenderAddr,int position, float fOffset);

    ////指定娇正方式
    public static native int   SetDrawViewTpye(long RenderAddr,int tpye);

    //打印底层jni日志，nEnable=1为开启，0为关闭
    public  native static void PrintJNILog(int nEnable);

    static {
        System.loadLibrary("VSFisheye");
    }
}
