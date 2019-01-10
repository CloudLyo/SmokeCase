package com.cloudnew.smokecase;

public class Constant {


	//烟雾查询
	public static String SMOKE_IP="192.168.0.103";
	public static int SMOKE_port=6666;
	public static String SMOKE_CHK="01 03 00 34 00 01 c5 c4";
	public static int SMOKE_LEN=7;
	public static int SMOKE_NUM=1;


	//温湿度查询命令
	public static String TEMHUM_IP="192.168.0.104";
	public static int TEMHUM_port=6666;
	public static String TEMHUM_CHK="01 03 00 14 00 02 84 0f";
	public static int TEMHUM_LEN=9;
	public static int TEMHUM_NUM=1;


	//蜂鸣器
	public static String BUZZER_IP = "192.168.0.105";
	public static int BUZZER_port = 6666;

	public static final int NODE_LEN = 13;
	public static final int NODE_NUM = 1;
	// 命令
	public static final String CLOSEALL_CMD = "01 10 00 5a 00 02 04 00 00 00 00 76 ec";
	public static final String BUZZER_CMD = "01 10 00 5a 00 02 04 01 00 00 00 77 10";
	public static final String RED_CMD = "01 10 00 5a 00 02 04 00 01 00 00 27 2c";
	public static final String GREEN_CMD = "01 10 00 5a 00 02 04 00 00 01 00 77 7c";
	public static final String BLUE_CMD = "01 10 00 5a 00 02 04 00 00 00 01 b7 2c";
	
}