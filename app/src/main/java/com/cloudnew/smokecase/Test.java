package com.cloudnew.smokecase;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import util.FROTemHum;
import util.StreamUtil;

/**
 * Created by CloudNew on 1/8/2019
 * cloudnew@foxmail.com.
 */
public class Test {
    static private Context context;
    static private TemHumData data;
    static private Float dataTemp;

    static private byte[] read_buff;

    static private TextView tem_tv;
    static private TextView hum_tv;

    static private Socket mSocket;
    static private SocketAddress mSocketAddress;
    static private InputStream inputStream;
    static private OutputStream outputStream;

    static private Boolean STATU = false;
    static private Boolean CIRCLE = true;

    public static void main(String args[]){
        data = new TemHumData();
        mSocket = new Socket();
        mSocketAddress = new InetSocketAddress(Constant.TEMHUM_IP, Constant.TEMHUM_port);
        try {
            // socket连接
            System.out.print("connecting\n");
            mSocket.connect(mSocketAddress, 3000);// 设置连接超时时间为3秒
            if (mSocket.isConnected()) {
                inputStream = mSocket.getInputStream();// 得到输入流
                outputStream = mSocket.getOutputStream();// 得到输出流
                System.out.print("connected\n");
            } else {
                System.out.print("connect faild\n");
            }

            // 循环读取数据
            while (CIRCLE) {
                // 查询温湿度
                StreamUtil.writeCommand(outputStream, Constant.TEMHUM_CHK);
                Thread.sleep(200);
                read_buff = StreamUtil.readData(inputStream);
                System.out.print(read_buff.length);

                dataTemp = FROTemHum.getTemData(Constant.TEMHUM_LEN, Constant.TEMHUM_NUM, read_buff);
                System.out.print(dataTemp+"\n");
                if (dataTemp != null) {
                    data.setTem((int)(float)dataTemp);
                }
                dataTemp = FROTemHum.getHumData(Constant.TEMHUM_LEN, Constant.TEMHUM_NUM, read_buff);
                System.out.print(dataTemp+"\n");
                if (dataTemp != null) {
                    data.setHum((int)(float)dataTemp);
                }

                Thread.sleep(200);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
