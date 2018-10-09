package com.ipcamera.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.igexin.sdk.PushManager;
import com.ipcamer.demo.R;
import com.ipcamera.demo.bean.ErrorBean;
import com.ipcamera.demo.bean.JsonBean;
import com.ipcamera.demo.bean.PushBindDeviceBean;
import com.ipcamera.demo.bean.SetLanguageBean;
import com.ipcamera.demo.net.ApiCallBack;
import com.ipcamera.demo.net.HttpConstances;
import com.ipcamera.demo.net.HttpHelper;
import com.ipcamera.demo.net.VcmApi;
import com.ipcamera.demo.utils.EncryptionUtils;
import com.ipcamera.demo.utils.Log;
import com.ipcamera.demo.utils.StringUtils;
import com.ipcamera.demo.utils.ToastUtils;

public class IpConnectActivity extends Activity implements View.OnClickListener{
private EditText et_uid,et_token,et_oemid;
    private  PushBindDeviceBean pushBindDeviceBean=null;
    private SetLanguageBean setLanguageBean=null;
    private Button btn_get_token;
    // DemoPushService.class 自定义服务名称, 核心服务
    private Class userPushService = DemoPushService.class;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_connect);
        /**
         *
         * 1，个推 配置 添加两个个推服务类DemoPushService和DemoPushService，直接复制到你的项目中
         * 2，在清单文件配置对应的 服务组件和个推对应的密钥配置
         * 3，配置对应的权限
         *
         *
         * 绑定接口顺序：
         * 1，调通“绑定接口”
         * 2，绑定成功后“设置语言接口”
         * 3，业务上删除摄像机后要调用 “解绑接口”
         *
         */
        initView();
        initPush();


    }
    private void initPush() {

        //初始化话个推
        PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
        //启动接收推送服务
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
        //得到对应的token字符串
        et_token.setText(PushManager.getInstance().getClientid(this));
    }
    //初始化数据
    private void initData() {
        pushBindDeviceBean=new PushBindDeviceBean();
        String date=(System.currentTimeMillis()+"").substring(0,10);
        pushBindDeviceBean.setDate(date);
        String ran=StringUtils.getFourRandom();
        pushBindDeviceBean.setRan(ran);
        pushBindDeviceBean.setEncryp(StringUtils.getEncryp("281e70f4-f9e8-211e-4bea-db24d44b1adf",date,ran));
        pushBindDeviceBean.setToken(et_token.getText().toString());
        pushBindDeviceBean.setUid(et_uid.getText().toString().toUpperCase());
        pushBindDeviceBean.setOemid(et_oemid.getText().toString());

        setLanguageBean=new SetLanguageBean();
        String date2=(System.currentTimeMillis()+"").substring(0,10);
        setLanguageBean.setDate(date2);
        String ran2=StringUtils.getFourRandom();
        setLanguageBean.setRan(ran2);
        setLanguageBean.setEncryp(StringUtils.getEncryp("281e70f4-f9e8-211e-4bea-db24d44b1adf",date2,ran2));
        setLanguageBean.setToken(et_token.getText().toString());
        setLanguageBean.setUid(et_uid.getText().toString().toUpperCase());
        setLanguageBean.setOemid(et_oemid.getText().toString());
        //de en es fr it ja ko nl pl pt-br ru th vi zh zh_FT
        setLanguageBean.setLanguage("zh");

    }

    private void initView() {
        et_uid=(EditText)findViewById(R.id.et_uid);
        et_token=(EditText)findViewById(R.id.et_token);
        et_oemid=(EditText)findViewById(R.id.et_oemid);
        findViewById(R.id.btn_get_token).setOnClickListener(this);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.btn_1:
               initData();
               if (check()){
                   bindDevices();
               }

               break;
           case R.id.btn_2:
               initData();
               if (check()){
                   unbindDevices();
               }

               break;
           
           case R.id.btn_3:
               initData();
               if (check()){
                   setLanguage();
               }

               break;

           case R.id.btn_get_token:
               et_token.setText(PushManager.getInstance().getClientid(this));
               break;
       }
    }
   //设置语言接口
    private void setLanguage() {

        VcmApi.get().load(HttpConstances.SETLANGUAGE, setLanguageBean, new ApiCallBack() {
            @Override
            public void onFinish(JsonBean bean) {
                ToastUtils.show(IpConnectActivity.this,bean.getJson());
            }

            @Override
            public void onError(ErrorBean bean) {
                ToastUtils.show(IpConnectActivity.this,bean.getErrorMsg());
            }
        });
    }
   //解绑接口
    private void unbindDevices() {

        VcmApi.get().load(HttpConstances.UNBAND_APP_PUSH_BY_DEVICES, pushBindDeviceBean, new ApiCallBack() {
            @Override
            public void onFinish(JsonBean bean) {
                ToastUtils.show(IpConnectActivity.this,bean.getJson());
            }

            @Override
            public void onError(ErrorBean bean) {
                ToastUtils.show(IpConnectActivity.this,bean.getErrorMsg());
            }
        });
    }

    private boolean check() {
        if (textIsNull(et_oemid)&&textIsNull(et_token)&&textIsNull(et_uid)) {
            return true;
        }else {
            return false;
        }
    }

    private boolean textIsNull(EditText editText){
        if (editText.getText().toString().equals("")){
            ToastUtils.show(IpConnectActivity.this,"存在输入框为空");
            return false;
        }else {
            return true;
        }
    }
    //绑定接口
    private void bindDevices() {

        VcmApi.get().load(HttpConstances.BAND_APP_PUSH_BY_DEVICES, pushBindDeviceBean, new ApiCallBack() {
            @Override
            public void onFinish(JsonBean bean) {
                ToastUtils.show(IpConnectActivity.this,bean.getJson());
            }

            @Override
            public void onError(ErrorBean bean) {
                ToastUtils.show(IpConnectActivity.this,bean.getErrorMsg());
            }
        });
    }
}
