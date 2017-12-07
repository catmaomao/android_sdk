/**
 * HYDialog.java
 * com.heyijoy.gamesdk.widget
 *
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-3-7 		Administrator
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

/**
 * ClassName:HYDialog
 * @author   msh
 * @Date	 2014-3-7		下午4:18:32
 *
 */
public class HYDialog extends Dialog{
	private Context activity;
	public HYDialog(Context context) {
		super(context);
		this.activity = context;
	}
	public HYDialog(Context context,int style) {
		super(context,style);
		this.activity = context;
	}
	/*public void show(int width,int height) {//根据屏幕密度大小显示
		Window dialogWindow = this.getWindow();
	     WindowManager.LayoutParams lp = dialogWindow.getAttributes();
	     WindowManager m = activity.getWindowManager();
	     DisplayMetrics dm = new DisplayMetrics();  
	     activity.getWindowManager().getDefaultDisplay().getMetrics(dm);  
	     float density  = dm.density;      // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
	     int densityDpi = dm.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
	     
	    lp.height = (int) (height*density);
	    lp.width = (int) (width*density);	
		super.show();
	}*/
	@Override
	public void show() {
		//添加动画效果
		/*Window dialogWindow = this.getWindow();
		dialogWindow.setWindowAnimations(R.style.dialogWindowAnim_hy);*/
		if(activity instanceof Activity){
			Activity ac = (Activity)activity;
			if(ac.isFinishing()){
				return;
			}
		}
		
		super.show();
		
	}
}

