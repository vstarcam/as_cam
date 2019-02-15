package com.ricky.jnifisheye;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Fisheye60Render implements GLSurfaceView.Renderer{
    private String did;
    private long renderPtr = 0;
    private GLSurfaceView mTargetSurface;

    public static final int  DEVICE_TYPE_C93  = 0;
    public static final int  DEVICE_TYPEC60   = 1;

    private int      m_nDRAW_VIEW_TYPE 	 = DEVICE_TYPEC60;

    private boolean bIsExpand = false;

    private int view_w = 0;
    private int view_h = 0;
    /**缩放矩阵x轴参数 */
    private float scaleX = 0.6f;
    /** 缩放矩阵y轴参数 */
    private float scaleY = 0.6f;

    private float mAngleX;
    private float mAngleY;

    //缩放到最小时的初始值
    public float scaleInitX = 0.6f;
    public float scaleInitY = 1f;
    public float miniScaleX = scaleInitX;

    public Fisheye60Render(String did,GLSurfaceView surface, int nDeviceType) {

        this.did = did;
        mTargetSurface = surface;
        m_nDRAW_VIEW_TYPE = nDeviceType;
    }

    @Override
    protected void finalize() throws Throwable {
        if (renderPtr != 0) {
            FisheyeAPI.FreeViewRender(renderPtr);
        }
        super.finalize();
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

        StopAllAction();

        FisheyeAPI.ChangedRender(renderPtr, width, height);
        this.view_w = width;
        this.view_h = height;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        renderPtr = FisheyeAPI.CreateFisheye60Render();
        if (renderPtr != 0) {
            SetDrawViewType(m_nDRAW_VIEW_TYPE);
            set180Open(bIsExpand);
        }
    }

    public void update(byte[] data, int nW,int nH, int nSize)
    {
        if (renderPtr == 0) {
            return;
        }

        FisheyeAPI.Display(renderPtr,data,nW, nH);
        mTargetSurface.requestRender();
    }

    public  void SetDrawViewType(int nType)
    {
        if (renderPtr != 0) {
            if (FisheyeAPI.SetDrawViewTpye(renderPtr,nType) > 0){
                m_nDRAW_VIEW_TYPE = nType;
                mTargetSurface.requestRender();
            }
        }
        else {
            m_nDRAW_VIEW_TYPE = nType;
        }
    }

    //----------------展开 beg------------------------------------------------
    public boolean get180Open() {
        return bIsExpand;
    }

    public void set180Open(boolean isExpand) {
        if (renderPtr != 0) {
            if (FisheyeAPI.ExpandView(renderPtr,isExpand) == 1) {
                bIsExpand = isExpand;
                if(bIsExpand) {
                    scaleX = 1.5f;
                    scaleY = scaleX;
                    miniScaleX = scaleX;
                    mAngleY = 0f;
                    mAngleX = 0f;
                }
                else {
                    float ratio = view_w / (float) view_h;
                    scaleX = ratio/2;
                    scaleY = ratio/2 - 0.3f;
                    scaleInitX = ratio/2;
                    scaleInitY = scaleY;
                    miniScaleX = scaleInitX;
                }

                mTargetSurface.requestRender();
            }
        }
    }//原图与展开图切换


    public void Open180ing(boolean isOpen) {
        Expand_index = 0;
        if (isOpen) {
            if (renderPtr != 0) {
                if (FisheyeAPI.StartExpandViewIng(renderPtr,isOpen,N_EXPAND_COUNT) > 0)
                    handlerExpandView.postDelayed(runnableExpandView, 100);
            }
        }
        else
        {
            if (renderPtr != 0) {
                if (FisheyeAPI.StartExpandViewIng(renderPtr,isOpen,N_EXPAND_COUNT) > 0) {
                    handlerCloseExpandView.postDelayed(runnableCloseExpandView, 100);
                    //bIsExpand =false;
                }
            }
        }
    }

    public void StopOpen180ing() {
        Expand_index = N_EXPAND_COUNT;
    }

    //原图向展开图切换动作
    private final int   N_EXPAND_COUNT = 20;
    private int         Expand_index   = 0;
    Handler handlerExpandView =new Handler();
    Runnable runnableExpandView =new Runnable() {
        @Override
        public void run() {
            ++Expand_index;
            if (Expand_index >= N_EXPAND_COUNT)
            {
                if (renderPtr != 0) {
                    FisheyeAPI.ExpandViewIngStep(renderPtr,N_EXPAND_COUNT,N_EXPAND_COUNT,true);
                }
                handlerExpandView.removeCallbacks(runnableExpandView);
                Expand_index = 0;
                bIsExpand =true;

                scaleX = 1.5f;
                scaleY = scaleX;
                miniScaleX = scaleX;
                mAngleY = 0f;
                mAngleX = 0f;
            }
            else
            {
                if (renderPtr != 0) {
                    FisheyeAPI.ExpandViewIngStep(renderPtr,Expand_index,N_EXPAND_COUNT,true);
                }
                handlerExpandView.postDelayed(this,100);
            }
            mTargetSurface.requestRender();
        }
    };//END原图向展开图切换动作

    //展开图向原图切换动作
    Handler handlerCloseExpandView =new Handler();
    Runnable runnableCloseExpandView =new Runnable() {
        @Override
        public void run() {
            ++Expand_index;
            if (Expand_index >= N_EXPAND_COUNT)
            {
                handlerCloseExpandView.removeCallbacks(runnableCloseExpandView);
                if (renderPtr != 0) {
                    FisheyeAPI.ExpandViewIngStep(renderPtr,N_EXPAND_COUNT,N_EXPAND_COUNT,false);
                }
                Expand_index = 0;
                bIsExpand =false;
                float ratio = view_w / (float) view_h;
                scaleX = ratio/2;
                scaleY = ratio/2 - 0.3f;
                scaleInitX = ratio/2;
                scaleInitY = scaleY;
                miniScaleX = scaleInitX;
            }
            else
            {
                if (renderPtr != 0) {
                    FisheyeAPI.ExpandViewIngStep(renderPtr,Expand_index,N_EXPAND_COUNT,false);
                }

                handlerCloseExpandView.postDelayed(this,100);
            }
            mTargetSurface.requestRender();
        }
    };//END展开图向原图切换动作

    //----------------巡航------------------------------------------------
    private Boolean bLeftCruise = true;
    Handler handlerCruise =new Handler();
    Runnable runnableCruise =new Runnable() {
        @Override
        public void run() {
            if (bIsExpand)
            {
                float fBase = GetMaxBase();
                if (getAngleX() > fBase) {
                    bLeftCruise = false;
                }
                else if(getAngleX() < -fBase)
                    bLeftCruise = true;

                if (bLeftCruise) {
                    setAngleX(getAngleX() + 0.005f);
                }
                else
                {
                    setAngleX(getAngleX() - 0.005f);
                }

                mTargetSurface.requestRender();
                handlerCruise.postDelayed(this,100);
            }
            else
                handlerCruise.postDelayed(this,100);
        }
    };


    //开始巡航
    private  boolean isCruiseing = false;
    public void DelayedCruise()
    {
        isCruiseing = true;
        handlerCruise.postDelayed(runnableCruise, 1000);
    }

    public void StartCruise() {
        if (isCruiseing ==false)
        {
            isCruiseing = true;
            if (bIsExpand)
                handlerCruise.postDelayed(runnableCruise, 100);
        }
    }

    //停止巡航
    public void StopCruise() {
        if (isCruiseing) {
            isCruiseing = false;
            handlerCruise.removeCallbacks(runnableCruise);
        }
    }

    public  float GetMaxBase()
    {
        float base;
        if(scaleX > 4.5f){
            base = 0.78f;
        }
        else if(scaleX > 4.2){
            base = 0.71f;
        }
        else if(scaleX > 4){
            base = 0.70f;
        }
        else if(scaleX > 3){
            base = 0.62f;
        }
        else if(scaleX > 2.5){
            base = 0.54f;
        }
        else if(scaleX > 2){
            base = 0.52f;
        }else if(scaleX > 1.5){
            base = 0.49f;
        }else{
            base = 0.42f;
        }
        return base;
    }


    public float getAngleX() {
        return mAngleX;
    }

    public void setAngleX(float fValue) {
        if (renderPtr != 0)
        {
            int nR = FisheyeAPI.setAngle(renderPtr,fValue,true,0,false);
            if(nR > 0){
                mAngleX = fValue;
                mTargetSurface.requestRender();
            }
        }
    }

    public float getAngleY() {
        return mAngleY;
    }

    public void setAngleY(float fValue) {
        if (renderPtr != 0)
        {
            int nR = FisheyeAPI.setAngle(renderPtr,0,false,fValue,true);
            if(nR > 0){
                mAngleY = fValue;
                mTargetSurface.requestRender();
            }
        }
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float fValue) {
        scaleX = fValue;
        mTargetSurface.requestRender();
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float fValue) {
        scaleY = fValue;
        mTargetSurface.requestRender();
    }

    public  boolean CalibrationParameters(float base,float fx, float fy)
    {
        if(fx > base){//已左移到最左边
            mAngleX = base;
            if(fy < -base){//左上
                if(base >= 0.41)
                    base = 0.2f;
                mAngleY = -base;
            }
            if(fy > base){//右上
                if(base >= 0.41)
                    base = 0.2f;
                mAngleY = base;
            }

            return true;
        }else if(fx < -base){//最右边
            mAngleX = -base;
            if(fy > base){//右上
                if(base >= 0.41)
                    base = 0.2f;
                mAngleY = base;
            }
            if(fy < -base){
                if(base >= 0.41)
                    base = 0.2f;
                mAngleY = -base;
            }
            return true;
        }else if(fy > base){//最上面
            if(base >= 0.52)
                base = 0.42f;

            mAngleY = base;
            return true;
        }else if(fy < -base){//最下面
            if(base >= 0.52)
                base = 0.42f;
            mAngleY = -base;
            return true;
        }

        return false;
    }


    public void MoveAngle(float fX, float fY)
    {
        StopAllAction();
        float fBase = GetMaxBase() + 0.2f;
        float ffx   = mAngleX + fX;
        float ffy   = mAngleY + fY;
        Boolean bIsOK = CalibrationParameters(fBase,ffx,ffy);
        if (bIsOK == false){
            if (renderPtr != 0) {
                int nR = FisheyeAPI.moveAngle(renderPtr,ffx,ffy,0);
                if (nR > 0) {
                    mAngleX = ffx;
                    mAngleY = ffy;
                    mTargetSurface.requestRender();
                }
            }//renderPtr != 0
        }
    }

    public void moveAngleEnd() {
        float fBase = GetMaxBase();
        CalibrationParameters(fBase);
    }

    //----------------------------------边缘回弹接口--------------------------------------------------
    static int EDGE_TIME_TALL = 20;
    private boolean m_bUpdateX = false;
    private boolean m_bUpdateY = false;
    private float m_fUdateAveX = 0;
    private float m_fUdateAveY = 0;
    private int   m_nTimerEdgeTJ = 0;
    private boolean m_bIsEdgeIng = false;
    private  void  setAngleIng(float fX, boolean bUpdateX,float fY,boolean bUpdateY)
    {
        m_nTimerEdgeTJ = EDGE_TIME_TALL;
        float x = mAngleX - fX;
        float y = mAngleY - fY;

        if (bUpdateX)
        {
            m_fUdateAveX = x / EDGE_TIME_TALL;
            m_bUpdateX = true;
        }
        else
            m_bUpdateX = false;

        if (bUpdateY)
        {
            m_fUdateAveY = y / EDGE_TIME_TALL;
            m_bUpdateY = true;
        }
        else
            m_bUpdateY = false;

        StartAngleing();
    }

    private   void CalibrationParameters(float base)
    {
        if(mAngleX > base){//已左移到最左边
            float fy = 0;
            Boolean  uy = false;
            if(mAngleY < -base){//左上
                if(base >= 0.41f)
                    base = 0.2f;
                fy = -base;
                uy = true;
            }
            if(mAngleY > base){//右上
                if(base >= 0.41f)
                    base = 0.2f;
                fy = base;
                uy = true;
            }

            setAngleIng(base,true,fy,uy);
        }else if(mAngleX < -base){//最右边
            float fy = 0;
            Boolean  uy = false;
            if(mAngleY > base){//右上
                if(base >= 0.41f)
                    base = 0.2f;
                fy = base;
                uy = true;
            }
            if(mAngleY < -base){
                if(base >= 0.41f)
                    base = 0.2f;
                fy = -base;
                uy = true;
            }

            setAngleIng(-base,true,fy,uy);
        }else if(mAngleY > base){//最上面
            if(base >= 0.52f)
                base = 0.42f;

            setAngleIng(0,false,base,true);
        }else if(mAngleY < -base){//最下面
            if(base >= 0.52f)
                base = 0.42f;

            setAngleIng(0,false,-base,true);
        }
    }

    Handler handlerAngleing =new Handler();
    Runnable runnableAngleing =new Runnable() {
        @Override
        public void run() {
            --m_nTimerEdgeTJ;
            float fx =0;
            float fy =0;
            if (m_bUpdateX) {
                fx = mAngleX - m_fUdateAveX;
            }

            if (m_bUpdateY) {
                fy = mAngleY - m_fUdateAveY;
            }

            if (renderPtr != 0) {
                int nR = FisheyeAPI.setAngle(renderPtr,fx,m_bUpdateX,fy,m_bUpdateY);
                if (nR > 0) {
                    if (m_bUpdateX) {
                        mAngleX = fx;
                    }

                    if (m_bUpdateY) {
                        mAngleY = fy;
                    }
                }
            }

            if (m_nTimerEdgeTJ >0)
            {
                mTargetSurface.requestRender();
                handlerAngleing.postDelayed(this,100);
            }
            else {
                handlerAngleing.removeCallbacks(runnableAngleing);
                m_bIsEdgeIng = false;
            }
        }
    };

    private void StartAngleing()
    {
        if (m_bIsEdgeIng ==false) {
            m_bIsEdgeIng = true;
            m_nTimerEdgeTJ = EDGE_TIME_TALL;
            handlerAngleing.postDelayed(runnableAngleing, 100);
        }
    }

    private void StopAngleing()
    {
        if (m_bIsEdgeIng) {
            m_nTimerEdgeTJ = 0;
            m_bIsEdgeIng = false;
            handlerAngleing.removeCallbacks(runnableAngleing);
        }
    }
    //----------------------------------边缘回弹接口end-----------------------------------------------
    private  void  StopAllAction()
    {
        StopAngleing();
        StopCruise();
        StopOpen180ing();
    }

    public void wheelEvent(float angle) {
        if (renderPtr != 0) {
            //Log.e("fisheye_jni","angle:"+angle);
            float fValue = FisheyeAPI.SeteyeZ(renderPtr,0,angle/3);
            if (fValue > 2){
                //ANGLE = fValue;
                mTargetSurface.requestRender();
            }
        }//renderPtr != 0
    }
}
