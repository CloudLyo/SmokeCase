package com.cloudnew.smokecase;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import lecho.lib.hellocharts.gesture.ContainerScrollType;

public class HomeActivity extends AppCompatActivity {
    public TextView tv_tem;
    public TextView tv_hum;
    public TextView tv_smoke;
    public Button btn_baojing,btn_apply;
    public String SMOKE_IP;
    public String TEMHUM_IP;
    public int SMOKE_PORT;
    public int TEMHUM_PORT;
    public int smoke_up,smoke_down,tem_up,tem_down,hum_up,hum_down;
    public TemHumConnectTask temHumConnectTask;
    public SmokeConnectTask smokeConnectTask;
    public BuzzerConnectTask buzzerConnectTask;
    public BuzzerControlTask buzzerControlTask;
    public EditText etv_smoke_ip,etv_smoke_port,etv_temhum_ip,etv_temhum_port,etv_smoke_up,etv_smoke_down,etv_tem_up,etv_tem_down,etv_hum_up,etv_hum_down;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initdata();
        initview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMonitor();
    }

    void initview(){
        bindview();
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.SMOKE_IP = etv_smoke_ip.getText().toString();
                Constant.SMOKE_port = Integer.parseInt(etv_smoke_port.getText().toString());
                Constant.TEMHUM_IP = etv_temhum_ip.getText().toString();
                Constant.TEMHUM_port = Integer.parseInt(etv_temhum_port.getText().toString());
                Constant.smoke_down = Integer.parseInt(etv_smoke_down.getText().toString());
                Constant.smoke_up = Integer.parseInt(etv_smoke_up.getText().toString());
                Constant.tem_up = Integer.parseInt(etv_tem_up.getText().toString());
                Constant.tem_down = Integer.parseInt(etv_tem_down.getText().toString());
                Constant.hum_up = Integer.parseInt(etv_hum_up.getText().toString());
                Constant.hum_down = Integer.parseInt(etv_hum_down.getText().toString());

                SharedPreferences sp = getSharedPreferences("Constant",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putInt("SMOKE_PORT",Constant.SMOKE_port );
                editor.putInt("TEMHUM_PORT",Constant.TEMHUM_port);
                editor.putInt("smoke_down",Constant.smoke_down );
                editor.putInt("smoke_up",Constant.smoke_up);
                editor.putInt("tem_up",Constant.tem_up);
                editor.putInt("tem_down",Constant.tem_down);
                editor.putInt("hum_up",Constant.hum_up);
                editor.putInt("hum_down",Constant.hum_down);
                editor.putString("SMOKE_IP",Constant.SMOKE_IP);
                editor.putString("TEMHUM_IP",Constant.TEMHUM_IP);
                editor.commit();

            }
        });
        etv_smoke_ip.setText(Constant.SMOKE_IP);
        etv_smoke_port.setText(Constant.SMOKE_port+"");
        etv_temhum_ip.setText(Constant.TEMHUM_IP);
        etv_temhum_port.setText(Constant.TEMHUM_port+"");
        etv_smoke_up.setText(Constant.smoke_up+"");
        etv_smoke_down.setText(Constant.smoke_down+"");
        etv_tem_up.setText(Constant.tem_up+"");
        etv_tem_down.setText(Constant.tem_down+"");
        etv_hum_down.setText(Constant.hum_down+"");
        etv_hum_up.setText(Constant.hum_up+"");
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

        smoke_up = sp.getInt("smoke_up",-1);
        if (smoke_up!=-1) Constant.smoke_up = smoke_up;

        smoke_down = sp.getInt("smoke_up",-1);
        if (smoke_down!=-1) Constant.smoke_up = smoke_down;

        tem_up = sp.getInt("smoke_up",-1);
        if (tem_up!=-1) Constant.smoke_up = tem_up;

        tem_down = sp.getInt("smoke_up",-1);
        if (tem_down!=-1) Constant.smoke_up = tem_down;

        hum_up = sp.getInt("smoke_up",-1);
        if (hum_up!=-1) Constant.smoke_up = hum_up;

        hum_down = sp.getInt("smoke_up",-1);
        if (hum_down!=-1) Constant.smoke_up = hum_down;
    }

    void bindview(){
        tv_tem = findViewById(R.id.tv_tem);
        tv_hum = findViewById(R.id.tv_hum);
        tv_smoke = findViewById(R.id.tv_smoke);
        //btn_baojing = findViewById(R.id.btn_baojing);
        etv_smoke_ip = findViewById(R.id.etv_smoke_ip);
        etv_smoke_port = findViewById(R.id.etv_smoke_port);
        etv_temhum_ip = findViewById(R.id.etv_temhum_ip);
        etv_temhum_port = findViewById(R.id.etv_temhum_port);
        etv_smoke_up = findViewById(R.id.etv_smoke_up);
        etv_smoke_down = findViewById(R.id.etv_smoke_down);
        etv_tem_up = findViewById(R.id.etc_tem_up);
        etv_tem_down = findViewById(R.id.etv_tem_down);
        etv_hum_down = findViewById(R.id.etv_hum_down);
        etv_hum_up = findViewById(R.id.etv_hum_up);
        btn_apply = findViewById(R.id.btn_apply);
    }

    void startMonitor(){
        if (temHumConnectTask!=null) temHumConnectTask.setCIRCLE(false);
        temHumConnectTask = new TemHumConnectTask(this,tv_tem,tv_hum);
        temHumConnectTask.setCIRCLE(true);
        temHumConnectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        if (smokeConnectTask!=null) smokeConnectTask.setCIRCLE(false);
        smokeConnectTask = new SmokeConnectTask(this,tv_smoke);
        smokeConnectTask.setCIRCLE(true);
        smokeConnectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        if (buzzerConnectTask!=null) smokeConnectTask.setCIRCLE(false);
        buzzerConnectTask = new BuzzerConnectTask(this,btn_baojing);
        buzzerConnectTask.setCIRCLE(true);
        buzzerConnectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
