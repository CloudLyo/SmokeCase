package util;

import android.util.Log;

import com.cloudnew.smokecase.SmokeConnectTask;

/**
 * Created by CloudNew on 1/9/2019
 * cloudnew@foxmail.com.
 */
public class RunWithTimeout {
    Runnable task;
    long timeOut;
    public RunWithTimeout(Runnable task, long timeOut) {
        this.task = task;
        this.timeOut = timeOut;
    }

    public boolean run(){
        Thread t = new Thread(task);
        long st = System.currentTimeMillis();
        long det_t;
        t.start();
        while(true){
            det_t=System.currentTimeMillis()-st;
            if (det_t >= timeOut||!t.isAlive()) break;
        }
        Log.d("TAG",t.isAlive()+"");
        if (t.isAlive()){
            Log.d("TAG","false");
            return false;
        } else{
            //Log.d("TAG","true");
            return true;
        }
    }
}
