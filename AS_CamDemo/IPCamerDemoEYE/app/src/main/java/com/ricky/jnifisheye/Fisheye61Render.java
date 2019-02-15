package com.ricky.jnifisheye;

import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Fisheye61Render implements GLSurfaceView.Renderer {

    public static final int  ONE_DRAW_VIEW    = 0;
    public static final int  FOUR_DRAW_VIEW   = 4;

    public static final int EYE_POSITION_NOT   		= -1;//未知位置
    public static final int EYE_TOP_LEFT_VIEW  		= 0; //左上图
    public static final int EYE_TOP_RIGHT_VIEW 		= 1; //右上图
    public static final int EYE_BOTTOM_LEFT_VIEW	= 2; //左下图
    public static final int EYE_BOTTOM_RIGHT_VIEW	= 3; //右下图

    private boolean  bIsExpand     = true;     	//是否展开
    private int      m_nDRAW_VIEW_TYPE 	 = ONE_DRAW_VIEW;
    private int      m_nFourDrawPosition = EYE_POSITION_NOT;

    private String did;
    private long renderPtr = 0;
    private GLSurfaceView mTargetSurface;

    public Fisheye61Render(String did,GLSurfaceView surface) {

        this.did = did;
        mTargetSurface = surface;
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

        StopCruise();
        StopExpandViewIng();
        FisheyeAPI.ChangedRender(renderPtr, width, height);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        renderPtr = FisheyeAPI.CreateFisheye61Render();
        if (renderPtr != 0)
            SetDrawViewType(m_nDRAW_VIEW_TYPE);
    }

    public void update(byte[] data, int nW,int nH, int nSize)
    {
        if (renderPtr == 0) {
            return;
        }

        FisheyeAPI.Display(renderPtr,data,nW, nH);
        mTargetSurface.requestRender();
    }


    boolean bMoveAngle = false;
    public void transByPointF(float fX,float fY)
    {
        if (renderPtr == 0) {
            return;
        }

        if (bIsExpand == false) {
            //非展开画面不处理
            return;
        }

        if (FOUR_DRAW_VIEW == m_nDRAW_VIEW_TYPE) {
            //4画面不处理
            return;
        }

        bMoveAngle = true;
        StopCruise();
        int nRet = FisheyeAPI.moveAngle(renderPtr,fX,fY,EYE_POSITION_NOT);
        if (nRet > 0)
            mTargetSurface.requestRender();
    }

    public void transByPointF(float fX,float fY,int position)
    {
        if (renderPtr == 0) {
            return;
        }

        if (ONE_DRAW_VIEW == m_nDRAW_VIEW_TYPE) {
            //1画面不处理
            return;
        }

        bMoveAngle = true;
        StopCruise();
        if (EYE_POSITION_NOT == m_nFourDrawPosition)
            moveZoomFourViewAngle2(fX,fY,position);
        else
            moveZoomFourViewAngle(fX,fY);
    }

    private void moveZoomFourViewAngle(float fX,float fY){
        int nRet = FisheyeAPI.moveAngle(renderPtr,fX,fY,EYE_POSITION_NOT);
        if (nRet > 0)
            mTargetSurface.requestRender();
    }

    private void moveZoomFourViewAngle2(float fX,float fY,int position){
        int nRet = FisheyeAPI.moveAngle(renderPtr,fX,fY,position);
        if (nRet > 0)
            mTargetSurface.requestRender();
    }

    ///////////////////////滑动//////////////////////////
    private float[] moveAngleTimeZvalue = new float[10];
    private int    moveAngleCountIndex = 0;
    private int    moveAnglePosition   = 0;
    boolean bIsMoveAngleTimeIng = false;
    float  fGeteyeZValue  = 0;
    public void moveAngleEnd(int position)
    {
        if (renderPtr == 0) {
            return;
        }

        if (bIsExpand == false) {
            //非展开画面不处理
            return;
        }

        if (bMoveAngle == false)
            return;

        if (m_nFourDrawPosition != EYE_POSITION_NOT)
            position = m_nFourDrawPosition;

        bMoveAngle = false;
        fGeteyeZValue = FisheyeAPI.GeteyeZ(renderPtr,position);
        float zOffset = -1;
        if (fGeteyeZValue > 0.703862f) {
            zOffset = fGeteyeZValue - 0.703862f;
        }

        if (zOffset != -1){
            float pjz = zOffset / 20.f;
            float ysValue = pjz/10.f;
            for (int i = 1; i < 6; ++i) {
                float f = i* ysValue;
                moveAngleTimeZvalue[5-i] = pjz*2 + f;
                moveAngleTimeZvalue[5+i-1] = pjz*2 - f;
            }

            moveAngleCountIndex = 0;
            moveAnglePosition = position;
            handlermoveAngleTime.postDelayed(runnablemoveAngleTime, 100);
            bIsMoveAngleTimeIng = true;
        }
    }

    ///////////////////////滑动回弹效果//////////////////////////
    Handler handlermoveAngleTime =new Handler();
    Runnable runnablemoveAngleTime =new Runnable() {
        @Override
        public void run() {
            if (moveAngleCountIndex >= 10) {
                if (renderPtr != 0) {

                    if(moveAnglePosition == EYE_POSITION_NOT) {
                        fGeteyeZValue = 0.703862f;
                    }
                    else if(moveAnglePosition == EYE_TOP_LEFT_VIEW){
                        fGeteyeZValue = 0.703862f;
                    }
                    else if(moveAnglePosition == EYE_TOP_RIGHT_VIEW){
                        fGeteyeZValue = 0.703862f;
                    }
                    else if(moveAnglePosition == EYE_BOTTOM_LEFT_VIEW){
                        fGeteyeZValue = 0.703862f;
                    }
                    else if(moveAnglePosition == EYE_BOTTOM_RIGHT_VIEW){
                        fGeteyeZValue = 0.703862f;
                    }

                    FisheyeAPI.SeteyeZ(renderPtr,moveAnglePosition,fGeteyeZValue);
                }

                bIsMoveAngleTimeIng =false;
            }
            else {
                float zOffset = moveAngleTimeZvalue[moveAngleCountIndex];
                if (ONE_DRAW_VIEW == m_nDRAW_VIEW_TYPE){
                    if (renderPtr != 0) {
                        fGeteyeZValue = fGeteyeZValue - zOffset;
                        FisheyeAPI.SeteyeZ(renderPtr,EYE_POSITION_NOT,fGeteyeZValue);
                    }
                }
                else
                {
                    if (renderPtr != 0) {
                        fGeteyeZValue = fGeteyeZValue - zOffset;
                        FisheyeAPI.SeteyeZ(renderPtr,moveAnglePosition,fGeteyeZValue);
                    }
                }
                ++moveAngleCountIndex;
                handlermoveAngleTime.postDelayed(this,100);
            }

            mTargetSurface.requestRender();
        }
    };


    ///////////////////////巡航///////////////////////
    boolean bIsCruise = false;
    Handler handlerCruise =new Handler();
    Runnable runnableCruise =new Runnable() {
        @Override
        public void run() {
            handlerCruise.postDelayed(this,100);
            if (FOUR_DRAW_VIEW == m_nDRAW_VIEW_TYPE)
            {
                if (renderPtr != 0) {
                    FisheyeAPI.SetCruiseAngle(renderPtr,m_nFourDrawPosition,0.01f);
                }
            }
            else
            {
                if (renderPtr != 0) {
                    FisheyeAPI.SetCruiseAngle(renderPtr,EYE_POSITION_NOT,0.01f);
                }
            }

            mTargetSurface.requestRender();
        }
    };

    //开始巡航
    public void StartCruise() {
        if (!bIsCruise)
        {
            bIsCruise = true;
            if (renderPtr != 0) {
                FisheyeAPI.StartCruise(renderPtr,m_nFourDrawPosition);
            }
            handlerCruise.postDelayed(runnableCruise, 100);
        }
    }

    //停止巡航
    public void StopCruise() {
        if (bIsCruise){
            handlerCruise.removeCallbacks(runnableCruise);
            bIsCruise = false;
        }

        if (bIsMoveAngleTimeIng){
            bIsMoveAngleTimeIng = false;
            handlermoveAngleTime.removeCallbacks(runnablemoveAngleTime);
            if (renderPtr != 0) {
                FisheyeAPI.StopCruise(renderPtr,m_nFourDrawPosition);
            }
        }
    }
    //获取当前是否是 巡航状态
    public boolean getCruiseStatus(){
        return bIsCruise;
    }

    //----------------展开 beg------------------------------------------------
    public boolean IsExpandView()
    {
        return bIsExpand;
    }

    public void setExpandView(boolean isExpand) {
        if (FOUR_DRAW_VIEW == m_nDRAW_VIEW_TYPE)
        {
            //4画面不处理
            return;
        }

        if (renderPtr != 0) {
            if (FisheyeAPI.ExpandView(renderPtr,isExpand) == 1) {
                bIsExpand = isExpand;
                mTargetSurface.requestRender();
            }
        }
    }//原图与展开图切换

    public boolean IsOpenTimeRun() {
        if (Expand_index > 0 && Expand_index < N_EXPAND_COUNT)
            return true;
        else
            return false;
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

    //展开效果
    public void setExpandViewIng(boolean isExpand) {
        if (FOUR_DRAW_VIEW == m_nDRAW_VIEW_TYPE)
        {
            //4画面不处理
            return;
        }

        if (IsOpenTimeRun())
            return;

        StopCruise();
        StartExpandViewIng(isExpand);
    }

    private void StartExpandViewIng(boolean isExpand)
    {
        if (FOUR_DRAW_VIEW == m_nDRAW_VIEW_TYPE) {
            //4画面不处理
            return;
        }

        Expand_index = 0;
        if (isExpand) {
            if (renderPtr != 0) {
                if (FisheyeAPI.StartExpandViewIng(renderPtr,isExpand,N_EXPAND_COUNT) > 0)
                    handlerExpandView.postDelayed(runnableExpandView, 100);
            }
        }
        else
        {
            if (renderPtr != 0) {
                if (FisheyeAPI.StartExpandViewIng(renderPtr,isExpand,N_EXPAND_COUNT) > 0) {
                    handlerCloseExpandView.postDelayed(runnableCloseExpandView, 100);
                    bIsExpand =false;
                }
            }
        }
    }

    private void StopExpandViewIng()
    {
        Expand_index = N_EXPAND_COUNT;
    }
    //----------------展开 end------------------------------------------------

    //----------------展开放大与缩小------------------------------------------------
    public void  scaleExpandViewBeg()
    {
        if (FOUR_DRAW_VIEW == m_nDRAW_VIEW_TYPE) {
            //4画面不处理
            return;
        }

        StopCruise();
        if (renderPtr != 0) {
            FisheyeAPI.ScaleOpenViewBeg(renderPtr,EYE_POSITION_NOT);
        }
    }

    public void  scaleExpandViewEnd()
    {
        if (FOUR_DRAW_VIEW == m_nDRAW_VIEW_TYPE) {
            //4画面不处理
            return;
        }

        if (renderPtr != 0) {
            int nRet = FisheyeAPI.ScaleOpenViewEnd(renderPtr,EYE_POSITION_NOT);
            if(nRet == 100)
                bIsExpand = true;
            else if(nRet == 200)
                bIsExpand = false;

            mTargetSurface.requestRender();
        }
    }

    public void  scaleExpandView(float fScale)
    {
        if (FOUR_DRAW_VIEW == m_nDRAW_VIEW_TYPE)
        {
            //4画面不处理
            return;
        }

        if (IsOpenTimeRun())
            return;

        if (renderPtr != 0) {
            FisheyeAPI.ScaleOpenView(renderPtr,EYE_POSITION_NOT,fScale);
            mTargetSurface.requestRender();
        }
    }

    //-----------------------------设置单画或4画名-----------------------------------
    //ONE_DRAW_VIEW    = 0;
    //FOUR_DRAW_VIEW   = 4;
    public  void SetDrawViewType(int nType)
    {
        if (renderPtr != 0) {
            if (FisheyeAPI.SetDrawViewTpye(renderPtr,nType) > 0){
                m_nDRAW_VIEW_TYPE = nType;
                m_nFourDrawPosition = EYE_POSITION_NOT;
                if (m_nDRAW_VIEW_TYPE == FOUR_DRAW_VIEW)
                    bIsExpand = true;

                mTargetSurface.requestRender();
            }
        }
        else {
            m_nDRAW_VIEW_TYPE = nType;
            m_nFourDrawPosition = EYE_POSITION_NOT;
        }
    }

    public int getDrawViewType()
    {
        return m_nDRAW_VIEW_TYPE;
    }

    //4画面时判断点在那个区域的画面
    public  int HitPointLocation(int nW,int nH, int x,int y)
    {
        Rect TLRect = new Rect(0,0,nW/2,nH/2);
        Rect TRRect = new Rect(nW/2,0,nW,nH/2);
        Rect BLRect = new Rect(0,nH/2,nW/2,nH);
        Rect BRRect = new Rect(nW/2,nH/2,nW,nH);

        if(TLRect.contains(x,y))
            return EYE_TOP_LEFT_VIEW;

        if(TRRect.contains(x,y))
            return EYE_TOP_RIGHT_VIEW;

        if(BLRect.contains(x,y))
            return EYE_BOTTOM_LEFT_VIEW;

        if(BRRect.contains(x,y))
            return EYE_BOTTOM_RIGHT_VIEW;

        return EYE_POSITION_NOT;
    }

    //4画面时设置具体画那个方向
    public void  SetDrawPosition(int position)
    {
        if (renderPtr != 0) {
            if (FisheyeAPI.SetDrawPosition(renderPtr,position) > 0){
                m_nFourDrawPosition = position;
                mTargetSurface.requestRender();
            }
        }
    }

    public int GetDrawPosition()
    {
        return m_nFourDrawPosition;
    }
}
