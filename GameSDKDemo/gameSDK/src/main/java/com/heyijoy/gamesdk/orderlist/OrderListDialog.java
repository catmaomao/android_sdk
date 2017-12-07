package com.heyijoy.gamesdk.orderlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.OrderBean;
import com.heyijoy.gamesdk.data.PayBean;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.orderlist.AutoListView.OnLoadListener;
import com.heyijoy.gamesdk.orderlist.AutoListView.OnRefreshListener;
import com.heyijoy.gamesdk.util.MyParse;
import com.heyijoy.gamesdk.widget.HYDialog;
import com.heyijoy.gamesdk.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

/**
 * 交易明细  支付和充值
 * @author shaohuma
 *
 */
public class OrderListDialog extends Dialog implements OnRefreshListener,OnLoadListener {
	private final String ONFRESH = "onFresh";
	private final String ONLOAD = "onLoad";
	private LinearLayout myLayout ; 
	private Activity activity ;
	private HYDialog dlg;
	private static final int PAGER_NUM = 2;
	private int overPagerNum = 0;
	private HYCallBack method;
	TabHost mTabHost;
	ViewPager mViewPager;
	
	ArrayList<OrderBean> orderBeanList = new ArrayList<OrderBean>();
	ArrayList<OrderBean> orderBeanListPay = new ArrayList<OrderBean>();
	ArrayList<OrderBean> orderBeanListRecharge = new ArrayList<OrderBean>();
	
	AutoListView listPay ;
	AutoListView listRecharge ;
	
	OrderListAdapter adapterPay;
	OrderListAdapter adapterRecharge;
	
	private int pageNumPay = 0 , pageNumRecharge = 0 ;
	
	public OrderListDialog(Activity activity) {
		super(activity);
		this.activity = activity;
	}
	public OrderListDialog(Activity activity,HYCallBack method) {
		super(activity);
		this.activity = activity;
		this.method=method;
	}
	@Override
	public void show() {
		showDialog();
	}
	
