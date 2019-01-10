package com.cloudnew.smokecase;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Jorble on 2016/3/4.
 */
public class BuzzerConnectTask extends AsyncTask<Void, Void, Void> {

	private Context context;
	private Boolean statu;

	private byte[] read_buff;

	private Button btn;
	String btn_text = "连接中";

	private static Socket mSocket;
	private SocketAddress mSocketAddress;
	private static InputStream inputStream;
	private static OutputStream outputStream;

	private Boolean STATU = false;

	public BuzzerConnectTask(Context context, Button btn) {
		this.context = context;
		this.btn = btn;
	}

	/**
	 * 更新界面
	 */
	@Override
	protected void onProgressUpdate(Void... values) {
		btn.setText(btn_text);
	}

	/**
	 * 准备
	 */
	@Override
	protected void onPreExecute() {
	}
	
	/**
	 * 子线程任务
	 * 
	 * @param params
	 * @return
	 */
	@Override
	protected Void doInBackground(Void... params) {
		mSocket = new Socket();
		mSocketAddress = new InetSocketAddress(Constant.BUZZER_IP, Constant.BUZZER_port);
		try {
			// socket连接
			while(!isSuccess()){
				if (mSocket!=null) {
					try {
						mSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				connect();
			}
			btn_text = "报警";
			publishProgress();
			Thread.sleep(200);
			((HomeActivity)context).buzzerControlTask = new BuzzerControlTask(context,((HomeActivity)context).tv_tem,((HomeActivity)context).tv_hum,((HomeActivity)context).tv_smoke,((HomeActivity)context).btn_baojing,getInputStream(),getOutputStream());
			((HomeActivity)context).buzzerControlTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void connect(){
		btn_text = "连接中";
		publishProgress();
		if (mSocket==null||mSocket.isClosed()) mSocket = new Socket();
		try {
			mSocket.connect(mSocketAddress, 3000);// 设置连接超时时间为3秒
			mSocket.setSoTimeout(1000);
			inputStream = mSocket.getInputStream();// 得到输入流
			outputStream = mSocket.getOutputStream();// 得到输出流
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 判断socket是否还在连接
	 * 
	 * @return
	 */
	public Boolean isSuccess() {
		return mSocket.isConnected();
	}

	/**
	 * 获取socket
	 * 
	 * @return
	 */
	public static Socket getmSocket() {
		return mSocket;
	}

	/**
	 * 获取输入流
	 * 
	 * @return
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * 获取输出流
	 * 
	 * @return
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}

	public Boolean getSTATU() {
		return STATU;
	}

	public void setSTATU(Boolean sTATU) {
		STATU = sTATU;
	}

}