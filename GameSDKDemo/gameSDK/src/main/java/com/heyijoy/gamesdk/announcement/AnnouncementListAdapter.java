package com.heyijoy.gamesdk.announcement;


import com.heyijoy.gamesdk.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class AnnouncementListAdapter extends BaseAdapter {
	public Context mContext;
	private AnnouncementCache announcementCache;
	public AnnouncementListAdapter(Context context,AnnouncementCache announcementCache){
		mContext = context;
		this.announcementCache = announcementCache;
	}
	@Override
	public int getCount() {
		return (announcementCache.getAnnouncements() == null)?0:announcementCache.getAnnouncements().size()+1;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	
	public Announcement getAnnouncement(int position){
		return announcementCache.getAnnouncements().get(position-1);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		/* ViewHolder holder = null;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.dialog_hy_announcement_item,parent , false);
				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.item_title_txt);
				holder.content = (TextView) convertView.findViewById(R.id.item_content_txt);
				holder.url = (TextView) convertView.findViewById(R.id.item_url_txt);
				holder.line=(TextView)convertView.findViewById(R.id.item_img_line);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.title.setText(announcementCache.getAnnouncements().get(position).getTitle());
			TextPaint tp = holder.title.getPaint();
			tp.setFakeBoldText(true);
			String content = "";
			if(announcementCache.getAnnouncements().get(position).getContent().length()>87){
				content = announcementCache.getAnnouncements().get(position).getContent().substring(0, 87)+"...";
			}else{
				content = announcementCache.getAnnouncements().get(position).getContent();
			}
			holder.content.setText(content);
			
			String urlContent = announcementCache.getAnnouncements().get(position).getUrlContent();
			if(urlContent!=null&&!"".equals(urlContent)){
				holder.url.setText(urlContent);
				holder.url.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
				AnnouncementClickListener linkOnclickListener = new AnnouncementClickListener(announcementCache.getAnnouncements().get(position));
				holder.url.setOnClickListener(linkOnclickListener);
			}
			if(position<1){
				holder.line.setVisibility(View.GONE);
			}else{
				holder.line.setVisibility(View.VISIBLE);
			}
			return convertView;*/
		
		if(position == 0){
		       convertView =  LayoutInflater.from(mContext).inflate(R.layout.dialog_hy_announcement_bannar, null);
		       LinearLayout bannar = (LinearLayout)convertView.findViewById(R.id.hy_announcement_baner);
				if(AnnouncementCache.getInstance().getBannar()!=null){
					BitmapDrawable bd=new BitmapDrawable(AnnouncementCache.getInstance().getBannar());
					bannar.setBackgroundDrawable(bd);
					AnnouncementClickListener bannarOnclickListener = new AnnouncementClickListener(AnnouncementCache.getInstance(),true);
					bannar.setOnClickListener(bannarOnclickListener);
				}else{
					bannar.setVisibility(View.GONE);
				}
		        return convertView;
		    }else{
		    	 ViewHolder holder = null;
				if(convertView == null || position >0){
					convertView = LayoutInflater.from(mContext).inflate(R.layout.dialog_hy_announcement_item,parent , false);
					holder = new ViewHolder();
					holder.title = (TextView) convertView.findViewById(R.id.item_title_txt);
					holder.content = (TextView) convertView.findViewById(R.id.item_content_txt);
					holder.url = (TextView) convertView.findViewById(R.id.item_url_txt);
					holder.line=(TextView)convertView.findViewById(R.id.item_img_line);
					convertView.setTag(holder);
				}else{
					holder = (ViewHolder) convertView.getTag();
				}
				String title = announcementCache.getAnnouncements().get(position-1).getTitle();
				if(title!=null&&!"".equals(title)){
					holder.title.setVisibility(View.VISIBLE);
					holder.title.setText(title);
					TextPaint tp = holder.title.getPaint();
					tp.setFakeBoldText(true);
				}else{
					holder.title.setVisibility(View.GONE);
				}
				String content = "";
			/*	if(announcementCache.getAnnouncements().get(position-1).getContent().length()>87){
					content = announcementCache.getAnnouncements().get(position-1).getContent().substring(0, 87)+"...";
				}else{
					content = announcementCache.getAnnouncements().get(position-1).getContent();
				}*/
				content = announcementCache.getAnnouncements().get(position-1).getContent();
				content.trim();
				holder.content.setText(content);
				
				String urlContent = announcementCache.getAnnouncements().get(position-1).getUrlContent();
				if(urlContent!=null&&!"".equals(urlContent)){
					holder.url.setVisibility(View.VISIBLE);
					holder.url.setText(urlContent);
					holder.url.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
					AnnouncementClickListener linkOnclickListener = new AnnouncementClickListener(announcementCache.getAnnouncements().get(position-1),true);
					holder.url.setOnClickListener(linkOnclickListener);
				}else{
					holder.url.setVisibility(View.GONE);
				}
				
				if(position<2){
					holder.line.setVisibility(View.GONE);
				}else{
					holder.line.setVisibility(View.VISIBLE);
				}
				return convertView;
		    }
		
		
	}
	class ViewHolder {
		TextView title;
		TextView content;
		TextView url;
		TextView line;
	}
	
}
