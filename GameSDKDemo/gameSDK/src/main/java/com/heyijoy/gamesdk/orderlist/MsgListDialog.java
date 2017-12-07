package com.heyijoy.gamesdk.orderlist;

import java.util.ArrayList;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.activity.HYRelayActivity;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.MsgBean;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.sql.MsgDBTool;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MsgListDialog {
	public LinearLayout myLayout;
	private Activity activity;
	public AlertDialog dlg;
	private ArrayList<MsgBean> msgBeanList = new ArrayList<MsgBean>();
	private HYCallBack method;
	private ListView listView;
	RelativeLayout noMsgLayout;
	private TextView msgCountTxt, footerTxt;
	private int msgCount, headerCount = 0;
	private MsgListAdapter adapter;
	private LayoutInflater inflater;
	private View headerLayout, footerLayout;

	public MsgListDialog(Activity activity) {
		this.activity = activity;
	}
	
	public MsgListDialog(Activity activity,HYCallBack method) {
		this.activity = activity;
		this.method=method;
	}

	public void showDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(activity,
				R.style.dialog);
		dlg = alert.create();
		// dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
		dlg.show();
		myLayout = (LinearLayout) dlg.getLayoutInflater().inflate(
				R.layout.dialog_hy_msglist, null);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(myLayout);
		msgCountTxt = (TextView) myLayout
				.findViewById(R.id.dialog_hy_msglist_count_txt);

		listView = (ListView) myLayout.findViewById(R.id.auto_list);
		noMsgLayout = (RelativeLayout) myLayout
				.findViewById(R.id.msglist_no_msg_layout);
		adapter = new MsgListAdapter(activity, msgBeanList);
		inflater = (LayoutInflater) activity
				.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
		headerLayout = inflater.inflate(R.layout.dialog_hy_msglist_item, null);
		TextView msgTitleTxt = (TextView) headerLayout
				.findViewById(R.id.item_msg_title_txt);
		TextView msgContentTxt = (TextView) headerLayout
				.findViewById(R.id.item_msg_content_txt);
		TextView rightTxt = (TextView) headerLayout
				.findViewById(R.id.item_msg_bt);
		TextView msgTimeTxt = (TextView) headerLayout
				.findViewById(R.id.item_msg_time);
		ImageView msgPull = (ImageView) headerLayout
				.findViewById(R.id.item_msg_pull_bt);
		msgTitleTxt.setText("未绑定手机号");
		msgContentTxt.setText("为了您的账号安全，建议您绑定手机号，如您的密码忘记或者被盗，可使用手机号验证找回。");
		msgContentTxt.setMaxLines(5);
		rightTxt.setText("立即绑定");
		msgTimeTxt.setVisibility(View.GONE);
		msgPull.setVisibility(View.INVISIBLE);
		footerLayout = inflater
				.inflate(R.layout.dialog_hy_msglist_footer, null);
		footerTxt = (TextView) footerLayout
				.findViewById(R.id.hy_msglist_footer_txt);
		/* 返回键 */
		ImageView back = (ImageView) myLayout.findViewById(R.id.hy_msg_back);
		back.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishDialog();
			}
		});
		headerLayout
				.setOnClickListener(new android.view.View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Util.showBindingDlg(activity, succesBack, "4");
					}
				});
		initView();

		dlg.show();
		setData();
		dlg.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				finishDialog();
			}
		});
	}

	HYCallBack succesBack = new HYCallBack() {
		@Override
		public void onSuccess(Bean bean) {
			if (GameSDKApplication.getInstance().hasBinded()) {
				headerCount = 0;
				listView.removeHeaderView(headerLayout);
			}
			setData();
		}

		@Override
		public void onFailed(int code,String failReason) {
			if (GameSDKApplication.getInstance().hasBinded()) {
				headerCount = 0;
				listView.removeHeaderView(headerLayout);
			}
			setData();
		}
	};

	/** 为PagerView的内容添加1个listview */
	private void initView() {
		if (!GameSDKApplication.getInstance().hasBinded()) {
			listView.addHeaderView(headerLayout);
			headerCount = 1;
		}
		listView.addFooterView(footerLayout);
		listView.setAdapter(adapter);
		MsgDBTool dbTool = new MsgDBTool(activity);
		msgBeanList.addAll(dbTool.getMsgList());
	}

	/** 获得数据 */
	private void setData() {
		MsgDBTool dbTool = new MsgDBTool(activity);
		msgCount = dbTool.getNeverShowMsgCount();
		if (GameSDKApplication.getInstance().hasBinded()) {
			try {
				listView.removeHeaderView(headerLayout);
				headerCount = 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		footerTxt.setText("共" + (msgBeanList.size() + headerCount) + "条消息");
		if (msgCount + headerCount <= 0) {// 未读消息数量
			msgCountTxt.setVisibility(View.GONE);
		} else if ((msgBeanList.size() + headerCount) >= 100) {
			msgCountTxt.setText("99");
		} else {
			msgCountTxt.setText(msgCount + headerCount + "");
		}

		if ((msgBeanList.size() + headerCount) <= 0) {// 列表中所有消息数量
			noMsgLayout.setVisibility(View.VISIBLE);
			footerTxt.setVisibility(View.GONE);
		}
		adapter.notifyDataSetChanged();
	}

	public void closeDialog() {
		if (dlg != null && dlg.isShowing())
			dlg.dismiss();
	}

	public void finishDialog()
	{
		if(dlg!=null&&dlg.isShowing())
		{
			dlg.dismiss();
		}
		if(method!=null)
		{
			method.onSuccess(null);
		}
	}
	
	public class MsgListAdapter extends BaseAdapter {

		public Context mContext;
		private ArrayList<MsgBean> msgBeanList;

		public MsgListAdapter(Context context, ArrayList<MsgBean> msgBeanList) {
			mContext = context;
			this.msgBeanList = msgBeanList;
		}

		@Override
		public int getCount() {
			return msgBeanList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.dialog_hy_msglist_item, parent, false);
				holder = new ViewHolder();
				holder.msgTitleTxt = (TextView) convertView
						.findViewById(R.id.item_msg_title_txt);
				holder.msgContentTxt = (TextView) convertView
						.findViewById(R.id.item_msg_content_txt);
				holder.msgTimeTxt = (TextView) convertView
						.findViewById(R.id.item_msg_time);
				// holder.msgImg = (ImageView)
				// convertView.findViewById(R.id.item_msg_bt);
				holder.msgPull = (ImageView) convertView
						.findViewById(R.id.item_msg_pull_bt);
				holder.rightBt = (RelativeLayout) convertView
						.findViewById(R.id.relativeLayout2);
				holder.rightTxt = (TextView) convertView
						.findViewById(R.id.item_msg_bt);
				holder.itemRelative = (RelativeLayout) convertView
						.findViewById(R.id.hy_msg_detail_item_layout);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.msgTitleTxt
					.setText(msgBeanList.get(position).getMainTitle());
			holder.msgContentTxt
					.setText(msgBeanList.get(position).getContent());
			if (msgBeanList.get(position).getButtTitle() != null
					&& msgBeanList.get(position).getButtTitle().length() > 0) {
				holder.rightBt.setVisibility(View.VISIBLE);
				holder.rightTxt.setText(msgBeanList.get(position)
						.getButtTitle());
			} else {
				holder.rightBt.setVisibility(View.GONE);
			}

			// holder.msgContentTxt.setText("一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十");
			// SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
			// holder.msgTimeTxt.setText(format.format(msgBeanList.get(position).getSendTime()));
			holder.msgTimeTxt.setText(timeFormat(msgBeanList.get(position)
					.getSendTime()));

			if (msgBeanList.get(position).getContent().length() < 35) {
				holder.msgPull.setVisibility(View.INVISIBLE);
			} else {
				holder.msgPull.setVisibility(View.VISIBLE);
			}
			if (msgBeanList.get(position).getIsDisplayed()) {
				holder.msgTitleTxt.setTextColor(mContext.getResources()
						.getColor(R.color.hy_light_gray));
				holder.msgContentTxt.setTextColor(mContext.getResources()
						.getColor(R.color.hy_light_gray));
			} else {
				holder.msgTitleTxt.setTextColor(mContext.getResources()
						.getColor(R.color.hy_dark_black));
				holder.msgContentTxt.setTextColor(mContext.getResources()
						.getColor(R.color.hy_content_gray));
			}
			holder.rightBt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Intent intent = new Intent(activity,
								HYRelayActivity.class);
						intent.putExtra("from", "HYNotifyBar");
						intent.putExtra("msgBean", msgBeanList.get(position));
						intent.putExtra("clickFrom", 2);
						activity.startActivity(intent);
					} catch (Exception e) {
						Toast.makeText(mContext, "请下载最新的合乐智趣视频客户端！", 2000).show();
						e.printStackTrace();
					}
					if (!msgBeanList.get(position).getIsDisplayed()) {
						msgBeanList.get(position).setIsDisplayed(true);
						MsgDBTool dbTool = new MsgDBTool(activity);
						dbTool.updataIsDisplayed(msgBeanList.get(position)
								.getMsgId());// 设置为已读
					}
					setData();
					notifyDataSetChanged();
				}
			});
			holder.itemRelative.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (msgBeanList.get(position).getContent().length() >= 35) {// 大于等于35个字符才会展开
						if (msgBeanList.get(position).getIsOpen()) {
							msgBeanList.get(position).setIsOpen(false);
							holder.msgContentTxt.setMaxLines(2);
							holder.msgPull
									.setImageResource(R.drawable.hy_pay_detail_item_arrow_down);
						} else {
							msgBeanList.get(position).setIsOpen(true);
							holder.msgContentTxt.setMaxLines(20);
							holder.msgPull
									.setImageResource(R.drawable.hy_pay_detail_item_arrow_up);
						}
					}
					if (!msgBeanList.get(position).getIsDisplayed()) {
						msgBeanList.get(position).setIsDisplayed(true);
						MsgDBTool dbTool = new MsgDBTool(activity);
						dbTool.updataIsDisplayed(msgBeanList.get(position)
								.getMsgId());// 设置为已读
					}
					setData();
					notifyDataSetChanged();
				}
			});
			return convertView;
		}

		class ViewHolder {
			TextView msgTitleTxt;
			TextView msgContentTxt;
			TextView msgTimeTxt;
			TextView rightTxt;
			// ImageView msgImg;
			ImageView msgPull;
			RelativeLayout rightBt;
			RelativeLayout itemRelative;
		}

		private String timeFormat(String oldTime) {
			String time = "";
			if (oldTime != null && oldTime.length() > 10) {
				time = oldTime.substring(0, 10).replaceFirst("-", "年")
						.replace("-", "月")
						+ "日";
			}
			return time;
		}

	}

}