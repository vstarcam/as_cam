package com.ipcamera.demo;


import com.ipcamer.demo.R;

import vstc2.nativecaller.NativeCaller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class StartActivity extends Activity {
	private static final String LOG_TAG = "StartActivity";
	private TextView version;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Intent in = new Intent(StartActivity.this, AddCameraActivity.class);
			startActivity(in);
			finish();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "StartActivity onCreate");
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.start);
		version = (TextView)findViewById(R.id.version);
		version.setText("jni_code:"+NativeCaller.GetVersion());
		Intent intent = new Intent();
		intent.setClass(StartActivity.this, BridgeService.class);
		startService(intent);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					NativeCaller.PPPPInitialOther("ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL");
					Thread.sleep(3000);
					Message msg = new Message();
					mHandler.sendMessage(msg);
					Log.e("vst","path"+getApplicationContext().getFilesDir().getAbsolutePath());
                    NativeCaller.SetAPPDataPath(getApplicationContext().getFilesDir().getAbsolutePath());
				} catch (Exception e) {

				}
			}
		}).start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			return true;
		return super.onKeyDown(keyCode, event);
	}

}
