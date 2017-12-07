package com.heyijoy.gamesdk.orderlist;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.heyijoy.gamesdk.R;
import com.heyijoy.gamesdk.act.HYCallBackStr;
import com.heyijoy.gamesdk.announcement.AnnouncementListDialog;
import com.heyijoy.gamesdk.data.BenefitBean;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.util.MyParse;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.widget.HYDialog;

import java.util.HashMap;

@SuppressWarnings("deprecation")
public class BenefitDialog extends Dialog {
	private LinearLayout myLayout;
	private Activity activity;
	private HYDialog dlg;
	private RelativeLayout codeLayout;
	private LinearLayout vipLayout;
	private Boolean vipFlag = false;
	private Boolean codeFlag = false;
	private TextView codeTxt, titleTxt, subTitleTxt, contentTxt, vipText;
	private ImageView copyImg, backImg;

	public BenefitDialog(Activity activity) {
		super(activity);
		this.activity = activity;
	}

	@Override
	public void show() {
		initDialog();
	}

	private void initDialog() {
		getData();
	}

	private void setCode(final BenefitBean bean) {
		codeLayout = (RelativeLayout) myLayout
				.findViewById(R.id.dialog_hy_benefit_code_layout);
		codeTxt = (TextView) myLayout
				.findViewById(R.id.dialog_hy_benefit_code_txt);
		subTitleTxt = (TextView) myLayout
				.findViewById(R.id.dialog_hy_benefit_sub_title);
		contentTxt = (TextView) myLayout
				.findViewById(R.id.dialog_hy_benefit_content);
		copyImg = (ImageView) myLayout
				.findViewById(R.id.dialog_hy_benefit_copy_img);

		if (bean.getCode() == null || bean.getCode().length() < 1) {
			codeLayout.setVisibility(View.GONE);
		} else {
			codeTxt.setText("激活码：" + bean.getCode());
		}
		if (bean.getSubTitle().equals("")) {
			subTitleTxt.setVisibility(View.GONE);
		} else {
			subTitleTxt.setText(bean.getSubTitle());
		}

		contentTxt.setText(bean.getContent());
		copyImg.setOnClickListener(new android.view.View.OnClickListener() {
			@SuppressWarnings("static-access")
			@Override
			public void onClick(View arg0) {
				ClipboardManager cmb = (ClipboardManager) activity
						.getSystemService(activity.CLIPBOARD_SERVICE);
				cmb.setText(bean.getCode());
				Toast.makeText(activity, "复制成功！", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void setVip(BenefitBean bean) {
		String vipCont = bean.getContent();
		if (vipCont != null && !"".equals(vipCont)) {
			vipText = (TextView) myLayout.findViewById(R.id.dialog_yt_tv);
			vipCont = Util.toNewStr(vipCont);
			vipText.setText(vipCont);
		}
	}

	public void setMTitle() {
		titleTxt = (TextView) myLayout
				.findViewById(R.id.dialog_hy_benefit_title);
		titleTxt.setText("VIP 福利");
		backImg = (ImageView) myLayout.findViewById(R.id.hy_benefit_back);
		backImg.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.dontClick(v);
				finishDialog();
			}
		});
	}

	private void setDialg() {
		dlg = new HYDialog(activity, R.style.pay_dialog);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(myLayout);
		dlg.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					finishDialog();
				}
				return false;
			}
		});
		dlg.show();
	}

	public void finishDialog() {
		if (dlg != null && dlg.isShowing()) {
			dlg.dismiss();
		}
		if (!Util.isFastDoubleClick()) {
			AnnouncementListDialog announceDlg = new AnnouncementListDialog(
					activity);
			announceDlg.show();
		}
	}

	/**
	 * 获得数据
	 */
	private void getData() {
		HttpApi.requestBenefit(new HYCallBackStr() {
			@Override
			public void callback(String jsonData) {
				if (jsonData == null) {
					starAnnouncement();
					return;
				}
				HashMap<String, BenefitBean> paramsMap = MyParse
						.parseBenefit(jsonData);
				if(paramsMap!=null)
				{
					for (int i = 0; i < paramsMap.size(); i++) {
						BenefitBean bean = paramsMap.get(i + "");
						if (i == 0) {
							if (bean != null) {
								vipFlag = true;
							}
						} else if (i == 1) {
							if (bean != null) {
								codeFlag = true;
							}
						}
					}

					if (vipFlag && codeFlag) {
						setDialogForAll(paramsMap);
					} else if (vipFlag) {
						setDialogForOne(paramsMap.get("0"));
					} else if (codeFlag) {
						setDialogForSec(paramsMap.get("1"));
					} else {
						starAnnouncement();
					}
				}else
				{
					starAnnouncement();
				}
			}
		});
	}

	private void starAnnouncement() {
		AnnouncementListDialog announceDlg = new AnnouncementListDialog(
				activity);
		announceDlg.show();

	}

	protected void setDialogForSec(BenefitBean benefitBean) {
		myLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.dialog_hy_benefit2, null);
		setMTitle();
		setCode(benefitBean);
		setDialg();
	}

	protected void setDialogForOne(BenefitBean benefitBean) {
		myLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.dialog_hy_benefit_only_vip, null);
		setMTitle();
		setVip(benefitBean);
		setDialg();
	}

	protected void setDialogForAll(HashMap<String, BenefitBean> paramsMap) {
		myLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.dialog_hy_benefit, null);
		setMTitle();
		setVip(paramsMap.get("0"));
		setCode(paramsMap.get("1"));
		setDialg();
	}
}
