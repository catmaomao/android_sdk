package com.heyijoy.gamesdk.memfloat;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 悬浮窗service
 * @author shaohuma
 *
 */
public class FloatService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
//	public static Boolean isShowFloat;
//	private WindowManager wm = null;
//	private WindowManager.LayoutParams wmParams = null;
//	private View view;
//	private float mTouchStartX;
//	private float mTouchStartY;
//	private float x;
//	private float y;
//	private Boolean isMoving = true;
//	private LinearLayout floatLayout, subLayout;
//	private HYRelativeLayout logoLayout;
//	private ImageView iv_float_logo, iv_red_point, ivPersonal, hideImg, changeImg;
//	public static boolean isFirst = true;
//	private int delaytime = 1000;
//	private Timer timer = null;
//	private TimerTask timerTask = null;
//	private final int MAX_TIME = 10;
//	private int recLen = MAX_TIME;
//	private AlertDialog exitDialog;
//	private IntentFilter intentFilter;
//	private FloatReceiver floatReceiver;
//
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		try {
//			view = LayoutInflater.from(this).inflate(R.layout.hy_float, null);
//			view.setVisibility(View.GONE);
//			floatLayout = (LinearLayout) view.findViewById(R.id.hy_float_layout);
//			logoLayout = (HYRelativeLayout) view.findViewById(R.id.hy_float_logo_layout);
//			subLayout = (LinearLayout) view.findViewById(R.id.hy_float_sub_layout);
//			iv_float_logo = (ImageView) view.findViewById(R.id.hy_float_logo_img);
//			iv_red_point = (ImageView) view.findViewById(R.id.hy_float_present_logo_pot_img);//红点
//
//			ivPersonal = (ImageView) view.findViewById(R.id.hy_float_personal_img);//个人中心
//			hideImg = (ImageView) view.findViewById(R.id.hy_float_hide_img);//隐藏图标
//			changeImg = (ImageView) view.findViewById(R.id.hy_float_change_account_img);//切换账户
//
//		} catch (Exception e) {
//			HYPlatform.closeHYFloat(FloatService.this);
//			e.printStackTrace();
//		}
//
//	}
//
//	@Override
//	public void onStart(Intent intent, int startId) {
//		registerReceiver();
//		try {
//			if (intent != null) {
//				createView();
//				viewGone();
//				handler.postDelayed(task, delaytime);
//				restartTimer();
//				isFirst = true;
//				isShowFloat = true;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void viewGone() {
////		ivPersonal.setVisibility(View.GONE);
//		changeImg.setVisibility(View.GONE);
//		floatLayout.setBackgroundDrawable(null);
//		subLayout.setVisibility(View.GONE);
//		iv_red_point.setVisibility(View.GONE);
//	}
//
//	private void createView() throws Exception {
//		// 获取WindowManager
//		wm = (WindowManager) getApplicationContext().getSystemService("window");
//		// 设置LayoutParams(全局变量）相关参数
//		wmParams = GameSDKApplication.getInstance().getMywmParams();
//		if (wmParams == null) {
//			HYPlatform.closeHYFloat(FloatService.this);
//			return;
//		}
//		wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
//		// wmParams.type = 2002;
//		wmParams.flags |= 8;
//		// wmParams.flags |= 8 |
//		// WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
//
//		wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
//		// 以屏幕左上角为原点，设置x、y初始值
//		wmParams.x = 0;
//		wmParams.y = 200;
//		// 设置悬浮窗口长宽数据
//		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//		wmParams.format = 1;
//
//		wm.addView(view, wmParams);
//
//		logoLayout.setOnTouchListener(new OnTouchListener() {
//			public boolean onTouch(View v, MotionEvent event) {
//				// 获取相对屏幕的坐标，即以屏幕左上角为原点
//				if (subLayout.getVisibility() == View.VISIBLE)
//					return false;// 悬浮窗展开时不能移动
//				x = event.getRawX();
//				y = event.getRawY(); // 25是系统状态栏的高度
//				// y = event.getRawY() - getStatusBarHeight(); // 25是系统状态栏的高度
//				if (isMoving) {
//					isMoving = false;
//					// 获取相对View的坐标，即以此View左上角为原点
//					mTouchStartX = event.getX();
//					mTouchStartY = event.getY();
//				}
//				if (iv_float_logo.getVisibility() == View.VISIBLE) {
//					iv_float_logo.setBackgroundResource(R.drawable.suspend_view);
//				}
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:
//					return false;
//				case MotionEvent.ACTION_MOVE:
//					updateViewPosition();
//					return v.onTouchEvent(event);
//
//				case MotionEvent.ACTION_UP:
//					isMoving = true;
//					updateViewPosition();
//					mTouchStartX = mTouchStartY = 0;
//					restartTimer();
//					move2Side();
//					break;
//				}
//				return false;
//			}
//		});
//		
//		iv_float_logo.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				restartTimer();
//				if (subLayout.getVisibility() == View.GONE) {
//					viewVisible();
//				} else {
//					viewGone();
//				}
//			}
//		});
//
//		/**
//		 * 个人中心
//		 */
//		ivPersonal.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Util.dontClick(v);
//				isFirst = false;
//				viewGone();
//				isShowFloat = false;
//				view.setVisibility(View.GONE);
//				restartTimer();
//				Intent intent = new Intent(FloatService.this, HYInitActivity.class);
//				intent.putExtra("from", "moreByFloatService");
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				FloatService.this.startActivity(intent);
//			}
//		});
//
//		/**
//		 * 隐藏图标
//		 */
//		hideImg.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Util.dontClick(v);
//				viewGone();
//				restartTimer();
//				showSystemDialog();
//			}
//		});
//
//		changeImg.setOnClickListener(new OnClickListener() {// 切换账号
//			@Override
//			public void onClick(View v) {
//				Util.dontClick(v);
//				callMethod_05();
//			}
//		});
//	}
//
//	private void viewVisible() {
//		floatLayout.setBackgroundResource(R.drawable.hy_float_layout_bg);
//		subLayout.setVisibility(View.VISIBLE);
//	}
//	
//	private void registerReceiver() {// 注册接收广播
//		intentFilter = new IntentFilter();
//		floatReceiver = new FloatReceiver();
//		intentFilter.addAction(HYConstant.YK_SDK_FLOAT);
//		registerReceiver(floatReceiver, intentFilter);
//	}
//
//	/**
//	 * 显示关闭悬浮窗提示
//	 */
//	private void showSystemDialog() {
//
//		AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.dialog);
//		exitDialog = alert.create();
//		exitDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
//		exitDialog.show();
//		LinearLayout loginLayout1 = (LinearLayout) exitDialog.getLayoutInflater().inflate(R.layout.hy_float_show_exit,
//				null);
//		exitDialog.setCanceledOnTouchOutside(false);
//		exitDialog.setContentView(loginLayout1);
//		TextView exitTitle = (TextView) loginLayout1.findViewById(R.id.tv_title_text);
//		TextView exitContent = (TextView) loginLayout1.findViewById(R.id.hy_dialog_show_exit_content_txt);
//		exitTitle.setText("隐藏图标");
//		exitContent.setText("请确认是否隐藏图标,隐藏后将在下次登录时重新启动");
//		Button btnConfirm = (Button) loginLayout1.findViewById(R.id.btn_confirm);
//		btnConfirm.setText("确认");
//		btnConfirm.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (exitDialog != null && exitDialog.isShowing())
//					exitDialog.dismiss();
//				HYPlatform.closeHYFloat(FloatService.this);
//			}
//		});
//		Button btnCancel = (Button) loginLayout1.findViewById(R.id.btn_cancel);
//		btnCancel.setText("取消");
//		btnCancel.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (exitDialog != null && exitDialog.isShowing())
//					exitDialog.dismiss();
//			}
//		});
//	}
//
//
//	private Handler handler = new Handler();
//	private Runnable task = new Runnable() {
//		public void run() {
//			if (wm != null && wmParams != null) {
//				try {
//					handler.postDelayed(this, delaytime);
//					wm.updateViewLayout(view, wmParams);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	};
//
//
//	private void callMethod_05() {// 切换账号
//		viewGone();
//		Intent intent = new Intent();
//		intent.setAction(HYConstant.YK_CHANGE_ACCOUNT + GameSDKApplication.getInstance().getAppid());
//		intent.putExtra(HYConstant.YK_CHANGE_ACCOUNT, HYConstant.YK_CHANGE_ACCOUNT);
//		sendBroadcast(intent);
//	}
//
//
//	private void updateViewPosition() {
//		// 更新浮动窗口位置参数
//		wmParams.x = (int) (x - mTouchStartX);
//		wmParams.y = (int) (y - mTouchStartY);
//		wm.updateViewLayout(view, wmParams);
//	}
//
//	private void move2Side() {
//		TranslateAnimation animation = new TranslateAnimation(400, 400, 0, 0);
//		animation.setDuration(400);
//		animation.setFillAfter(true);
//		view.startAnimation(animation);
//	}
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		return null;
//	}
//
//	private void restartTimer() {
//		try {
//			if (iv_float_logo.getVisibility() == View.VISIBLE) {
//				iv_float_logo.setBackgroundResource(R.drawable.suspend_view);
//			}
//			if (timer != null) {
//				timer.cancel();
//				timer = null;
//			}
//			if (timerTask != null) {
//				timerTask.cancel();
//				timerTask = null;
//			}
//			if (timer == null) {
//				timer = new Timer();
//			}
//			if (timerTask == null) {
//				timerTask = new TimerTask() {
//					@Override
//					public void run() {
//						if (recLen > -1)
//							recLen--;
//						Message message = new Message();
//						message.what = 1;
//						handlerTimer.sendMessage(message);
//					}
//				};
//			}
//			recLen = MAX_TIME;
//			if (timer != null && timerTask != null)
//				timer.schedule(timerTask, 1000, 1000);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	final Handler handlerTimer = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 1:
//				if (!isShowFloat || !Util.isForeground(FloatService.this)) {
//					view.setVisibility(View.GONE);
//					if (exitDialog != null && exitDialog.isShowing())
//						exitDialog.hide();
//				} else {
//					view.setVisibility(View.VISIBLE);
//					if (exitDialog != null && exitDialog.isShowing())
//						exitDialog.show();
//				}
//
//				if (recLen == 5 && subLayout.getVisibility() == View.VISIBLE) {
//					viewGone();
//				}
//				if (recLen == 0)
//					iv_float_logo.setBackgroundResource(R.drawable.suspend_view_static);
//			}
//		}
//	};
//
//	private class FloatReceiver extends BroadcastReceiver {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			synchronized (FloatService.this) {
//				try {
//					isShowFloat = true;
//					String name = intent.getStringExtra("from");
//					if ("preCenter".equals(name)) {
////						callMethod_02();
//					}
//					// unregisterReceiver(floatReceiver);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	@Override
//	public void onDestroy() {
//		try {
//			unregisterReceiver(floatReceiver);
//		} catch (Exception e) {
//			HYPlatform.closeHYFloat(FloatService.this);
//			e.printStackTrace();
//		}
//		handler.removeCallbacks(task);
//		try {
//			if (wm != null)
//				wm.removeView(view);
//			wm = null;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			if (timerTask != null) {
//				timerTask.cancel();
//				timerTask = null;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		super.onDestroy();
//	}
}