package com.heyijoy.gamesdk.orderlist;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class OrderListPagerAdapter extends PagerAdapter {
	List<View> mViewList;
	public OrderListPagerAdapter(List<View> viewlist){
		mViewList = viewlist;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mViewList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mViewList.get(position));
	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return super.getItemPosition(object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(mViewList.get(position));
		return mViewList.get(position);
	}

	public List<View> getViewList() {
		return mViewList;
	}


}
