package com.cloudnew.smokecase;

import android.content.Context;
import android.os.AsyncTask;
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

	private InputStream inputStream;
	private OutputStream outputStream;

	public BuzzerControlTask(Context context, InputStream inputStream, OutputStream outputStream, String cmd) {
		this.context = context;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.cmd = cmd;
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
			// 发送命令
			StreamUtil.writeCommand(outputStream, cmd);
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
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return null;
	}

}