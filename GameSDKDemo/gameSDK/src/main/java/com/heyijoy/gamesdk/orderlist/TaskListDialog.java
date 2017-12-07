package com.heyijoy.gamesdk.orderlist;

import java.util.ArrayList;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.TaskBean;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.widget.HYDialog;
import com.heyijoy.gamesdk.widget.HYProgressDlg;
import com.heyijoy.gamesdk.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

//积分任务列表对话框
public class TaskListDialog extends Dialog{ 
	public LinearLayout myLayout ; 
	private Context context ;
	public HYDialog dlg;
	private HYProgressDlg proDlg;
	private HYCallBack method;
	private ArrayList<TaskBean> getvipList = new ArrayList<TaskBean>();
	private RelativeLayout noMsgLayout;
	private ListView autolist ;
	private TaskListAdapter adapter;
	public TaskListDialog(Context context) {
		super(context);
		this.context = context;
	}
	public TaskListDialog(Context context,HYCallBack method) {
		super(context);
		this.context = context;
		this.method=method;
	}
	
	public void showDialog(){
		myLayout = (LinearLayout)getLayoutInflater().inflate(
		        R.layout.dialog_hy_tasklist, null);
		dlg = new HYDialog(context, R.style.pay_dialog);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(myLayout);
		
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(myLayout);
		/*返回键*/
		ImageView back = (ImageView)myLayout.findViewById(R.id.hy_vip_back);
		
		autolist = (ListView)myLayout.findViewById(R.id.auto_list);
		noMsgLayout = (RelativeLayout) myLayout
				.findViewById(R.id.msglist_no_msg_layout);
		back.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishDialog();
			}
		});
		dlg.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
					finishDialog();
				}
				return false;
			}
		});
		proDlg = HYProgressDlg.show(context, "正在加载…",false);
		HYCallBack callBack = new HYCallBack() {
			@Override
			public void onSuccess(Bean bean) {
				getvipList =  GameSDKApplication.getInstance().getTaskBeanList();
				adapter = new TaskListAdapter(context, getvipList);
				autolist.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				dlg.show();
				proDlg.dismiss();
			}

			@Override
			public void onFailed(int code,String failReason) {
				noMsgLayout.setVisibility(View.VISIBLE);
				dlg.show();
				proDlg.dismiss();
			}
		};
		Util.getTaskListByPre(callBack);
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

}