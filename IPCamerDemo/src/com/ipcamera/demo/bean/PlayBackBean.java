package com.ipcamera.demo.bean;

import java.io.Serializable;

public class PlayBackBean implements Serializable{
 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String did;
	private String path;
	private int videotime;
	public String getDid() {
		return did;
	}
	public void setDid(String did) {
		this.did = did;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public void setVideotime(int time){
		this.videotime=time;
	}
	public int getVideotime(){
		return videotime;
	}
	}
