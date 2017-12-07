package com.heyijoy.gamesdk.announcement;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.util.Logger;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.widget.HYDialog;
import com.heyijoy.gamesdk.R;

public class AnnouncementListDialog extends Dialog {
	private RelativeLayout myLayout ; 
	private Activity activity ;
	private HYDialog dlg;
	
	ArrayList<Announcement> announcementBeanList = new ArrayList<Announcement>();
	AnnouncementListAdapter adapterAll;
	List<Announcement> announcementList = new ArrayList<Announcement>();
	
	public AnnouncementListDialog(Activity activity) {
		super(activity);
		this.activity = activity;
	}
	@Override
	public void show() {
		if(getData()){
			showDialog();
		}
	}
	
	private void showDialog(){
		
		adapterAll = new AnnouncementListAdapter(activity, AnnouncementCache.getInstance());
//		if(AnnouncementCache.getInstance().getBannar()==null&&adapterAll.getCount()==2){

				myLayout = (RelativeLayout) getLayoutInflater().inflate(
				        R.layout.dialog_hy_announcement_dlg_new, null);
				TextView title = (TextView) myLayout.findViewById(R.id.item_title_txt);
				TextView content = (TextView) myLayout.findViewById(R.id.item_content_txt);
				TextView url = (TextView) myLayout.findViewById(R.id.item_url_txt);
				
				String titleStr = adapterAll.getAnnouncement(1).getTitle();
				String contentStr = adapterAll.getAnnouncement(1).getContent();
//				String urlStr = adapterAll.getAnnouncement(1).getUrlContent();
				if(titleStr==null||"".equals(titleStr)){
					title.setVisibility(View.INVISIBLE);
				}else{
					title.setVisibility(View.VISIBLE);
					title.setText(titleStr);
				}
				if(contentStr==null||"".equals(contentStr)){
					content.setVisibility(View.GONE);
				}else{
					content.setVisibility(View.VISIBLE);
					content.setText(Util.toStandardString(contentStr));
					content.setMovementMethod(ScrollingMovementMethod.getInstance()) ;  
				}
//				if(urlStr==null||"".equals(urlStr)){
//					url.setVisibility(View.GONE);
//				}else{
					url.setVisibility(View.VISIBLE);
//					url.setText(urlStr);
//					url.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
					if("".equals(adapterAll.getAnnouncement(1).getUrl()))
					{
						url.setBackgroundResource(R.drawable.hy_announcement_close_button);
						url.setClickable(true);
						url.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								finish();
							}
						});
					}else
					{
						AnnouncementClickListener linkOnclickListener = new AnnouncementClickListener(adapterAll.getAnnouncement(1),true);
						url.setOnClickListener(linkOnclickListener);
					}
//				}
//		}else{ 
//			myLayout = (LinearLayout) activity.getLayoutInflater().inflate(
//			        R.layout.dialog_hy_announcement_dlg, null);
//			ListView list = (ListView) myLayout.findViewById(R.id.hy_announcement_list);
//			list.setAdapter(adapterAll);
//			list.setDivider(null);
//		}
		
		dlg = new HYDialog(activity, R.style.pre_dialog);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(myLayout);
		dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				finish();
			}
		});
//		TextView tv_title_text = (TextView)myLayout.findViewById(R.id.tv_title_text);
//		tv_title_text.setText(HYConstant.TITLE_ANNOUNCEMENT);
//		tv_title_text.setText(AnnouncementCache.getInstance().getTitle());
		Button btn_back = (Button)myLayout.findViewById(R.id.back);
		btn_back.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					finish();
				}
			});
		
	/*	LinearLayout bannar = (LinearLayout)myLayout.findViewById(R.id.hy_announcement_baner);
		if(AnnouncementCache.getInstance().getBannar()!=null){
			BitmapDrawable bd=new BitmapDrawable(AnnouncementCache.getInstance().getBannar());
			bannar.setBackgroundDrawable(bd);
			AnnouncementClickListener bannarOnclickListener = new AnnouncementClickListener(AnnouncementCache.getInstance());
			bannar.setOnClickListener(bannarOnclickListener);
		}else{
			bannar.setVisibility(View.GONE);
		}*/
		dlg.show();
		
	}

	/**
	 * 获得数据
	 */
	public boolean getData(){ 
		announcementList = AnnouncementCache.getInstance().getAnnouncements();
		if(announcementList!=null && announcementList.size()>0){
			return true;
		}else{
			return false;
		}
	}
	private void finish(){
		dlg.dismiss();
		AnnouncementCache.getInstance().destroy();
	}
	
}
