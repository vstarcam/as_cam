package com.ipcamera.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import vstc2.nativecaller.NativeCaller;

import com.ipcamer.demo.R;
import com.ipcamera.demo.BridgeService.DateTimeInterface;
import com.ipcamera.demo.BridgeService.PlayBackInterface;
import com.ipcamera.demo.utils.AudioPlayer;
import com.ipcamera.demo.utils.ContentCommon;
import com.ipcamera.demo.utils.CustomBuffer;
import com.ipcamera.demo.utils.CustomBufferData;
import com.ipcamera.demo.utils.CustomBufferHead;
import com.ipcamera.demo.utils.MyRender;
import com.ipcamera.demo.utils.Tools;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * 远程录像回放
 */
public class PlayBackActivity extends BaseActivity implements PlayBackInterface, DateTimeInterface
{
	private ImageView playImg;
	private String strDID;
	private String strFilePath;
	private int strFilesize;
	private byte[] videodata = null;
	private int videoDataLen = 0;
	private int nVideoWidth = 0;
	private int nVideoHeight = 0;
	private boolean isPlaySeekBar = false;
	private LinearLayout layoutConnPrompt;
	private SeekBar playSeekBar;
	private GLSurfaceView myGlSurfaceView;
	private MyRender myRender;
	private int i1 = 0;
	private int i2 = 0;
	boolean exit = false;
	private TextView textTimeStamp;
	private String tzStr = "GMT+08:00";
	private long time;
	private long time1;
	private String timeShow = " ";
	
	private CustomBuffer AudioBuffer = null;
	private AudioPlayer audioPlayer = null;
	private static final int AUDIO_BUFFER_START_CODE = 0xff00ff;
	
