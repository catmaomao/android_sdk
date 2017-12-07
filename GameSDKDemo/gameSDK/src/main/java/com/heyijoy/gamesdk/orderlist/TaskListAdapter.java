package com.heyijoy.gamesdk.orderlist;

import java.util.ArrayList;

import com.heyijoy.gamesdk.data.TaskBean;
import com.heyijoy.gamesdk.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TaskListAdapter  extends BaseAdapter {

	public Context mContext;
	private ArrayList<TaskBean> taskBeanList;
	private TaskBean taskBean=null;
	public TaskListAdapter(Context context, ArrayList<TaskBean> taskBeanList){
		mContext = context;
		this.taskBeanList = taskBeanList;
		
	}
	@Override
	public int getCount() {
		return taskBeanList.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.dialog_hy_task_item,parent , false);
			holder = new ViewHolder();
			holder.taskTxt = (TextView) convertView.findViewById(R.id.item_task_txt);
			holder.statusImg = (ImageView) convertView.findViewById(R.id.item_status_img);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		taskBean=taskBeanList.get(position);
		holder.taskTxt.setText(taskBean.getDesc());
		if(Integer.parseInt(taskBean.getStatus())==0)
		{
			holder.statusImg.setBackgroundResource(R.drawable.hy_btn_task_unfinished);
		}else
		{
			holder.statusImg.setBackgroundResource(R.drawable.hy_btn_task_finished);
		}
		
		return convertView;
	}
	
	class ViewHolder {
		TextView taskTxt;
		ImageView statusImg;
	}
	
}
