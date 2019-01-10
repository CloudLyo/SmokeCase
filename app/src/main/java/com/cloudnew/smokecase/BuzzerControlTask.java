package com.cloudnew.smokecase;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.io.OutputStream;

import util.FROIOControl;
import util.StreamUtil;

/**
 * Created by Jorble on 2016/3/4.
 */
public class BuzzerControlTask extends AsyncTask<Void, Void, Void> {

	private Context context;
	private Boolean statu;
	private String cmd;
	private byte[] read_buff;
	TextView tv_tem;
	TextView tv_hum;
	TextView tv_smoke;
	Button btn_baojing;
	private InputStream inputStream;
	private OutputStream outputStream;

	public BuzzerControlTask(Context context, TextView tv_tem, TextView tv_hum, TextView tv_smoke, Button btn_baojing,InputStream inputStream, OutputStream outputStream) {
		this.context = context;
		this.tv_tem = tv_tem;
		this.tv_hum = tv_hum;
		this.tv_smoke = tv_smoke;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.btn_baojing = btn_baojing;
	}

	/**
	 * 更新界面
	 */
	@Override
	protected void onProgressUpdate(Void... values) {
		if (statu == true) {
			Toast.makeText(context, "操作成功！", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(context, "操作失败！", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 子线程任务
	 * 
	 * @param params
	 * @return
	 */
	@Override
	protected Void doInBackground(Void... params) {
		try {
			while (true){
				if ((tv_smoke.getText().toString().equals("连接中")||Integer.parseInt(tv_smoke.getText().toString())<=30)
						&&(tv_tem.getText().toString().equals("连接中")||Integer.parseInt(tv_tem.getText().toString())<=25)
						//&&(tv_hum.getText().toString().equals("连接中")||Integer.parseInt(tv_hum.getText().toString())<=27)
					) continue;
				// 发送命令
				Log.d("tag","buzzer");
				StreamUtil.writeCommand(outputStream, Constant.BUZZER_CMD);
				Thread.sleep(200);
				//读取返回值
				read_buff = StreamUtil.readData(inputStream);
				// 如果设备无返回值，直接返回null
				if(read_buff==null || read_buff.length<Constant.NODE_LEN) return null;
				//判断是否操作成功
				statu = FROIOControl.isSuccess(Constant.NODE_LEN, Constant.NODE_NUM, read_buff);
				// 更新界面
				publishProgress();
				Thread.sleep(1000);

				StreamUtil.writeCommand(outputStream, Constant.CLOSEALL_CMD);
				Thread.sleep(200);
				//读取返回值
				read_buff = StreamUtil.readData(inputStream);
				// 如果设备无返回值，直接返回null
				if(read_buff==null || read_buff.length<Constant.NODE_LEN) return null;
				//判断是否操作成功
				statu = FROIOControl.isSuccess(Constant.NODE_LEN, Constant.NODE_NUM, read_buff);
				// 更新界面
				publishProgress();
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return null;
	}

}