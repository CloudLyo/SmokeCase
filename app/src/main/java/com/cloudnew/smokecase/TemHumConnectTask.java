package com.cloudnew.smokecase;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import util.FROSmoke;
import util.FROTemHum;
import util.RunWithTimeout;
import util.StreamUtil;

/**
 * Created by CloudNew on 1/8/2019
 * cloudnew@foxmail.com.
 */
public class TemHumConnectTask extends AsyncTask<Void,Void,Void> {

    private Context context;
    private TemHumData data;
    private Float dataTemp;

    private byte[] read_buff;

    private TextView tem_tv;
    private TextView hum_tv;
    String tem_text = "连接中";
    String hum_text = "连接中";

    private Socket mSocket;
    private SocketAddress mSocketAddress;
    private InputStream inputStream;
    private OutputStream outputStream;

    private Boolean STATU = false;
    private Boolean CIRCLE = false;

    public TemHumConnectTask(Context context, TextView tem_tv, TextView hum_tv) {
        this.context = context;
        this.data = new TemHumData();
        this.tem_tv = tem_tv;
        this.hum_tv = hum_tv;
    }

    /**
     * 更新界面
     */
    @Override
    protected void onProgressUpdate(Void... values) {
        tem_tv.setText(tem_text);
        hum_tv.setText(hum_text);
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
        mSocketAddress = new InetSocketAddress(Constant.TEMHUM_IP, Constant.TEMHUM_port);
        mSocket = new Socket();
        while(!isSuccess()&&CIRCLE){
            if (mSocket!=null) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            connect();
        }
        // 循环读取数据
        while (CIRCLE) {
            // 查询温湿度
            try{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StreamUtil.writeCommand(outputStream, Constant.TEMHUM_CHK);
                    }
                }).start();
                Thread.sleep(200);
                read_buff = StreamUtil.readData(inputStream);
                dataTemp = FROTemHum.getTemData(Constant.TEMHUM_LEN, Constant.TEMHUM_NUM, read_buff);
                if (dataTemp != null) {
                    data.setTem((int)(float)dataTemp);
                }else{
                    while(!isSuccess()&&CIRCLE){
                        if (mSocket!=null) {
                            try {
                                mSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        connect();
                    }
                    continue;
                }
                dataTemp = FROTemHum.getHumData(Constant.TEMHUM_LEN, Constant.TEMHUM_NUM, read_buff);
                if (dataTemp != null) {
                    data.setHum((int)(float)dataTemp);
                }else{
                    while (!isSuccess()) connect();
                    continue;
                }
                // 更新界面
                tem_text = String.valueOf(data.getTem());
                hum_text = String.valueOf(data.getHum());
                publishProgress();
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
                while(!isSuccess()&&CIRCLE){
                    if (mSocket!=null) {
                        try {
                            mSocket.close();
                        } catch (IOException e1) {
                            e.printStackTrace();
                        }
                    }
                    connect();
                }continue;
            }
        }
        return null;
    }

    public void connect(){
        tem_text = "连接中";
        hum_text = "连接中";
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
