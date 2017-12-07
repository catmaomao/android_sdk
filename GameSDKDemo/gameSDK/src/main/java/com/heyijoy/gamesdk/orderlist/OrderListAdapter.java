package com.heyijoy.gamesdk.orderlist;

import java.util.ArrayList;

import com.heyijoy.gamesdk.data.OrderBean;
import com.heyijoy.gamesdk.lib.HYConstant;
import com.heyijoy.gamesdk.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OrderListAdapter extends BaseAdapter {

	public Context mContext;
	private ArrayList<OrderBean> orderBeanList;
	public OrderListAdapter(Context context, ArrayList<OrderBean> orderBeanList){
		mContext = context;
		this.orderBeanList = orderBeanList;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return orderBeanList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.dialog_hy_pay_detail_item,parent , false);
			holder = new ViewHolder();
			holder.nameTxt = (TextView) convertView.findViewById(R.id.item_goodsname_txt);
			holder.isSuccessTxt = (TextView) convertView.findViewById(R.id.item_isSuccess_txt);
			holder.priceTxt = (TextView) convertView.findViewById(R.id.item_price_txt);
			holder.timeTxt = (TextView) convertView.findViewById(R.id.item_time_txt);
			holder.orderidTxt = (TextView) convertView.findViewById(R.id.item_orderid_txt);
			holder.tipsTxt = (TextView) convertView.findViewById(R.id.item_tips_txt);
			holder.hasRebateTxt = (TextView) convertView.findViewById(R.id.item_rebate_already_txt);
			holder.rebateAmoutTxt = (TextView) convertView.findViewById(R.id.item_rebate_amount_txt);
			holder.bonusTxt = (TextView) convertView.findViewById(R.id.item_rebate_bonus_txt);
			holder.vip1Txt = (TextView) convertView.findViewById(R.id.item_rebate_convert_txt);
			holder.vip2Txt = (TextView) convertView.findViewById(R.id.item_rebate_convert2_txt);
			holder.arrow = (ImageView) convertView.findViewById(R.id.item_arrow);
			holder.mainLayout = (RelativeLayout) convertView.findViewById(R.id.hy_pay_detail_item_layout);
			holder.moreLayout = (RelativeLayout) convertView.findViewById(R.id.item_layout2);
			holder.row2Layout = (LinearLayout) convertView.findViewById(R.id.item_rebate_row2);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.nameTxt.setText(orderBeanList.get(position).getGoodName());
		holder.isSuccessTxt.setText(orderBeanList.get(position).getStatus().equals("2") ? "成功" : "未成功");
		String channelCode = orderBeanList.get(position).getPayChannel();
		String payChannel  = parseChannel(channelCode);
		float price = Float.valueOf(orderBeanList.get(position).getPrice())/100;
		holder.priceTxt.setText(payChannel+price+"元");
		holder.timeTxt.setText(orderBeanList.get(position).getTime());
		holder.orderidTxt.setText("订单号："+orderBeanList.get(position).getOrderID());
		holder.tipsTxt.setText(orderBeanList.get(position).getStatus().equals("2") ? "说明：支付成功" : "说明：支付未成功，如有疑问请联系客服.");
		//是否有任何返利
		if(orderBeanList.get(position).getRebate().equals("0")&&orderBeanList.get(position).getBonus().equals("0")&&orderBeanList.get(position).getVip().equals("")){
			holder.hasRebateTxt.setVisibility(View.GONE);
		}else{
			holder.hasRebateTxt.setVisibility(View.VISIBLE);
			holder.hasRebateTxt.setText("已返利");
		}
		//三项中只要有一项没有
		if(orderBeanList.get(position).getRebate().equals("0")||orderBeanList.get(position).getBonus().equals("0")||orderBeanList.get(position).getVip().equals("")){
			holder.row2Layout.setVisibility(View.GONE);
			holder.vipTxt = holder.vip1Txt;
		}else{
			holder.row2Layout.setVisibility(View.VISIBLE);
			holder.vip1Txt.setVisibility(View.GONE);
			holder.vipTxt = holder.vip2Txt;
		}
		/*返利优豆数量*/
		if(orderBeanList.get(position).getRebate().equals("0")){
			holder.rebateAmoutTxt.setVisibility(View.GONE);
		}else{
			holder.rebateAmoutTxt.setVisibility(View.VISIBLE);
			holder.rebateAmoutTxt.setText("返利"+(Float.parseFloat(orderBeanList.get(position).getRebate())/100)+"优豆");
		}
		/*返合乐智趣积分*/
		if(orderBeanList.get(position).getBonus().equals("0")){
			holder.bonusTxt.setVisibility(View.GONE);
		}else{
			holder.bonusTxt.setVisibility(View.VISIBLE);
			holder.bonusTxt.setText("返利"+(Float.parseFloat(orderBeanList.get(position).getBonus()))+"合乐智趣积分");
		}
		/*返vip天数*/
		if(orderBeanList.get(position).getVip().equals("")){
			holder.vipTxt.setVisibility(View.GONE);
		}else{
			holder.vipTxt.setVisibility(View.VISIBLE);
			holder.vipTxt.setText(orderBeanList.get(position).getVip());
		}
		if(!orderBeanList.get(position).getIsShow()){
			holder.arrow.setImageResource(R.drawable.hy_pay_detail_item_arrow_down);
			holder.moreLayout.setVisibility(View.GONE);
		}else{
			holder.arrow.setImageResource(R.drawable.hy_pay_detail_item_arrow_up);
			holder.moreLayout.setVisibility(View.VISIBLE);
		}
		holder.mainLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(orderBeanList.get(position).getIsShow()){
					orderBeanList.get(position).setIsShow(false);
				}else{
					orderBeanList.get(position).setIsShow(true);
				}
				notifyDataSetChanged();
			}
		});
		
		return convertView;
	}
	class ViewHolder {
		TextView nameTxt;
		TextView isSuccessTxt;
		TextView priceTxt;
		TextView timeTxt;
		TextView orderidTxt;
		TextView tipsTxt;
		TextView hasRebateTxt;
		TextView rebateAmoutTxt;
		TextView bonusTxt;
		TextView vipTxt;
		TextView vip1Txt;
		TextView vip2Txt;
		ImageView arrow;
		RelativeLayout mainLayout;
		RelativeLayout moreLayout;
		LinearLayout row2Layout;
	}
	
	private String parseChannel(String channelCode){
		String payChannel = "其他支付";
		if(channelCode.equals(HYConstant.CHANNEL_ALIPAY)){
			payChannel = "支付宝支付";
		}else if(channelCode.equals(HYConstant.CHANNEL_WXPAY)){
			payChannel = "微信支付";
		}else if(channelCode.equals(HYConstant.CHANNEL_YK_WXPAY)){
			payChannel = "微信支付";
		}else if(channelCode.equals(HYConstant.CHANNEL_OPERATOR)){
			payChannel = "充值卡支付";
		}else if(channelCode.equals(HYConstant.CHANNEL_SMS)){
			payChannel = "短信支付";
		}else if(channelCode.equals(HYConstant.CHANNEL_UNIONPAY)){
			payChannel = "银联支付";
		}
		return payChannel;
	}
}
