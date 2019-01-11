package com.cloudnew.smokecase;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

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
    public LineChartView lineChartView;
    public ArrayList<PointValue> pointValueList;
    public ArrayList<Line> linesList;
    public Axis axisX,axisY;
    public LineChartData lineChartData;
    public boolean isFinish = false;
    public int position = 0;
    public Thread chartThread;
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

        smoke_down = sp.getInt("smoke_down",-1);
        if (smoke_down!=-1) Constant.smoke_down = smoke_down;

        tem_up = sp.getInt("tem_up",-1);
        if (tem_up!=-1) Constant.tem_up = tem_up;

        tem_down = sp.getInt("tem_down",-1);
        if (tem_down!=-1) Constant.tem_down = tem_down;

        hum_up = sp.getInt("hum_up",-1);
        if (hum_up!=-1) Constant.hum_up = hum_up;

        hum_down = sp.getInt("hum_down",-1);
        if (hum_down!=-1) Constant.hum_down = hum_down;
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
        pointValueList = new ArrayList<>();
        if (chartThread!=null&&chartThread.isAlive()){
            chartThread.interrupt();
        }
        initAxisView();
        showMovingLineChart();
    }

    private void initAxisView() {

        lineChartView = findViewById(R.id.line_chart);
        linesList = new ArrayList<>();
        /** 初始化Y轴 */
        axisY = new Axis();
        axisY.setName("温度（°C）");//添加Y轴的名称
        axisY.setHasLines(true);//Y轴分割线
        axisY.setTextSize(10);//设置字体大小
//        axisY.setTextColor(Color.parseColor("#AFEEEE"));//设置Y轴颜色，默认浅灰色
        lineChartData = new LineChartData(linesList);
        lineChartData.setAxisYLeft(axisY);//设置Y轴在左边

        /** 初始化X轴 */
        axisX = new Axis();
        axisX.setHasTiltedLabels(false);//X坐标轴字体是斜的显示还是直的，true是斜的显示
//        axisX.setTextColor(Color.CYAN);//设置X轴颜色
        axisX.setName("时间（s）");//X轴名称
        axisX.setHasLines(true);//X轴分割线
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(0);//设置0的话X轴坐标值就间隔为1

        List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
        for (int i = 0; i < 61; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(i+""));
        }
        axisX.setValues(mAxisXValues);//填充X轴的坐标名称

        lineChartData.setAxisXBottom(axisX);//X轴在底部
        lineChartView.setLineChartData(lineChartData);

        Viewport port = initViewPort(0,6);//初始化X轴6个间隔坐标
        lineChartView.setCurrentViewportWithAnimation(port);

        lineChartView.setInteractive(false);//设置不可交互
        lineChartView.setScrollEnabled(true);
        lineChartView.setValueTouchEnabled(false);
        lineChartView.setFocusableInTouchMode(false);
        lineChartView.setViewportCalculationEnabled(false);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.startDataAnimation();

    }

    private Viewport initViewPort(float left,float right) {
        Viewport port = new Viewport();
        port.top = 80;//Y轴上限，固定(不固定上下限的话，Y轴坐标值可自适应变化)
        port.bottom = 0;//Y轴下限，固定
        port.left = left;//X轴左边界，变化
        port.right = right;//X轴右边界，变化
        return port;
    }

    private void showMovingLineChart() {
        chartThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if (!tv_tem.getText().toString().equals("连接中")) pointValueList.add(new PointValue(position,Integer.parseInt(tv_tem.getText().toString())));//实时添加新的点
                    //根据新的点的集合画出新的线
                    Line line = new Line(pointValueList);
                    line.setColor(Color.parseColor("#FFCD41"));//设置折线颜色
                    line.setShape(ValueShape.CIRCLE);//设置折线图上数据点形状为 圆形 （共有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
                    line.setCubic(false);//曲线是否平滑，true是平滑曲线，false是折线
                    line.setHasLabels(true);//数据是否有标注
//        line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据,设置了line.setHasLabels(true);之后点击无效
                    line.setHasLines(true);//是否用线显示，如果为false则没有曲线只有点显示
                    line.setHasPoints(true);//是否显示圆点 ，如果为false则没有原点只有点显示（每个数据点都是个大圆点）

                    linesList.add(line);
                    lineChartData.setLines(linesList);
                    lineChartData.setAxisYLeft(axisY);//设置Y轴在左
                    lineChartData.setAxisXBottom(axisX);//X轴在底部
                    lineChartView.setLineChartData(lineChartData);
                    //float xAxisValue = position;
                    //根据点的横坐标实时变换X坐标轴的视图范围
                    Viewport port;
                    if(position > 20){
                        port = initViewPort(position - 20,position);
                    }
                    else {
                        port = initViewPort(0,20);
                    }
                    lineChartView.setMaximumViewport(port);
                    lineChartView.setCurrentViewport(port);

                    position++;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (pointValueList.size()>=61){
                        position = 20;
                        linesList.clear();
                        ArrayList<PointValue> tlist = pointValueList;
                        pointValueList = new ArrayList<>();
                        for (int i=41;i<=60;++i){
                            pointValueList.add(new PointValue(i-41,tlist.get(i).getY()));
                        }
                    }
                }
            }
        });
        chartThread.start();
    }
}