	private void showDialog(){
		myLayout = (LinearLayout) getLayoutInflater().inflate(
		        R.layout.dialog_hy_pay_orderlist, null);
		dlg = new HYDialog(activity, R.style.pay_dialog);
		dlg.setCanceledOnTouchOutside(false);
		dlg.setContentView(myLayout);
		/*返回键*/
		ImageView back = (ImageView)myLayout.findViewById(R.id.hy_pay_icon_back);
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
		initTabHost();
		initPagerView();
		setListener();
		dlg.show();
		getData(overPagerNum , ONFRESH);		
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
	
	/**为TabHost添加3个标签*/
	private void initTabHost() {
		LayoutInflater lf = getLayoutInflater().from(activity);
		
		View view1 = lf.inflate(R.layout.hy_pay_tabmini, null);
		TextView text1 = (TextView) view1.findViewById(R.id.hy_pay_tab_label);
		text1.setText("支付");
		View view2 = lf.inflate(R.layout.hy_pay_tabmini, null);
		TextView text2 = (TextView) view2.findViewById(R.id.hy_pay_tab_label);
		text2.setText("充值");
		
		mTabHost = (TabHost)myLayout.findViewById(R.id.tabhost);
		mTabHost.setup();
		mTabHost.addTab(mTabHost.newTabSpec("A")
				.setIndicator(view1)
				.setContent(android.R.id.tabcontent));  
	  
		mTabHost.addTab(mTabHost.newTabSpec("B")
				.setIndicator(view2)
				.setContent(android.R.id.tabcontent));  
	  
	}
	
	
	/**为PagerView的内容添加3个listview*/
	private void initPagerView() {
		
		mViewPager = (ViewPager)myLayout.findViewById(R.id.hy_pay_orderlist_pager);
		LayoutInflater lf = getLayoutInflater().from(activity);
		View view1 = lf.inflate(R.layout.dialog_hy_pay_orderlist_page, null);
		View view2 = lf.inflate(R.layout.dialog_hy_pay_orderlist_page, null);
		List<View> viewlist = new ArrayList<View>();
		viewlist.add(view1);
		viewlist.add(view2);
		OrderListPagerAdapter myPagerAdapter = new OrderListPagerAdapter(viewlist);
		mViewPager.setAdapter(myPagerAdapter);
		mViewPager.setOffscreenPageLimit(PAGER_NUM);
		
		listPay = (AutoListView)view1.findViewById(R.id.pager_list);
		listRecharge = (AutoListView)view2.findViewById(R.id.pager_list);
		
		
		adapterPay = new OrderListAdapter(activity, orderBeanListPay);
		adapterRecharge = new OrderListAdapter(activity, orderBeanListRecharge);
		
		listPay.setAdapter(adapterPay);
		listRecharge.setAdapter(adapterRecharge);
		
		
		listPay.setOnRefreshListener(this);
		listRecharge.setOnRefreshListener(this);
		
		listPay.setOnLoadListener(this);
		listRecharge.setOnLoadListener(this);
	}
	/** 为TabHost添加监听器，为ViewPager添加监听器*/
	private void setListener() {
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String arg0) {
				int currentIndex = mTabHost.getCurrentTab();
				mViewPager.setCurrentItem(currentIndex);
			}
		});
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				overPagerNum = position;
				mTabHost.setCurrentTab(overPagerNum);
				switch (overPagerNum) {
				case 0:
					if(orderBeanListPay.size() == 0){
						getData(overPagerNum , ONFRESH);
					}
					break;
				case 1:
					if(orderBeanListRecharge.size() == 0){
						getData(overPagerNum , ONFRESH);
					}
					break;
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
	}

	/**获得数据*/
	private void getData(final int localPagerNum , final String action){
		synchronized(this){
			
			String url = HYConstant.URL_GET_DETAIL_PAY;
			int pageNum = 0;
			switch(localPagerNum){
			case 0:
				url = HYConstant.URL_GET_DETAIL_PAY;
				pageNum = (action == ONFRESH) ?  0 : pageNumPay;
				break;
			case 1:
				url = HYConstant.URL_GET_DETAIL_RECHARGE;
				pageNum = (action == ONFRESH) ?  0 : pageNumRecharge;
				break;
			}
			//历史订单后的回调
			HYCallBack callBack = new HYCallBack() {
				@Override
				public void onSuccess(Bean bean) {//正确获得数据
					PayBean payBean = (PayBean)bean;
					String jsonData = payBean.getParams();
					HashMap<String, Object> orderBeanMap = MyParse.parseOrderList(jsonData);
					int total = (Integer) orderBeanMap.get("total");
					orderBeanList = (ArrayList<OrderBean>) orderBeanMap.get("orderBeanList");
					
					switch(localPagerNum){
					case 0:
						if(action == ONFRESH){
							pageNumPay = 1;
							listPay.onRefreshComplete();
							orderBeanListPay.clear();
							orderBeanListPay.addAll(orderBeanList);
						}else if(action == ONLOAD){
							pageNumPay++;
							listPay.onLoadComplete();
							orderBeanListPay.addAll(orderBeanList);
						}
						listPay.setResultSize(total);
						adapterPay.notifyDataSetChanged();
						break;
					case 1:
						if(action == ONFRESH){
							pageNumRecharge = 1;
							listRecharge.onRefreshComplete();
							orderBeanListRecharge.clear();
							orderBeanListRecharge.addAll(orderBeanList);
						}else if(action == ONLOAD){
							pageNumRecharge++;
							listRecharge.onLoadComplete();
							orderBeanListRecharge.addAll(orderBeanList);
						}
						listRecharge.setResultSize(total);
						adapterRecharge.notifyDataSetChanged();
						break;
					}
				}
				@Override
				public void onFailed(int code,String failReason) {//数据获取错误
					switch(localPagerNum){
					case 0:
						if(action == ONFRESH){
							listPay.onRefreshComplete();
						}else if(action == ONLOAD){
							listPay.onLoadComplete();
						}
						listPay.setResultSize(-1);
						adapterPay.notifyDataSetChanged();
						break;
					case 1:
						if(action == ONFRESH){
							listRecharge.onRefreshComplete();
						}else if(action == ONLOAD){
							listRecharge.onLoadComplete();
						}
						listRecharge.setResultSize(-1);
						adapterRecharge.notifyDataSetChanged();
						break;
					}
				}
				
			};
			HttpApi.getInstance().getPayDetail(callBack , url , ""+ pageNum*20 , ""+ (pageNum+1)*20);
		}
	}

	@Override
	public void onLoad() {
		getData(overPagerNum , ONLOAD);
		
	}
	@Override
	public void onRefresh() {
		getData(overPagerNum , ONFRESH);
	}
}
