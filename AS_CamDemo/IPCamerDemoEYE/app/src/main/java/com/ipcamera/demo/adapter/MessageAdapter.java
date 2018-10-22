package com.ipcamera.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.ipcamer.demo.R;
import com.ipcamer.demo.R.drawable;
import com.ipcamer.demo.R.id;
import com.ipcamer.demo.R.layout;
import com.ipcamer.demo.R.string;
import com.ipcamera.demo.bean.MessageBean;
import com.ipcamera.demo.bean.WifiScanBean;

import java.util.ArrayList;

public class MessageAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private ArrayList<MessageBean> list;
	private ViewHolder holder;

	public MessageAdapter(Context context, ArrayList<MessageBean> mlist) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		list = mlist;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater
					.inflate(layout.message_list_item, null);
			holder = new ViewHolder();
			// holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.dz = (TextView) convertView.findViewById(id.dz);
			holder.time = (TextView) convertView
					.findViewById(id.time);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MessageBean mMessageBean = list.get(position);

		if(mMessageBean.getDz().equals("18"))
		{
			holder.dz.setText(context.getResources().getString(R.string.alerm_motion_alarm));
		}else if(mMessageBean.getDz().equals("online"))
		{
			holder.dz.setText(context.getResources().getString(R.string.pppp_status_online));
		}else if(mMessageBean.getDz().equals("offline"))
		{
			holder.dz.setText(context.getResources().getString(string.dev_offline));
		}

		holder.time.setText(timeStamp2Date(mMessageBean.getDate(),"yyyy-MM-dd HH:mm:ss"));

		return convertView;
	}



	private class ViewHolder {
		TextView dz;
		TextView time;

	}

	public static String timeStamp2Date(String seconds,String format) {
		if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
		return "";
		}
		if(format == null || format.isEmpty()){
		     format = "yyyy-MM-dd HH:mm:ss"; }
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(Long.valueOf(seconds+"000")));
		}
}
