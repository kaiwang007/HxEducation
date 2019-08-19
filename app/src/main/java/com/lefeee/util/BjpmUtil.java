package com.lefeee.util;

import java.util.ArrayList;

public class BjpmUtil {
	public static ArrayList<String> numArr = new ArrayList<String>(); // 学号
	public static ArrayList<String> scoreArr = new ArrayList<String>(); // 分数
	public static ArrayList<String> nameArr = new ArrayList<String>(); // 姓名
	public static ArrayList<String> bjmcArr = new ArrayList<String>(); // 班级名次
	public static ArrayList<String> njmcArr = new ArrayList<String>(); // 年级名次

	public static int getLength() {
		return scoreArr.size();
	}

	public static void clear() {
		scoreArr.clear();
		nameArr.clear();
		numArr.clear();
		bjmcArr.clear();
		njmcArr.clear();
	}

	public static void mcSort() {
		int temp ;
//		Log.d("mcsort", "size: " + scoreArr.size());
		for (int i = 0; i < scoreArr.size() ; i++) {
			temp =0;
			for (int j = 0; j < scoreArr.size() ; j++) {
//				Log.d("mcsort", "i: " + i + " j: " + j);
				if ( Float.valueOf(scoreArr.get(i)) >=  Float.valueOf(scoreArr.get(j)))
					temp++;
			}
			bjmcArr.set(i,  String.valueOf( scoreArr.size() - temp +1));
		}

	}

}
