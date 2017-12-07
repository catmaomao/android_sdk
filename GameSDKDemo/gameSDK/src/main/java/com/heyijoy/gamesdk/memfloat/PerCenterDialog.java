package com.heyijoy.gamesdk.memfloat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.act.HYPlatform;
import com.heyijoy.gamesdk.activity.HYInitActivity;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.HYThridParams;
import com.heyijoy.gamesdk.data.User;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.widget.BindDialog;
import com.heyijoy.gamesdk.widget.BindingPhoneDialog;
import com.heyijoy.gamesdk.widget.CertificationDialog;
import com.heyijoy.gamesdk.widget.HYDialog;
import com.heyijoy.gamesdk.widget.ModifyPwdDialog;
import com.heyijoy.gamesdk.R;

/**
 * 个人中心
 * 
 * @author shaohuma 礼包, 录屏,论坛,消息,切换账号,合乐智趣会员,领取码,交易明细,积分任务,福利,消费福利
 */
public class PerCenterDialog extends Dialog {
	private Context context;
	private HYDialog dlg;
	private Button btn_onlogout;
	private ImageView iv_delete, iv_nav_icon_right;
	private TextView tv_title_text, tv_bind_status,tv_binding_phone;
	private RelativeLayout rl_modify_pwd, rl_binding_phone, rl_certification;
	private HYCallBack method;

	public PerCenterDialog(Context context, HYCallBack method) {
		super(context);
		this.context = context;
		this.method = method;
	}

	public PerCenterDialog(Context context) {
		super(context);
		this.context = context;
	}

	public void startShow() {
		createDialog();
	}

	public void createDialog() {
		LinearLayout loginLayout1 = (LinearLayout) getLayoutInflater().inflate(R.layout.hy_float_more, null);
		dlg = new HYDialog(context, R.style.pre_dialog);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(loginLayout1);

		rl_binding_phone = (RelativeLayout) loginLayout1.findViewById(R.id.rl_binding_phone);
		rl_certification = (RelativeLayout) loginLayout1.findViewById(R.id.rl_certification);
		iv_nav_icon_right = (ImageView) loginLayout1.findViewById(R.id.iv_nav_icon_right);
		tv_bind_status = (TextView) loginLayout1.findViewById(R.id.tv_bind_status);
		tv_binding_phone = (TextView) loginLayout1.findViewById(R.id.tv_binding_phone);
		rl_modify_pwd = (RelativeLayout) loginLayout1.findViewById(R.id.rl_modify_pwd);
		btn_onlogout = (Button) loginLayout1.findViewById(R.id.btn_onlogout);
		iv_delete = (ImageView) loginLayout1.findViewById(R.id.iv_delete);
		iv_delete.setVisibility(View.VISIBLE);
		tv_title_text = (TextView) loginLayout1.findViewById(R.id.tv_title_text);
		tv_title_text.setText(HYConstant.TITLE_CENTER);
		
		//完全是为兼容打出的包是方块自己的包名，其他的包名还是走带三方绑定逻辑
		boolean isneedbind = GameSDKApplication.getInstance().isIsneedbind();
		HYThridParams thridParams = GameSDKApplication.getInstance().getThridParams();
		String type = Util.hasThridlogin(thridParams);
		if ("no".equals(type)) {//不带三方绑定，直接绑定手机号
			tv_binding_phone.setText("绑定手机号");
			tv_bind_status.setVisibility(View.VISIBLE);
			if(isneedbind){
				tv_bind_status.setText("未绑定");
				rl_binding_phone.setClickable(true);
				iv_nav_icon_right.setVisibility(View.VISIBLE);
			}else{
				rl_binding_phone.setClickable(false);
				iv_nav_icon_right.setVisibility(View.GONE);
				String phone = GameSDKApplication.getInstance().getPhonePre();
				tv_bind_status.setText(phone);
			}
		}else{//带三方绑定
			tv_binding_phone.setText("绑定账号");
			tv_bind_status.setVisibility(View.GONE);
			iv_nav_icon_right.setVisibility(View.VISIBLE);
			rl_binding_phone.setClickable(true);
		}
		
		dlg.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
					finishDialog("yes");
				}
				return false;
			}
		});
		dlg.show();

		setOnclick();

	}

	/**
	 * 点击事件
	 */
	private void setOnclick() {
		// 实名认证
		rl_certification.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				certification();
			}
		});
		// 修改密码
		rl_modify_pwd.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				modifyPwd();
			}
		});
		// 绑定账号
		rl_binding_phone.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				HYThridParams thridParams = GameSDKApplication.getInstance().getThridParams();
				String type = Util.hasThridlogin(thridParams);
				if ("no".equals(type)) {//不带三方绑定，直接绑定手机号
					bindingPhone();//此时直接绑定手机号
				}else{
					bindingAccount();
				}
				
			}
		});
		// 注销
		btn_onlogout.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				callMethod_05();
			}
		});
		// dismiss当前个人中心弹框
		iv_delete.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishDialog("yes");
			}
		});
	}

	/**
	 * 修改密码
	 */
	protected void modifyPwd() {
		if (dlg != null && dlg.isShowing()) {
			dlg.dismiss();
		}
		HYCallBack hyCallBack = new HYCallBack() {

			@Override
			public void onSuccess(Bean bean) {
				Util.sendBroadcast(context);
				Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailed(int code, String failReason) {
				Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
			}
		};
		new ModifyPwdDialog(context, hyCallBack, false, 0).show();
	}

	/**
	 * 实名认证
	 */
	protected void certification() {
		if (dlg != null && dlg.isShowing()) {
			dlg.dismiss();
		}
		HYCallBack hyCallBack = new HYCallBack() {

			@Override
			public void onSuccess(Bean bean) {
//				Toast.makeText(context, "认证成功", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailed(int code, String failReason) {
				Toast.makeText(context, failReason, Toast.LENGTH_SHORT).show();
			}
		};
		new CertificationDialog(context, hyCallBack).show();
	}

	private void callMethod_05()// 注销、切换账号
	{
		finishDialog("yes");
		Intent intent = new Intent();
		intent.setAction(HYConstant.YK_CHANGE_ACCOUNT + GameSDKApplication.getInstance().getAppid());
		intent.putExtra(HYConstant.YK_CHANGE_ACCOUNT, HYConstant.YK_CHANGE_ACCOUNT);
		context.sendBroadcast(intent);
	}

	/**
	 * 绑定账号（带三方）
	 */
	private void bindingAccount() {
		if (dlg != null && dlg.isShowing()) {
			dlg.dismiss();
		}
		HYCallBack callBack = new HYCallBack() {

			@Override
			public void onSuccess(Bean bean) {
				backMethod();
			}

			@Override
			public void onFailed(int code, String failReason) {
			}
		};
		new BindDialog(context, callBack).show();
	}
	
	/**
	 * 绑定手机号
	 */
	protected void bindingPhone() {
		if (dlg != null && dlg.isShowing()) {
			dlg.dismiss();
		}
		BindingPhoneDialog bindingDlg = new BindingPhoneDialog(context, "66");
		bindingDlg.show();
	}


	// "no":不发送广播，"yes":发送广播，"preCenter":发送广播，并传递标记位
	public void finishDialog(String str) {
		if (dlg != null && dlg.isShowing()) {
			dlg.dismiss();
		}

		if ("yes".equals(str)) {
			Util.sendBroadcast(context);
		}
		if (method != null) {
			method.onSuccess(null);
		}
	}

	public void backMethod() {
		if (method != null) {
			method.onSuccess(null);
		}
	}
}
