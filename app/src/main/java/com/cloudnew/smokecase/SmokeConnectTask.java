package com.cloudnew.smokecase;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import util.FROSmoke;
import util.RunWithTimeout;
import util.StreamUtil;

/**
 * Created by CloudNew on 1/8/2019
 * cloudnew@foxmail.com.
 */
public class SmokeConnectTask extends AsyncTask<Void,Void,Void> {

    private Context context;
    private SmokeData data;
    private Float smoke;

    private byte[] read_buff;

    private TextView smoke_tv;
    String smoke_text = "连接中";

    private Socket mSocket;
    private SocketAddress mSocketAddress;
    private InputStream inputStream;
    private OutputStream outputStream;

    private Boolean STATU = false;
    private Boolean CIRCLE = false;

    public SmokeConnectTask(Context context, TextView smoke_tv) {
        this.context = context;
        this.data = new SmokeData();
        this.smoke_tv = smoke_tv;
    }

    /**
     * 更新界面
     */
    @Override
    protected void onProgressUpdate(Void... values) {
        smoke_tv.setText(smoke_text);
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
        mSocketAddress = new InetSocketAddress(Constant.SMOKE_IP, Constant.SMOKE_port);
        mSocket = new Socket();
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
        while (CIRCLE) {
            // 查询烟雾值
            try {

                Log.d("TAG","writing");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StreamUtil.writeCommand(outputStream, Constant.SMOKE_CHK);
                    }
                }).start();
                Thread.sleep(200);
                Log.d("TAG","reading");
                read_buff = StreamUtil.readData(inputStream);
                Log.d("TAG","complete "+new String(read_buff));

                smoke = FROSmoke.getData(Constant.SMOKE_LEN, Constant.SMOKE_NUM, read_buff);
                if (smoke != null) {
                    data.setSun((int)(float)smoke);
                }else{
                    while (!isSuccess()) connect();
                    continue;
                }
                // 更新界面
                Log.d("TAG",data.getSun()+"$");
                smoke_text = data.getSun()+"";
                publishProgress();
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
                while (!isSuccess()){
                    connect();
                }
                continue;
            }
        }
        return null;
    }

    public void connect(){
        Log.d("TAG","smoke connecting");
        smoke_text = "连接中";
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
    public Socket getmSocket() {
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

    public Boolean getCIRCLE() {
        return CIRCLE;
    }

    public void setCIRCLE(Boolean cIRCLE) {
        CIRCLE = cIRCLE;
    }
}
