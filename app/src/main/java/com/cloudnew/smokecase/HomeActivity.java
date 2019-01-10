package com.cloudnew.smokecase;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    TextView tv_tem;
    TextView tv_hum;
    TextView tv_smoke;
    Button btn_baojing;
    String SMOKE_IP;
    String TEMHUM_IP;
    int SMOKE_PORT;
    int TEMHUM_PORT;
    TemHumConnectTask temHumConnectTask;
    SmokeConnectTask smokeConnectTask;
    BuzzerConnectTask buzzerConnectTask;
    public BuzzerControlTask buzzerControlTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initview();
        initdata();
    }

    @Override
    protected void onResume() {
        super.onResume();

        temHumConnectTask = new TemHumConnectTask(this,tv_tem,tv_hum);
        temHumConnectTask.setCIRCLE(true);
        temHumConnectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        smokeConnectTask = new SmokeConnectTask(this,tv_smoke);
        smokeConnectTask.setCIRCLE(true);
        smokeConnectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        buzzerConnectTask = new BuzzerConnectTask(this,btn_baojing);
        buzzerConnectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    void initview(){
        bindview();
        btn_baojing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    void initdata(){
        SharedPreferences sp = getSharedPreferences("Constant",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        SMOKE_IP = sp.getString("SMOKE_IP",null);
        if (SMOKE_IP != null) Constant.SMOKE_IP = SMOKE_IP;
        SMOKE_PORT = sp.getInt("SMOKE_PORT",0);
        if (SMOKE_PORT!=0) Constant.SMOKE_port = SMOKE_PORT;
        TEMHUM_IP = sp.getString("TEMHUM_IP",null);
        if (TEMHUM_IP!=null) Constant.TEMHUM_IP = TEMHUM_IP;
        TEMHUM_PORT = sp.getInt("TEMHUM_PORT",0);
        if (TEMHUM_PORT!=0) Constant.TEMHUM_port = TEMHUM_PORT;
    }

    void bindview(){
        tv_tem = findViewById(R.id.tv_tem);
        tv_hum = findViewById(R.id.tv_hum);
        tv_smoke = findViewById(R.id.tv_smoke);
        btn_baojing = findViewById(R.id.btn_baojing);
    }
}
