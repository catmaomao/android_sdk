/**
 * HYProgressDlg.java
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


import com.heyijoy.gamesdk.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ClassName:HYProgressDlg
 * 
 * @author msh
 * @Date 2014-3-7 上午11:14:20
 */
public class HYProgressDlg extends Dialog {

	private static HYProgressDlg ykProgerssDlg = null;
	private static String[] str = { "小酷正在催促信号君...", "信号君撇了小酷一眼...",
			"小酷拾取了道具板凳...", "小酷正在殴打信号君...", "小酷疯狂殴打信号君...", "小酷正在呼叫救护车...",
			"救护车在抢救信号君...", "小酷被信号君反殴中..." };

	public HYProgressDlg(Context context) {
		super(context);
	}

	public HYProgressDlg(Context context, int theme) {
		super(context, theme);
	}

	/*
	 * public static HYProgressDlg createDialog(Context context){ ykProgerssDlg
	 * = new HYProgressDlg(context,R.style.hyprogressDialog);
	 * ykProgerssDlg.setContentView(R.layout.dialog_ykprogress);
	 * ykProgerssDlg.getWindow().getAttributes().gravity = Gravity.CENTER;
	 * return ykProgerssDlg; }
	 */
	public static HYProgressDlg show(Context context, String title, String msg) {
		ykProgerssDlg = new HYProgressDlg(context, R.style.hyprogressDialog);
		try {
			Activity activity = (Activity) context;
			if (!activity.isFinishing()) {
				ykProgerssDlg.setContentView(R.layout.dialog_hyprogress);
				TextView tvTitle = (TextView) ykProgerssDlg
						.findViewById(R.id.tv_loading_title);
				View lyTitle = (View) ykProgerssDlg.findViewById(R.id.rl_title);
				View llLine = (View) ykProgerssDlg.findViewById(R.id.ll_line);
//				lyTitle.setVisibility(View.VISIBLE);
//				llLine.setVisibility(View.VISIBLE);
				tvTitle.setText(title);
				ykProgerssDlg.getWindow().getAttributes().gravity = Gravity.CENTER;
				ykProgerssDlg.setMessage(msg);
				ykProgerssDlg.setCancelable(false);
				ykProgerssDlg.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ykProgerssDlg;
	}

	public static HYProgressDlg show(Context context, String msg) {
		ykProgerssDlg = new HYProgressDlg(context, R.style.hyprogressDialog);
		ykProgerssDlg.setContentView(R.layout.dialog_hyprogress);
		ykProgerssDlg.getWindow().getAttributes().gravity = Gravity.CENTER;
		ykProgerssDlg.setMessage(msg);
		ykProgerssDlg.setCancelable(false);
		ykProgerssDlg.show();
		return ykProgerssDlg;
	}

	/**
	 * 
	 * @param context
	 * @param msg
	 * @param newType
	 *            true：表示新的模式；false：表示采用旧的模式
	 * @return
	 */
	public static HYProgressDlg show(Context context, String msg,
			boolean newType) {
		if (newType) {
			ykProgerssDlg = new HYProgressDlg(context,
					R.style.hyprogressDialog2);
			ykProgerssDlg.setContentView(R.layout.dialog_hyprogress_new);
			ykProgerssDlg.getWindow().getAttributes().gravity = Gravity.CENTER;
			Animation shake = AnimationUtils.loadAnimation(context,
					R.anim.shake);
			TextView tvMsg = (TextView) ykProgerssDlg
					.findViewById(R.id.tv_loadingmsg);
			ykProgerssDlg.setMessage(shake, context, tvMsg, 0);
			ykProgerssDlg.setCancelable(false);
			ykProgerssDlg.show();
		} else {
			show(context, msg);
		}
		return ykProgerssDlg;
	}

	public HYProgressDlg setMessage(String strMessage) {
		TextView tvMsg = (TextView) ykProgerssDlg
				.findViewById(R.id.tv_loadingmsg);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}
		return ykProgerssDlg;
	}

	public HYProgressDlg setMessage(final Animation shake,
			final Context context, final TextView tvMsg, final int i) {
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (tvMsg != null) {
					if (i < 8) {
						tvMsg.setText(str[i]);
					} else {
						tvMsg.setText(str[7]);
					}
					if (shake != null) {
						tvMsg.startAnimation(shake);
					}
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(5000);
								int j = i + 1;
								setMessage(shake, context, tvMsg, j);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
		return ykProgerssDlg;
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		if (ykProgerssDlg == null) {
			return;
		}
		ImageView imageView = (ImageView) ykProgerssDlg
				.findViewById(R.id.loadingImageView);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView
				.getBackground();
		animationDrawable.start();
	}

}
