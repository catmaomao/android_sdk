package com.heyijoy.gamesdk.memfloat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heyijoy.gamesdk.R;
import com.heyijoy.gamesdk.act.HYPlatform;
import com.heyijoy.gamesdk.activity.HYInitActivity;
import com.heyijoy.gamesdk.util.Util;

import java.util.Timer;
import java.util.TimerTask;

public class FloatView extends FrameLayout implements OnTouchListener {

	private final int HANDLER_TYPE_HIDE_LOGO = 100;// 隐藏LOGO

	private WindowManager.LayoutParams mWmParams;
	private WindowManager mWindowManager;
	private Context mContext;

	// private View mRootFloatView;
	private ImageView mIvFloatLogo;
	private LinearLayout mLlFloatMenu;
	private ImageView hy_float_personal_img;
	private ImageView hy_float_hide_img;
	private FrameLayout mFlFloatLogo;

	private boolean mIsRight;// logo是否在右边
	private boolean mCanHide;// 是否允许隐藏
	private float mTouchStartX;
	private float mTouchStartY;
	private int mScreenWidth;
	private int mScreenHeight;
	private boolean mDraging;

	private View rootFloatView;

	private Timer mTimer;
	private TimerTask mTimerTask;

	final Handler mTimerHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == HANDLER_TYPE_HIDE_LOGO) {
				if (!FloatViewService.isShowFloat || !Util.isForeground(mContext)) {
					if (getVisibility() == View.VISIBLE) {
						setVisibility(View.GONE);
					}
				} else {
					if (getVisibility() == View.GONE) {
						setVisibility(View.VISIBLE);
					}
				}

				// 比如隐藏悬浮框
				if (mCanHide) {
					mCanHide = false;
					if (mIsRight) {
						mIvFloatLogo.setImageResource(ResourceUtils.getDrawableId(mContext, "logo_right"));
					} else {
						mIvFloatLogo.setImageResource(ResourceUtils.getDrawableId(mContext, "logo_left"));
					}
					mWmParams.alpha = 0.7f;
					mWindowManager.updateViewLayout(FloatView.this, mWmParams);
					refreshFloatMenu(mIsRight);
					mLlFloatMenu.setVisibility(View.GONE);
				}
			}
			super.handleMessage(msg);
		}
	};

	public FloatView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context mContext) {
		this.mContext = mContext;
		Context applicationContext = mContext.getApplicationContext();
		mWindowManager = (WindowManager) applicationContext.getSystemService(Context.WINDOW_SERVICE);
		// 更新浮动窗口位置参数 靠边
		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		mWindowManager.getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
		this.mWmParams = new WindowManager.LayoutParams();
		// 设置window type
		mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
		// 设置图片格式，效果为背景透明
		mWmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		// 调整悬浮窗显示的停靠位置为左侧
		mWmParams.gravity = Gravity.LEFT | Gravity.TOP;

		// mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();

		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		mWmParams.x = 0;
		mWmParams.y = 200;
		// mWmParams.y = mScreenHeight / 2;

		// 设置悬浮窗口长宽数据
		mWmParams.width = LayoutParams.WRAP_CONTENT;
		mWmParams.height = LayoutParams.WRAP_CONTENT;
		addView(createView(mContext));
		mWindowManager.addView(this, mWmParams);

		mTimer = new Timer();
		// show();
	}

	/**
	 * 创建Float view
	 * 
	 * @param context
	 * @return
	 */
	private View createView(final Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		// 从布局文件获取浮动窗口视图
		rootFloatView = inflater.inflate(ResourceUtils.getLayoutId(context, "hy_widget_float_view"), null);
		mFlFloatLogo = (FrameLayout) rootFloatView.findViewById(ResourceUtils.getId(context, "hy_float_view"));

		mIvFloatLogo = (ImageView) rootFloatView
				.findViewById(ResourceUtils.getId(context, "hy_float_view_icon_imageView"));
		mLlFloatMenu = (LinearLayout) rootFloatView.findViewById(ResourceUtils.getId(context, "ll_menu"));
		hy_float_personal_img = (ImageView) rootFloatView
				.findViewById(ResourceUtils.getId(context, "hy_float_personal_img"));
		hy_float_personal_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mLlFloatMenu.setVisibility(View.GONE);
				openUserCenter();
			}
		});
		hy_float_hide_img = (ImageView) rootFloatView.findViewById(ResourceUtils.getId(context, "hy_float_hide_img"));
		hy_float_hide_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				openHideFloat();
				mLlFloatMenu.setVisibility(View.GONE);
			}
		});
		rootFloatView.setOnTouchListener(this);
		rootFloatView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mDraging) {
					if (mLlFloatMenu.getVisibility() == View.VISIBLE) {
						mLlFloatMenu.setVisibility(View.GONE);
					} else {
						mLlFloatMenu.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		rootFloatView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

		return rootFloatView;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		removeTimerTask();
		// 获取相对屏幕的坐标，即以屏幕左上角为原点
		int x = (int) event.getRawX();
		int y = (int) event.getRawY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mTouchStartX = event.getX();
			mTouchStartY = event.getY();
			mIvFloatLogo.setImageResource(ResourceUtils.getDrawableId(mContext, "suspend_view"));
			mWmParams.alpha = 1f;
			mWindowManager.updateViewLayout(this, mWmParams);
			mDraging = false;
			break;
		case MotionEvent.ACTION_MOVE:
			float mMoveStartX = event.getX();
			float mMoveStartY = event.getY();
			// 如果移动量大于3才移动
			if (Math.abs(mTouchStartX - mMoveStartX) > 3 && Math.abs(mTouchStartY - mMoveStartY) > 3) {
				mDraging = true;
				// 更新浮动窗口位置参数
				mWmParams.x = (int) (x - mTouchStartX);
				mWmParams.y = (int) (y - mTouchStartY);
				mWindowManager.updateViewLayout(this, mWmParams);
				mLlFloatMenu.setVisibility(View.GONE);
				return false;
			}

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:

			if (mWmParams.x >= mScreenWidth / 2) {
				mWmParams.x = mScreenWidth;
				mIsRight = true;
			} else if (mWmParams.x < mScreenWidth / 2) {
				mIsRight = false;
				mWmParams.x = 0;
			}
			mIvFloatLogo.setImageResource(ResourceUtils.getDrawableId(mContext, "suspend_view"));
			refreshFloatMenu(mIsRight);
			timerForHide();
			mWindowManager.updateViewLayout(this, mWmParams);
			// 初始化
			mTouchStartX = mTouchStartY = 0;
			break;
		}
		return false;
	}

	/**
	 * 隐藏悬浮窗
	 */
	public void hide() {
		FloatViewService.isShowFloat = false;
		Message message = mTimerHandler.obtainMessage();
		message.what = HANDLER_TYPE_HIDE_LOGO;
		mTimerHandler.sendMessage(message);
		removeTimerTask();
	}

	/**
	 * 显示悬浮窗
	 */
	public void show() {
		FloatViewService.isShowFloat = true;
		if (getVisibility() != View.VISIBLE) {
			mIvFloatLogo.setImageResource(ResourceUtils.getDrawableId(mContext, "suspend_view"));
			mWmParams.alpha = 1f;
			mWindowManager.updateViewLayout(this, mWmParams);
			timerForHide();
		}
	}

	/**
	 * 刷新float view menu
	 * 
	 * @param right
	 */
	private void refreshFloatMenu(boolean right) {
		if (right) {
			FrameLayout.LayoutParams paramsFloatImage = (FrameLayout.LayoutParams) mIvFloatLogo.getLayoutParams();
			paramsFloatImage.gravity = Gravity.RIGHT;
			mIvFloatLogo.setLayoutParams(paramsFloatImage);
			FrameLayout.LayoutParams paramsFlFloat = (FrameLayout.LayoutParams) mFlFloatLogo.getLayoutParams();
			paramsFlFloat.gravity = Gravity.RIGHT;
			mFlFloatLogo.setLayoutParams(paramsFlFloat);

			int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
					mContext.getResources().getDisplayMetrics());
			int padding52 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46,
					mContext.getResources().getDisplayMetrics());
			LinearLayout.LayoutParams paramsMenuAccount = (LinearLayout.LayoutParams) hy_float_personal_img
					.getLayoutParams();
			paramsMenuAccount.rightMargin = padding;
			paramsMenuAccount.leftMargin = padding;
			hy_float_personal_img.setLayoutParams(paramsMenuAccount);

			LinearLayout.LayoutParams paramsMenuFb = (LinearLayout.LayoutParams) hy_float_hide_img.getLayoutParams();
			paramsMenuFb.rightMargin = padding52;
			paramsMenuFb.leftMargin = padding;
			hy_float_hide_img.setLayoutParams(paramsMenuFb);
		} else {
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mIvFloatLogo.getLayoutParams();
			params.setMargins(0, 0, 0, 0);
			params.gravity = Gravity.LEFT;
			mIvFloatLogo.setLayoutParams(params);
			FrameLayout.LayoutParams paramsFlFloat = (FrameLayout.LayoutParams) mFlFloatLogo.getLayoutParams();
			paramsFlFloat.gravity = Gravity.LEFT;
			mFlFloatLogo.setLayoutParams(paramsFlFloat);

			int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
					mContext.getResources().getDisplayMetrics());
			int padding52 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46,
					mContext.getResources().getDisplayMetrics());

			LinearLayout.LayoutParams paramsMenuAccount = (LinearLayout.LayoutParams) hy_float_personal_img
					.getLayoutParams();
			paramsMenuAccount.rightMargin = padding;
			paramsMenuAccount.leftMargin = padding52;
			hy_float_personal_img.setLayoutParams(paramsMenuAccount);

			LinearLayout.LayoutParams paramsMenuFb = (LinearLayout.LayoutParams) hy_float_hide_img.getLayoutParams();
			paramsMenuFb.rightMargin = padding;
			paramsMenuFb.leftMargin = padding;
			hy_float_hide_img.setLayoutParams(paramsMenuFb);
		}
	}

	/**
	 * 定时隐藏float view
	 */
	private void timerForHide() {
		mCanHide = true;

		// 结束任务
		if (mTimerTask != null) {
			try {
				mTimerTask.cancel();
				mTimerTask = null;
			} catch (Exception e) {
			}

		}
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				Message message = mTimerHandler.obtainMessage();
				message.what = HANDLER_TYPE_HIDE_LOGO;
				mTimerHandler.sendMessage(message);
			}
		};
		if (mCanHide) {
			mTimer.schedule(mTimerTask, 2000, 1000);
		}
	}

	/**
	 * 打开用户中心
	 */
	private void openUserCenter() {
		setVisibility(View.GONE);
		FloatViewService.isShowFloat = false;
		Intent intent = new Intent(mContext, HYInitActivity.class);
		intent.putExtra("from", "moreByFloatService");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}

	private AlertDialog exitDialog;

	/**
	 * 消失悬浮窗
	 */
	private void openHideFloat() {
		AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.dialog);
		exitDialog = alert.create();
		exitDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		exitDialog.show();
		LinearLayout loginLayout1 = (LinearLayout) exitDialog.getLayoutInflater().inflate(R.layout.hy_float_show_exit,
				null);
		exitDialog.setCanceledOnTouchOutside(false);
		exitDialog.setContentView(loginLayout1);
		TextView exitTitle = (TextView) loginLayout1.findViewById(R.id.tv_title_text);
		TextView exitContent = (TextView) loginLayout1.findViewById(R.id.hy_dialog_show_exit_content_txt);
		exitTitle.setText("隐藏图标");
		exitContent.setText("请确认是否隐藏图标,隐藏后将在下次登录时重新启动");
		Button btnConfirm = (Button) loginLayout1.findViewById(R.id.btn_confirm);
		btnConfirm.setText("确认");
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (exitDialog != null && exitDialog.isShowing()) {
					exitDialog.dismiss();
				}
				hide();
				HYPlatform.closeHYFloat(mContext);
			}
		});
		Button btnCancel = (Button) loginLayout1.findViewById(R.id.btn_cancel);
		btnCancel.setText("取消");
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (exitDialog != null && exitDialog.isShowing())
					exitDialog.dismiss();
			}
		});
	}

	/**
	 * 是否Float view
	 */
	public void destroy() {
		hide();
		removeFloatView();
		removeTimerTask();
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		try {
			mTimerHandler.removeMessages(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void removeTimerTask() {
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}

	private void removeFloatView() {
		try {
			mWindowManager.removeView(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// 更新浮动窗口位置参数 靠边
		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		mWindowManager.getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
		int oldX = mWmParams.x;
		int oldY = mWmParams.y;
		switch (newConfig.orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:// 横屏
			if (mIsRight) {
				mWmParams.x = mScreenWidth;
				mWmParams.y = oldY;
			} else {
				mWmParams.x = oldX;
				mWmParams.y = oldY;
			}
			break;
		case Configuration.ORIENTATION_PORTRAIT:// 竖屏
			if (mIsRight) {
				mWmParams.x = mScreenWidth;
				mWmParams.y = oldY;
			} else {
				mWmParams.x = oldX;
				mWmParams.y = oldY;
			}
			break;
		}
		mWindowManager.updateViewLayout(this, mWmParams);
	}
}
