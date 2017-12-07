/**
 * HYInit.java
 * com.heyijoy.gamesdk
 *
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2014-2-20 		Administrator
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package com.heyijoy.gamesdk.act;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.SourceFromGameCenterContentResover;
import com.heyijoy.gamesdk.data.VersionInfo;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.http.HttpRequestManager;
import com.heyijoy.gamesdk.lib.HYBuildConfig;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.R;
import com.heyijoy.gamesdk.update.DownloadService;
import com.heyijoy.gamesdk.update.FileUtil;
import com.heyijoy.gamesdk.util.Logger;
import com.heyijoy.gamesdk.widget.HYDialog;
import com.heyijoy.gamesdk.widget.HYProgressDlg;
import com.heyijoy.sdk.analytics.UDAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ClassName:HYInit
 * 
 * @author msh
 * @Date 2014-2-20 下午4:53:11
 */
public class HYInit {
	public static boolean isNotify = false;
	private Context context;
	private HYProgressDlg proDlg;
	private boolean mIsBound;// 是否绑定了服务
	private Messenger mService = null;
	private HYCallBack callbackRe;
	public static VersionInfo remoteVersionInfo = null;
	boolean isInstallDig = true;// 是否自动弹出安装对话框
	private ProgressBar mProgressBar;
	private Button btn_install;
	private Button btn_install_cancel;
	private TextView processtv_size;
	private TextView processtv_percent;
	private TextView progesstv_allSize;
	private HYDialog dlgLoading;
	private String loadingSizeStr;
//	private List<String> mlist = null;
//	private List<String> mYKlist = null;
	private HYProgressDlg dialog = null;
	private String pack = "";
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				HYCallBack callBack = (HYCallBack) msg.obj;
				getPackFormRemote(callBack);
				break;
			default:
				break;
			}
		}
	};

	public HYInit(Context context) {
		this.context = context;
	}

	@SuppressLint("InlinedApi")
	public void init(HYCallBack callback, boolean isTest) {
		this.callbackRe = callback;
		HYBuildConfig.IS_TEST = isTest;// 初始化时控制url测试还是正式环境
		GameSDKApplication.getInstance().init(context.getApplicationContext());

		dialog = HYProgressDlg.show(context, "合乐智趣游戏  ", "初始化…");
//		mlist = new ArrayList<String>();
//		mYKlist = new ArrayList<String>();

		HYCallBack callBack = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
//				if (dialog != null && dialog.isShowing()) {
//					dialog.dismiss();
//				}

				final HYDialog dlg = new HYDialog(context, R.style.dialog);
				// Util.getTaskList(context,"init");
				// getFloatOrder();
				remoteVersionInfo = (VersionInfo) bean;
				if ("no".equals(remoteVersionInfo.getUpdateFlag())) {// 不需要升级
																		// 或者正在升级
					callbackRe.onSuccess(null);
					return;
				} else if ("can".equals(remoteVersionInfo.getUpdateFlag()) && DownloadService.isWorked(context)) {
					Intent intent = new Intent(context, DownloadService.class);
					context.startService(intent);
					callbackRe.onSuccess(null);
					return;
				} else if ("must".equals(remoteVersionInfo.getUpdateFlag()) && DownloadService.isWorked(context)) {
					startUpdate(false);
					/*
					 * Toast.makeText(context, "游戏正在更新，请稍后查看。。。",
					 * Toast.LENGTH_LONG).show(); Activity activity =
					 * (Activity)context; activity.finish();
					 */
					return;
				}

				/*
				 * //关闭 没有sd卡也能升级的功能。 if
				 * (!Environment.getExternalStorageState().equals("mounted")) {
				 * callbackRe.onFailed(context.getString(R.string.
				 * hy_no_sd_card_info)); return ; }
				 */

				LinearLayout loginLayout1 = (LinearLayout) dlg.getLayoutInflater().inflate(R.layout.dialog_hy_update,
						null);
				dlg.setCanceledOnTouchOutside(false);
				dlg.setContentView(loginLayout1);
				TextView tv_uptate_msg = (TextView) loginLayout1.findViewById(R.id.tv_update_msg);
				TextView tv_title_text = (TextView) loginLayout1.findViewById(R.id.tv_title_text);
				tv_title_text.setText("版本更新");

				if (remoteVersionInfo.getRemoteSize() == FileUtil.getLocalSize()) {// 本地已经下载完成
					tv_uptate_msg.setText("有新版本未安装！\n" + remoteVersionInfo.getVersionDetail());
					Button btn_update = (Button) loginLayout1.findViewById(R.id.btn_update);
					btn_update.setText("安装");
					btn_update.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							install(remoteVersionInfo.getInstallFileName());
						}
					});
				} else {
					tv_uptate_msg.setText(remoteVersionInfo.getVersionDetail());
					Button btn_update = (Button) loginLayout1.findViewById(R.id.btn_update);
					btn_update.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (FileUtil.isAvaiableSpace(remoteVersionInfo.getRemoteSize() - FileUtil.getLocalSize())) {
								startUpdate(true);
								dlg.dismiss();
							} else {// 空间不足
								Toast.makeText(context, "磁盘空间不足，请清理后重试", Toast.LENGTH_SHORT).show();
							}

						}
					});
				}
				Button btn_cancel = (Button) loginLayout1.findViewById(R.id.btn_cancel);

				if ("must".equals(remoteVersionInfo.getUpdateFlag())) {
					btn_cancel.setText("退出");
					btn_cancel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							System.exit(0);
						}
					});
				} else if ("can".equals(remoteVersionInfo.getUpdateFlag())) {
					btn_cancel.setText("取消");
					btn_cancel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dlg.dismiss();
							callbackRe.onSuccess(remoteVersionInfo);
						}
					});
				}
				dlg.setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
							return true;
						}
						return false;
					}
				});
				dlg.show();
			}

			@Override
			public void onFailed(int code, String failReason) {
//				if (dialog != null && dialog.isShowing()) {
//					dialog.dismiss();
//				}
				callbackRe.onFailed(code, failReason);
				Log.e(HYConstant.TAG, "code=" + code + ",failReason=" + failReason);
				// getFloatOrder();
				if (HttpRequestManager.STATE_ERROR_WITHOUT_NETWORK.equals(failReason)
						|| HttpRequestManager.STATE_ERROR_NETWORK_NOTWELL.equals(failReason)) {
					Toast.makeText(context, failReason, Toast.LENGTH_SHORT).show();
				} else {
					// callbackRe.onFailed(HYConstant.EXCEPTION_CODE, "初始化失败");
					Toast.makeText(context, "初始化失败,请重新启动游戏", Toast.LENGTH_SHORT).show();
				}
			}
		};

		Message msg = Message.obtain();
		msg.obj = callBack;
		msg.what = 1;
		handler.sendMessageDelayed(msg, 1000);

	}

	private void getPackFormRemote(HYCallBack callBack) {

//		mYKlist = mlist;
//		if (mYKlist.size() >= 1) {
//			pack = ((mYKlist.get(0)).split("&"))[0];// 从主客获取的格式为 包名&版本号，这里获取包名
//			Logger.d("pack&ver", "size>=1,packageName=" + pack);
//		}
//		Editor edit = GameSDKApplication.getInstance().getSPForPackXML().edit();
//		if ("".equals(pack)) {
//			Logger.d("pack&ver", "接收超时");
//			pack = GameSDKApplication.getInstance().getPckNameBySign();
//		}
//		edit.putString(HYConstant.PACK_FROM_GAMECENTER, pack);
//		edit.commit();
//		SourceFromGameCenterContentResover.getInstance().setSource();// 查询并存储source(从合乐智趣主客来源位置信息,需要在初始化之前执行)

		// if (!GameSDKApplication.getInstance().isStandAlone()){
		// HttpApi.getInstance().requestAnnouncement();// 单机模式不调用
		// }

		HttpApi.getInstance().getRemoteVersion(callBack);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		}, 1000);
	}

	private void startUpdate(boolean start) {

		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.YK_DOWNLOAD_RECEIVER");

		LinearLayout ll = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_loadapk, null);
		TextView tv_title_text = (TextView) ll.findViewById(R.id.tv_title_text);
		tv_title_text.setText("版本更新");
		mProgressBar = (ProgressBar) ll.findViewById(R.id.down_pb);
		processtv_size = (TextView) ll.findViewById(R.id.tv_loadingsize);
		processtv_percent = (TextView) ll.findViewById(R.id.tv_progress);
		progesstv_allSize = (TextView) ll.findViewById(R.id.tv_allsize);
		btn_install = (Button) ll.findViewById(R.id.btn_install);
		btn_install_cancel = (Button) ll.findViewById(R.id.btn_cancel);
		btn_install_cancel.setVisibility(View.GONE);
		dlgLoading = new HYDialog(context, R.style.dialog);
		dlgLoading.setContentView(ll);
		dlgLoading.setCanceledOnTouchOutside(false);
		setHTdownloadBtn();
		dlgLoading.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}
		});
		// 开始绑定服务 并且开启服务，服务开启后等待500毫秒 已保证此时已经绑定成功 当服务返回消息开始下载时 loading框消失 并弹出
		// 下载对话框
		proDlg = HYProgressDlg.show(context, "loading…");
		// 启动下载
		doBindService();// 绑定 服务 之后再启动服务 保证服务被杀掉后还会重新startCommand
		if (start) {
			Intent intent = new Intent(context, DownloadService.class);
			context.startService(intent);
		}

	}

	private Handler activityHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (proDlg != null) {
				proDlg.dismiss();
			}

			if (!dlgLoading.isShowing() && mIsBound) {
				dlgLoading.show();
			}
			DecimalFormat df = new DecimalFormat("0.00");
			switch (msg.what) {
			case DownloadService.START:

				int loadingSize = (int) msg.getData().getLong("SIZE");
				float floadingSize = ((float) loadingSize) / (1024 * 1024.f);
				loadingSizeStr = df.format(floadingSize);
				progesstv_allSize.setText("/" + loadingSizeStr + "MB");
				int progess = (int) msg.getData().getLong("CURRENTSIZE");
				mProgressBar.setMax(loadingSize); // 开始
				mProgressBar.setProgress(progess);
				break;
			case DownloadService.LOADING:
				setHTdownloadBtn();// 下载过程

				int percent = msg.getData().getInt("PERCENT");
				int currentSize = (int) msg.getData().getLong("CURRENTSIZE");
				float fcurrentSize = ((float) currentSize / (1024 * 1024.f));
				String currentSizeStr = df.format(fcurrentSize);
				int loadingSize1 = (int) msg.getData().getLong("SIZE");
				float floadingSize1 = ((float) loadingSize1) / (1024 * 1024.f);
				loadingSizeStr = df.format(floadingSize1);
				progesstv_allSize.setText("/" + loadingSizeStr + "MB");
				mProgressBar.setMax(loadingSize1);

				mProgressBar.setProgress(currentSize);
				processtv_percent.setText(percent + "%");
				processtv_size.setText(currentSizeStr + "MB");
				break;
			case DownloadService.FAIL:
				/*
				 * if(msg.arg1 == 0){ Toast.makeText(context,
				 * HttpRequestManager.STATE_ERROR_NETWORK_NOTWELL,
				 * Toast.LENGTH_SHORT).show(); }else if(msg.arg1 == 1){
				 * Toast.makeText(context, "sd卡空间不足，请清理后重试！",
				 * Toast.LENGTH_SHORT).show(); }
				 */
				processtv_percent.setText("下载失败");
				btn_install_cancel.setVisibility(View.VISIBLE);
				if ("can".equals(remoteVersionInfo.getUpdateFlag())) {
					btn_install_cancel.setText("取消");
					btn_install_cancel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							doUnbindService();
							dlgLoading.dismiss();
							callbackRe.onSuccess(remoteVersionInfo);
						}
					});
				} else if ("must".equals(remoteVersionInfo.getUpdateFlag())) {

					btn_install_cancel.setText("退出");
					btn_install_cancel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							try {// 退出系统前 停止已经开始的下载服务 当程序被用户杀掉后 下载不停止
									// //退出时清除notice
								Intent intent = new Intent(context, DownloadService.class);
								context.stopService(intent);
								DownloadService.notificationMrg.cancel(R.string.hy_notify_no);
							} catch (Exception e) {
							}
							System.exit(0);
						}
					});
				}
				setBtnReDownLoad();
				break;
			case DownloadService.OVER:
				int size = (int) msg.getData().getLong("SIZE");
				float floatsize = ((float) size) / (1024 * 1024.f);
				String sizeStr = df.format(floatsize);
				mProgressBar.setProgress(mProgressBar.getMax());
				processtv_size.setText(sizeStr + "MB");
				progesstv_allSize.setText("/" + sizeStr + "MB");
				processtv_percent.setText(100 + "%");
				btn_install_cancel.setVisibility(View.VISIBLE);
				if ("can".equals(remoteVersionInfo.getUpdateFlag())) {
					btn_install_cancel.setText("取消");
					btn_install_cancel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							dlgLoading.dismiss();
							callbackRe.onSuccess(remoteVersionInfo);
						}
					});
				} else if ("must".equals(remoteVersionInfo.getUpdateFlag())) {
					btn_install_cancel.setText("退出");
					btn_install_cancel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							System.exit(0);
						}
					});
				}
				btn_install.setVisibility(View.VISIBLE);
				btn_install.setText("点击安装");
				btn_install.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						install(remoteVersionInfo.getInstallFileName());
					}
				});
				doUnbindService();
				install(remoteVersionInfo.getInstallFileName());
				break;
			}
		};
	};
	final Messenger mMessenger = new Messenger(activityHandler);

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = new Messenger(service);
			try {
				Message msg = Message.obtain(null, DownloadService.MESSAGE_START);
				msg.replyTo = mMessenger;
				mService.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	void doBindService() {
		context.bindService(new Intent(context, DownloadService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			if (mService != null) {
				try {
					Message msg = Message.obtain(null, 0);
					msg.replyTo = mMessenger;
					mService.send(msg);
					context.unbindService(mConnection);
					mIsBound = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressLint("NewApi")
	public void install(String filename) {
		if (FileUtil.getFileType(filename).equals("apk")) {
			String str = context.getCacheDir().getAbsolutePath();
			new File(str).mkdir();
			authorize(str, 505, -1, -1);

			String str1 = FileUtil.setMkdir(context);
			new File(str1).mkdir();
			authorize(str1, 505, -1, -1);
			String path = FileUtil.setMkdir(context) + "/" + filename;

			authorizeFile(path);

			File file = new File(path);
			/*
			 * file.setReadable(true); file.setWritable(true);
			 */
			Intent intentInstall = new Intent(Intent.ACTION_VIEW);
			intentInstall.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			context.startActivity(intentInstall);
		}
	}

	public static boolean authorizeFile(String paramString) {

		String[] command = { "chmod", "777", paramString };
		ProcessBuilder builder = new ProcessBuilder(command);
		try {
			builder.start();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * try { Class localClass = Class.forName("android.os.FileUtils");
		 * Method localMethod = localClass.getMethod("setPermissions", new
		 * Class[] { String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE });
		 * localMethod.invoke(null, new Object[] { paramString,
		 * Integer.valueOf(paramInt1), Integer.valueOf(-1), Integer.valueOf(-1)
		 * }); return true; } catch (Exception localClassNotFoundException) { }
		 */
		return false;
	}

	private void setHTdownloadBtn() {
		btn_install.setText("后台下载");
		btn_install.setVisibility(View.VISIBLE);
		btn_install.setClickable(true);

		btn_install_cancel.setVisibility(View.GONE);// 取消或者退出
		if ("can".equals(remoteVersionInfo.getUpdateFlag())) {// 可以后台下载
			btn_install.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// isInstallDig = false;
					isNotify = false;
					Toast.makeText(context, "下载完成后点击通知栏进行安装", Toast.LENGTH_SHORT).show();
					doUnbindService();
					dlgLoading.dismiss();
					callbackRe.onSuccess(remoteVersionInfo);
				}
			});
		} else if ("must".equals(remoteVersionInfo.getUpdateFlag())) {
			btn_install.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "下载完成后点击通知栏进行安装", Toast.LENGTH_SHORT).show();
					Activity activity = (Activity) context;
					activity.finish();
					// System.exit(0);
					/*
					 * Intent i= new Intent(Intent.ACTION_MAIN);
					 * i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 * //android123提示如果是服务里调用，必须加入new task标识
					 * i.addCategory(Intent.CATEGORY_HOME);
					 * context.startActivity(i);
					 */
				}
			});

			/*
			 * btn_install_cancel.setText("退出");
			 * btn_install_cancel.setOnClickListener(new View.OnClickListener()
			 * {
			 * 
			 * @Override public void onClick(View arg0) { try {//退出系统前
			 * 停止已经开始的下载服务 当程序被用户杀掉后 下载不停止 //退出时清除notice Intent intent=new
			 * Intent(context, DownloadService.class);
			 * context.stopService(intent);
			 * DownloadService.notificationMrg.cancel(R.string.hy_notify_no); }
			 * catch (Exception e) { } System.exit(0); } });
			 */
		}
	}

	private void setBtnReDownLoad() {
		btn_install.setClickable(true);
		btn_install.setText("重新下载");
		btn_install.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btn_install.setClickable(false);
				doBindService();
				Intent intent = new Intent(context, DownloadService.class);
				context.startService(intent);
			}
		});
	}

	public static boolean authorize(String paramString, int paramInt1, int paramInt2, int paramInt3) {
		try {
			Class localClass = Class.forName("android.os.FileUtils");
			Method localMethod = localClass.getMethod("setPermissions",
					new Class[] { String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE });
			localMethod.invoke(null,
					new Object[] { paramString, Integer.valueOf(paramInt1), Integer.valueOf(-1), Integer.valueOf(-1) });
			return true;
		} catch (Exception localClassNotFoundException) {
			localClassNotFoundException.printStackTrace();
		}
		return false;
	}

}
