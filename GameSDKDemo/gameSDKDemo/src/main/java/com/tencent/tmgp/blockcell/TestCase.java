package com.tencent.tmgp.blockcell;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.test.AndroidTestCase;

public class TestCase extends AndroidTestCase {

	public void test() {
		SharedPreferences preferences = getContext().getSharedPreferences("huwei", Context.MODE_PRIVATE);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new java.util.Date());
		System.out.println("date=" + date);
		String[] split = date.split("-");
		int currentYear = Integer.parseInt(split[0]);
		int currentMonth = Integer.parseInt(split[1]);

		String hasBindTime = preferences.getString("hasbind", "");
		String[] split_old = hasBindTime.split("-");
		int oldYear = Integer.parseInt(split_old[0]);
		int oldMonth = Integer.parseInt(split_old[1]);
		if (currentYear >= oldYear && currentMonth > oldMonth) {// 如果今天已经提示过实名认证,则不再提示
			System.out.println("我要提示了");
			Editor editor = preferences.edit();
			editor.putString("hasbind", date);
			editor.commit();
		}
	}

}