	private class MyThread extends Thread {
		@Override
		public void run() {
			while (exit == true) {
				i2 = i1;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (i2 == i1) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							exit = false;
							PlayBackActivity.this.finish();
						}
					});
				}
			}
			super.run();
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (msg.what == 1 || msg.what == 2) {
				if (isOneShow) {
					layoutConnPrompt.setVisibility(View.GONE);
					isOneShow = false;
				}
			}
			switch (msg.what) {
			case 1: {// h264

				textTimeStamp.setText(timeShow);
				myRender.writeSample(videodata, nVideoWidth, nVideoHeight);
				playImg.setVisibility(View.GONE);
				int width = getWindowManager().getDefaultDisplay().getWidth();
		
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
						width, width * 3 / 4);
				lp.gravity = Gravity.CENTER;
				myGlSurfaceView.setLayoutParams(lp);
			}
				break;
			case 2: {// jpeg
				textTimeStamp.setText(timeShow);
				Bitmap bmp = BitmapFactory.decodeByteArray(videodata, 0,videoDataLen);
				if (bmp == null) {
					return;
				}
				Bitmap bitmap = null;
				int width = getWindowManager().getDefaultDisplay().getWidth();
				int height = getWindowManager().getDefaultDisplay().getHeight();
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
						width, width * 3 / 4);
				lp.gravity = Gravity.CENTER;
				playImg.setLayoutParams(lp);
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					bitmap = Bitmap.createScaledBitmap(bmp, width,
							width * 3 / 4, true);
				} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					bitmap = Bitmap.createScaledBitmap(bmp, width, height, true);
				}
				playImg.setVisibility(View.VISIBLE);
				playImg.setImageBitmap(bitmap);
			}
				break;
			default:
				break;
			}
		}
	};

	private boolean isOneShow = true;

	private Runnable mVideoTimeOut = new Runnable() {
		public void run() {
			if (isOneShow) {//
				BridgeService.setPlayBackInterface(PlayBackActivity.this);
				//NativeCaller.StartPlayBack(strDID, strFilePath, 0,0);
				NativeCaller.StartPlayBack(strDID, strFilePath, 0, strFilesize, getDiskCacheDir(PlayBackActivity.this), Tools.getPhoneSDKIntForPlayBack(), Tools.getPhoneMemoryForPlayBack());
				NativeCaller.PPPPGetSystemParams(strDID,ContentCommon.MSG_TYPE_GET_PARAMS);
				mHandler.postDelayed(mVideoTimeOut, 3000);
			}
		}
	};
	
	 public static int getPhoneSDKIntForPlayBack(){
	        int  a =0;
	        if(Build.BRAND.toLowerCase().contains("xiaomi")) a =24;
	          else a =Integer.parseInt(android.os.Build.VERSION.SDK);
	        return a;
	    }
	 public static int getPhoneMemoryForPlayBack(){
	        if(Tools.getPhoneTotalMemory()>=2.8&&Integer.parseInt(android.os.Build.VERSION.SDK)>=23)
	            return 1;
	        else return 0;
	    }
	 
	 
	
	
	public static String getDiskCacheDir(Context mContext){
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			cachePath = mContext.getExternalCacheDir().getPath();
		} else {
			cachePath = mContext.getCacheDir().getPath();
		}
		// 防止不存在目录文件，自动创建
		createFile(cachePath);
		// 返回文件存储地址
		return cachePath;
	}
	
	public static File createFile(String fPath){
		try {
			File file = new File(fPath);
			// 当这个文件夹不存在的时候则创建文件夹
			if(!file.exists()){
				// 允许创建多级目录
				file.mkdirs();
				// 这个无法创建多级目录
				// rootFile.mkdir();
			}
			return file;
		} catch (Exception e) {
		}
		return null;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDataFromOther();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.playback);
		BridgeService.setPlayBackInterface(this);
		//NativeCaller.StartPlayBack(strDID, strFilePath, 0,0);


		//h265 播放720P 需要大于3g的内存
		Log.e("videodate",strFilePath +"strFilesize"+strFilesize +"Tools.getPhoneMemoryForPlayBack()"+Tools.getPhoneMemoryForPlayBack());
		Log.e("videodate",getSysteTotalMemorySize(this)+"");
		Log.e("videodate",getAvailableInternalMemorySize(this)+"");
		Log.e("videodate",getTotalInternalMemorySize(this)+"");

		NativeCaller.StartPlayBack(strDID, strFilePath, 0, strFilesize, getDiskCacheDir(PlayBackActivity.this), Tools.getPhoneSDKIntForPlayBack(), Tools.getPhoneMemoryForPlayBack());
		
		//音频数据
		AudioBuffer = new CustomBuffer();
		audioPlayer = new AudioPlayer(AudioBuffer);
		mHandler.postDelayed(mVideoTimeOut, 3000);
		findView();
		BridgeService.setDateTimeInterface(this);
		NativeCaller.PPPPGetSystemParams(strDID,ContentCommon.MSG_TYPE_GET_PARAMS);
		
		StartAudio();//开启声音
	}

	/**
	  * 获取系统内存大小
	  * @return
	  */
	private String getSysteTotalMemorySize(Context context){

		ActivityManager mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);

		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo() ;

		mActivityManager.getMemoryInfo(memoryInfo) ;
		long memSize = memoryInfo.totalMem ;

		String availMemStr = Formatter.formatFileSize(context,memSize);
		return availMemStr ;
	}

	/**

	 * 获取手机内部可用空间大小

	 * @return

	 */

	static public String getAvailableInternalMemorySize(Context context) {

		File path = Environment.getDataDirectory();

		Log.i("zzz", path.getAbsolutePath());

		StatFs stat = new StatFs(path.getPath());

		long blockSize = stat.getBlockSize();

		long availableBlocks = stat.getAvailableBlocks();

		return Formatter.formatFileSize(context, availableBlocks * blockSize);
	}

	/**

	 * 获取手机内部空间大小

	 * @return

	 */

	static public String getTotalInternalMemorySize(Context context) {

		File path = Environment.getDataDirectory();//Gets the Android data directory

		Log.i("zzz", path.getAbsolutePath());

		StatFs stat = new StatFs(path.getPath());

		long blockSize = stat.getBlockSize();      //每个block 占字节数

		long totalBlocks = stat.getBlockCount();   //block总数

		return Formatter.formatFileSize(context, totalBlocks * blockSize);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if (isPlaySeekBar) {
				isPlaySeekBar = false;
				//playSeekBar.setVisibility(View.GONE);
			} else {
				isPlaySeekBar = true;
				//playSeekBar.setVisibility(View.VISIBLE);
			}
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	private void getDataFromOther() {
		Intent intent = getIntent();
		strDID = intent.getStringExtra("did");
		strFilePath = intent.getStringExtra("filepath");
		strFilesize = intent.getIntExtra("filesize", 0);
		Log.d("getDataFromOther", "strDID:" + strDID);
		Log.d("getDataFromOther", "strFilePath:" + strFilePath);
	}
	Boolean isTakepic = false;
	private Button save;
	private void findView() {
		playImg = (ImageView) findViewById(R.id.playback_img);
		layoutConnPrompt = (LinearLayout) findViewById(R.id.layout_connect_prompt);
		playSeekBar = (SeekBar) findViewById(R.id.playback_seekbar);
		textTimeStamp = (TextView) findViewById(R.id.textTimeStamp);
		myGlSurfaceView = (GLSurfaceView) findViewById(R.id.myhsurfaceview);
		myRender = new MyRender(myGlSurfaceView);
		myGlSurfaceView.setRenderer(myRender);
		save = (Button)findViewById(R.id.savepic);
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				isTakepic = true;
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		NativeCaller.StopPlayBack(strDID);
		StopAudio();
		exit = false;
	}

	private String setDeviceTime(long millisutc, String tz) {


		TimeZone timeZone = TimeZone.getTimeZone(tz);
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTimeInMillis(millisutc);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		String months = "";
		if (month < 10) {
			months = "0" + month;
		} else {
			months = String.valueOf(month);
		}
		String strDay = "";
		if (day < 10) {
			strDay = "0" + day;
		} else {
			strDay = String.valueOf(day);
		}
		String strHour = "";
		if (hour < 10) {
			strHour = "0" + hour;
		} else {
			strHour = String.valueOf(hour);
		}
		String strMinute = "";
		if (minute < 10) {
			strMinute = "0" + minute;
		} else {
			strMinute = String.valueOf(minute);
		}
		String strSecond = "";
		if (second < 10) {
			strSecond = "0" + second;
		} else {
			strSecond = String.valueOf(second);
		}
		return year + "-" + months + "-" + strDay + "   " + strHour + ":"
				+ strMinute + ":" + strSecond;
	}

	@Override
	public void callBackDatetimeParams(String did, int now, int tz,
			int ntp_enable, String ntp_svr) {
		setTimeZone(tz);
	}

	@Override
	public void callBackSetSystemParamsResult(String did, int paramType,
			int result) {
	}

	private void setTimeZone(int tz) {
		switch (tz) {
		case 39600:
			tzStr = "GMT-11:00";
			break;
		case 36000:
			tzStr = "GMT-10:00";
			break;
		case 32400:
			tzStr = "GMT-09:00";
			break;
		case 28800:
			tzStr = "GMT-08:00";
			break;
		case 25200:
			tzStr = "GMT-07:00";
			break;
		case 21600:
			tzStr = "GMT-06:00";
			break;
		case 18000:
			tzStr = "GMT-05:00";
			break;
		case 14400:
			tzStr = "GMT-04:00";
			break;
		case 12600:
			tzStr = "GMT-03:30";
			break;
		case 10800:
			tzStr = "GMT-03:00";
			break;
		case 7200:
			tzStr = "GMT-02:00";
			break;
		case 3600:
			tzStr = "GMT-01:00";
			break;
		case 0:
			tzStr = "GMT";
			break;
		case -3600:
			tzStr = "GMT+01:00";
			break;
		case -7200:
			tzStr = "GMT+02:00";
			break;
		case -10800:
			tzStr = "GMT+03:00";
			break;
		case -12600:
			tzStr = "GMT+03:30";
			break;
		case -14400:
			tzStr = "GMT+04:00";
			break;
		case -16200:
			tzStr = "GMT+04:30";
			break;
		case -18000:
			tzStr = "GMT+05:00";
			break;
		case -19800:
			tzStr = "GMT+05:30";
			break;

		case -21600:
			tzStr = "GMT+06:00";
			break;
		case -25200:
			tzStr = "GMT+07:00";
			break;
		case -28800:
			tzStr = "GMT+08:00";
			break;
		case -32400:
			tzStr = "GMT+09:00";
			break;
		case -34200:
			tzStr = "GMT+09:30";
			break;
		case -36000:
			tzStr = "GMT+10:00";
			break;
		case -39600:
			tzStr = "GMT+11:00";
			break;
		case -43200:
			tzStr = "GMT+12:00";
			break;
		default:
			break;
		}
	}

	// 拍照
	private void takePicture(final Bitmap bmp) {
		if (!isTakepic) {
			isTakepic = true;
			new Thread() {
				public void run() {
					savePicToSDcard(bmp);
				}
			}.start();
		} else {
			return;
		}
	}
	/*
	 * 保存到本地
	 * 注意：此处可以做本地数据库sqlit 保存照片，以便于到本地照片观看界面从SQLite取出照片
	 */
	private int i=0;//拍照张数标志
	private synchronized void savePicToSDcard(final Bitmap bmp) {
		String strDate = getStrDate();
		//String date = strDate.substring(0, 10);
		FileOutputStream fos = null;
		try {
			File div = new File(Environment.getExternalStorageDirectory(),
					"ipcamerademo/takepic");
			if (!div.exists()) {
				div.mkdirs();
			}
			++i;
			Log.e("", i+"");
			File file = new File(div, strDate + "_"+ strDID + "_"+ i +".jpg");
			fos = new FileOutputStream(file);
			if (bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos))
			{
				fos.flush();
				Log.d("tag", "takepicture success");
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showToast(R.string.ptz_takepic_ok);
					}
				});
			}
		} catch (Exception e) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showToast(R.string.ptz_takepic_fail);
				}
			});
			Log.d("tag", "exception:" + e.getMessage());
			e.printStackTrace();
		} finally {
			isTakepic = false;
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fos = null;
			}
		}
	}
	//时间格式
	private String getStrDate() {
		Date d = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd_HH_mm");
		String strDate = f.format(d);
		return strDate;
	}

	private Bitmap mBmp;
	@Override
	public void callBackPlaybackVideoData(byte[] videobuf, int h264Data,
			int len, final int width, final int height,int time,int frameType,int originframeLen) {
		Log.e("videodate","h264Data"+h264Data+"fremetype"+frameType);
		i1++;
		if (exit == false) {
			exit = true;
			new MyThread().start();
		}
		
		this.time = time;
		videodata = videobuf;
		videoDataLen = len;
		nVideoWidth = width;
		nVideoHeight = height;
		time1 = this.time * 1000;
		if(time >0)
		timeShow = setDeviceTime(time1, tzStr);
		if (h264Data == 1) { // H264

			if (isTakepic && width > 0&& height >0&& frameType != 6) {
				isTakepic = false;
				Log.e("vst","nV"+height+width);


						byte[] rgb = new byte[width * height * 2];
						NativeCaller.YUV4202RGB565(videodata, rgb, width, height);
						ByteBuffer buffer = ByteBuffer.wrap(rgb);
						mBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
						mBmp.copyPixelsFromBuffer(buffer);
						savePicToSDcard(mBmp);
						//takePicture(mBmp);


			}
			mHandler.sendEmptyMessage(1);
		} else { // MJPEG
			mHandler.sendEmptyMessage(2);
		}
		
		///音频数据
		if (h264Data == 1 && frameType == 6)
		{
			// Log.e("vst ", "h264Data == 0 && FrameType == 6 ");
			/*
			 * if (!isOpen) { StartAudio(); isOpen = true; }
			 */
			if (!audioPlayer.isAudioPlaying()) {
				return;
			}

			CustomBufferHead head = new CustomBufferHead();
			CustomBufferData data = new CustomBufferData();
			head.length = len;
			 //Log.e("vst ", "h264Data == 0 && FrameType == 6 " + "len" + len);
			head.startcode = AUDIO_BUFFER_START_CODE;
			data.head = head;
			data.data = videobuf;
			AudioBuffer.addData(data);

			Message msg = new Message();
			Bundle b = new Bundle();
			b.putInt("oneFramesize", originframeLen);
			msg.setData(b);
			return;
		}
	}
	

	///开启音频
	private void StartAudio() {
		synchronized (this) {
			AudioBuffer.ClearAll();
			audioPlayer.AudioPlayStart();
		}
	}

	private void StopAudio() {
		synchronized (this) {
			AudioBuffer.ClearAll();
			audioPlayer.AudioPlayStop();
		}
	}
}
