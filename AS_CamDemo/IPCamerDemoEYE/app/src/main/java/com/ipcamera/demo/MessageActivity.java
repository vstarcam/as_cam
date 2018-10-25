package com.ipcamera.demo;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.ipcamer.demo.R;
import com.ipcamera.demo.adapter.MessageAdapter;
import com.ipcamera.demo.bean.ErrorBean;
import com.ipcamera.demo.bean.JsonBean;
import com.ipcamera.demo.bean.MessageBean;
import com.ipcamera.demo.bean.PushBindDeviceBean;
import com.ipcamera.demo.net.ApiCallBack;
import com.ipcamera.demo.net.HttpConstances;
import com.ipcamera.demo.net.VcmApi;
import com.ipcamera.demo.utils.Log;
import com.ipcamera.demo.utils.StringUtils;
import com.ipcamera.demo.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageActivity extends Activity {

    private ListView mListview;
    private PushBindDeviceBean pushBindDeviceBean=null;
    private  MessageBean mMessageBean;
    private Button sureButton;
    private EditText et_uid,et_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initView();
        initDate();

    }

    private void initDate()
    {
        String ran=StringUtils.getFourRandom();
        String date=(System.currentTimeMillis()+"").substring(0,10);
        mMessageBean = new MessageBean();
        mMessageBean.setRan(ran);
        mMessageBean.setEncryp(StringUtils.getEncryp("281e70f4-f9e8-211e-4bea-db24d44b1adf",date,ran));
        mMessageBean.setUid(et_uid.getText().toString());
        mMessageBean.setDate(date);
        //时间格式必须是2018-10-20
        mMessageBean.setRecordDate(et_time.getText().toString());
    }

    private boolean check() {
        if (textIsNull(et_uid)&&textIsNull(et_time)&&textIsNull(et_uid)) {
            return true;
        }else {
            return false;
        }
    }

    private boolean textIsNull(EditText editText){
        if (editText.getText().toString().equals("")){
            ToastUtils.show(MessageActivity.this,getString(R.string.input_null));
            return false;
        }else {
            return true;
        }
    }

    private void  initView()
    {
        sureButton = (Button)findViewById(R.id.sure);
        et_uid = (EditText)findViewById(R.id.et_uid);
        et_time = (EditText)findViewById(R.id.et_time);
        sureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check())
                {
                    getDevicesInfo();
                }
            }
        });
        mListview = (ListView)findViewById(R.id.messagelist);
    }


    //记录接口
    private void getDevicesInfo() {
        initDate();
        Log.e("api","getDevicesInfo"+mMessageBean.getEncryp());
        VcmApi.get().load(HttpConstances.GETINFO, mMessageBean, new ApiCallBack() {
            @Override
            public void onFinish(JsonBean bean) {
                Log.e("api","bean"+bean);
                if(bean.getCode() == 200) {
                    //ToastUtils.show(MessageActivity.this, bean.getJson());
                    Bundle bd = new Bundle();
                    Message msg = showHandler.obtainMessage();
                    msg.what = 1;
                    bd.putString("content", bean.getJson());
                    msg.setData(bd);
                    showHandler.sendMessage(msg);
                }
            }

            @Override
            public void onError(ErrorBean bean) {
                Log.e("api","bean"+bean);
                ToastUtils.show(MessageActivity.this,bean.getErrorMsg());
            }
        });
    }

    Handler showHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bd = msg.getData();

            ArrayList<MessageBean> items = new ArrayList<MessageBean>();

            if (msg.what == 1) {
                String msgParam = bd.getString("content");

                JSONArray backBodyJson = null;// 首先把字符串转成 JSONArray 对象
                try {
                    backBodyJson = new JSONArray(msgParam);
                    for (int i = 0;i<backBodyJson.length();i++)
                    {
                        MessageBean messageBean = new MessageBean();
                        JSONObject job = backBodyJson.getJSONObject(i);
                        messageBean.setDate(job.getString("time"));
                        messageBean.setDz(job.getString("rea"));
                        //listMessagebean.add(new MessageBean());
                        items.add(messageBean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MessageAdapter messageAdapter = new MessageAdapter(MessageActivity.this,items);
                Log.e("api","bean"+items.size());
                mListview.setAdapter(messageAdapter);
                messageAdapter.notifyDataSetChanged();
                //messageAdapter.
            }

        }
    };
}
