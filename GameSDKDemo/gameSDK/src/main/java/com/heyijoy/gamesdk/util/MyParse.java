package com.heyijoy.gamesdk.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.data.BenefitBean;
import com.heyijoy.gamesdk.data.ConsumeBenefitsBean;
import com.heyijoy.gamesdk.data.ConsumeBenefitsPresentBean;
import com.heyijoy.gamesdk.data.MsgBean;
import com.heyijoy.gamesdk.data.OperatorBean;
import com.heyijoy.gamesdk.data.OrderBean;
import com.heyijoy.gamesdk.data.OrderFloatBean;
import com.heyijoy.gamesdk.data.TaskBean;
import com.heyijoy.gamesdk.data.VipBean;
import com.heyijoy.gamesdk.data.VipCodeBean;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.lib.HYConstant;

public class MyParse {

	public static HashMap<String, Object> parseOperator(String jsonData) {
		HashMap<String, Object> operatorBeanList = new HashMap<String, Object>();
		OperatorBean opratorbean = new OperatorBean();
		try {
			JSONArray jsonArray = new JSONArray(jsonData);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject temp = (JSONObject) jsonArray.get(i);
				opratorbean = new OperatorBean();
				opratorbean.setCardDesc(temp.getString("card_desc"));
				opratorbean.setCardOperator(temp.getString("card_operator"));
				opratorbean.setCardType(temp.getString("card_type"));
				JSONArray cardAmount = temp.getJSONArray("card_amount_list"); 
				List<String> cardAmountList = new ArrayList<String>();
				cardAmountList.add("请选择");
				for (int j = 0; j < cardAmount.length(); j++) {
					cardAmountList.add(cardAmount.getString(j));
				}
				opratorbean.setCardAmountList(cardAmountList);
				operatorBeanList.put(temp.getString("card_operator"),
						opratorbean);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return operatorBeanList;
	}

	public static HashMap<String, Object> parseOrderList(String jsonData) {
		HashMap<String, Object> orderBeanMap = new HashMap<String, Object>();
		OrderBean orderBean;
		ArrayList<OrderBean> orderBeanList = new ArrayList<OrderBean>();
		try {
			JSONObject json = new JSONObject(jsonData);
			int total = json.getInt("total");
			JSONArray jsonArray = json.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) { 
				JSONObject temp = (JSONObject) jsonArray.get(i);
				orderBean = new OrderBean();
				orderBean.setStatus(temp.getString("status"));
				orderBean.setPayChannel(temp.getString("pay_channel"));
				orderBean.setGoodName(temp.getString("good_name"));
				orderBean.setTime(temp.getString("time"));
				orderBean.setOrderID(temp.getString("order_id"));
				orderBean.setPrice(temp.getString("price"));
				orderBean.setRebate(temp.getString("rebate"));
				orderBean.setBonus(temp.getString("bonus"));
				orderBean.setVip(temp.getString("vip"));
				orderBeanList.add(orderBean);
			}
			orderBeanMap.put("total", total);
			orderBeanMap.put("orderBeanList", orderBeanList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return orderBeanMap;
	}

	public static void parseVipMsg(String jsonData, HYCallBack callBack) {
		try {
			Logger.d("pack&ver", "jsonData----jsonData="+jsonData);
			VipBean vipBean = new VipBean();
			JSONObject json = new JSONObject(jsonData);
			String status = json.getString("status");
			if (!"failed".equals(status)) {
				String forumSwitch = json.getString("forum_switch");
				String forumUrl = json.getString("forum_url");
				String isVip = json.getString("isVIP");
				String vipMsg = json.getString("send_package_msg");// 提示信息
				String vipUrl = json.getString("vip_page_url");// 会员购买入口地址
				String vipSwitch = json.getString("vip_switch");
				String userAvatar = json.getString("user_avatar");
				String userName = json.getString("user_name");
				String vipPkgSwitch = json.getString("package_code_switch");// 会员购买入口开关
				String vipScheme = json.getString("vip_activity_scheme");// 会员方案，1礼包码、2通知cp
				String vipYtid = json.getString("ytid");// ytid
				String welfareSwitch = json.getString("welfare_switch");// 悬浮窗福利开关，1显示、2隐藏
				String giftSwitch = json.getString("gift_switch_value");// 悬浮窗消费福利开关，1显示、0隐藏
				String displayRecorderSwitch = json.getString("display_recorder_switch");// 悬浮窗录屏开关，1显示、2隐藏

				if("1".equals(welfareSwitch)){
					GameSDKApplication.getInstance().setWelfare(true);
					String welfareUrl = json.getString("welfare_url");// 悬浮窗福利url
					Logger.v("welfareUrl=="+welfareUrl);
					GameSDKApplication.getInstance().setWelfareUrl(welfareUrl);
				}else{
					GameSDKApplication.getInstance().setWelfare(false);
				}
				if("1".equals(displayRecorderSwitch)){
					GameSDKApplication.getInstance().setVideo(true);
				}else{
					GameSDKApplication.getInstance().setVideo(false);
				}
				vipBean.setForumSwitch(forumSwitch);
				vipBean.setForumUrl(forumUrl);
				vipBean.setIsVip(isVip);
				vipBean.setVipMsg(vipMsg);
				vipBean.setVipUrl(vipUrl);
				vipBean.setVipSwitch(vipSwitch);
				vipBean.setUserAvatar(userAvatar);
				vipBean.setUserName(userName);
				vipBean.setVipPkgSwitch(vipPkgSwitch);
				vipBean.setVipScheme(vipScheme);
				vipBean.setVipYtid(vipYtid);
				vipBean.setIsVipGood(true);
				vipBean.setConsumeBenefitSwitch(giftSwitch);
			} else {
				int error = json.getInt("error");
				if (-1 == error) {
					vipBean.setForumSwitch("");
					vipBean.setForumUrl("");
					vipBean.setIsVip("");
					vipBean.setVipMsg("");
					vipBean.setVipUrl("");
					vipBean.setVipSwitch("");
					vipBean.setUserAvatar("");
					vipBean.setUserName("");
					vipBean.setVipPkgSwitch("");
					vipBean.setVipScheme("");
					vipBean.setVipYtid("");
					vipBean.setIsVipGood(false);
					vipBean.setConsumeBenefitSwitch("");
					GameSDKApplication.getInstance().setVideo(false);
				}
			}
			GameSDKApplication.getInstance().saveVip(vipBean);
			callBack.onSuccess(vipBean);
		} catch (Exception e) {
			HttpApi.getInstance().exceptionFailed(HYConstant.EXCEPTION_CODE, e.toString(), callBack);
			e.printStackTrace();
		}
	}

	public static ArrayList<VipCodeBean> parseVipCodeList(String jsonData) {
		ArrayList<VipCodeBean> vipCodeBeanList = new ArrayList<VipCodeBean>();
		try {
			JSONObject json = new JSONObject(jsonData);
			JSONArray codes = json.getJSONArray("codes");
			VipCodeBean vipCodeBean = null;
			JSONObject codeJson = new JSONObject();
			for (int i = 0; i < codes.length(); i++) {
				vipCodeBean = new VipCodeBean();
				codeJson = codes.getJSONObject(i);
				vipCodeBean.setCode(codeJson.getString("code"));
				vipCodeBean.setExpire(codeJson.getString("expire"));
				vipCodeBeanList.add(vipCodeBean);
			}
		} catch (Exception e) {
			// callBack.onFailed("程序错误");
		}
		return vipCodeBeanList;
	}

	public static HashMap<String, String> parseWXPay(String params) {
		HashMap<String, String> paramsMap = new HashMap<String, String>();
		String[] tempParams = params.split("&");
		for (int i = 0; i < tempParams.length; i++) {
			String tempKeyValue = tempParams[i];
			int keyIndex = tempKeyValue.indexOf("=");
			if (keyIndex > 0) {
				String tempKey = tempKeyValue.substring(0, keyIndex);
				String tempValue = tempKeyValue.substring(keyIndex + 1,
						tempKeyValue.length());
				paramsMap.put(tempKey, tempValue);
			}
		}
		return paramsMap;
	}

	public static void parseFloatOrder(String result, HYCallBack callBack) {
		Logger.d("pack&ver", "jsonData-Order="+result);
		JSONObject json = null;
		JSONArray resultsAll=null;
		StringBuilder sb = null;
		String code = null;
		OrderFloatBean orderFloatBean=new OrderFloatBean();
		try {
			json = new JSONObject(result);
			code = json.optString("status", "success");
			if ("failed".equals(code)) {
//				callBack.onFailed("failed");
			}else
			{
				resultsAll=json.getJSONArray("floatorder_first");
				sb = new StringBuilder();
				sb.append("");
				for (int j = 0; j < resultsAll.length(); j++) {
					sb.append(resultsAll.get(j));
					if (j != resultsAll.length() - 1) {
						sb.append(",");
					} 
				}
				orderFloatBean.setOrderFirst(sb.toString());
				
				resultsAll=json.getJSONArray("floatorder_second");
				sb = new StringBuilder();
				sb.append("");
				for (int j = 0; j < resultsAll.length(); j++) {
					sb.append(resultsAll.get(j));
					if (j != resultsAll.length() - 1) {
						sb.append(",");
					}
				}
				orderFloatBean.setOrderSecond(sb.toString());
				
				GameSDKApplication.getInstance().setOrderFloatBean(orderFloatBean);
				callBack.onSuccess(null);
			}
		} catch (Exception e) {
			HttpApi.getInstance().exceptionFailed(HYConstant.EXCEPTION_CODE, e.toString(), callBack);
			e.printStackTrace();
		}
	
	}
	
	public static MsgBean parsePushMsg(String jsonData) {
		try {
			JSONObject json = new JSONObject(jsonData);
			MsgBean msgBean = new MsgBean();
			msgBean.setMsgId(json.getString("id"));
			msgBean.setMsgType(json.getString("msg_type"));
			msgBean.setMainTitle(json.getString("main_title"));
			msgBean.setLinkUrl(json.getString("link_url"));
			msgBean.setContent(json.getString("content"));
			msgBean.setButtTitle(json.getString("button_title"));
			StringBuilder sb = new StringBuilder();
			sb.append("");
			JSONArray appIds = json.getJSONArray("app_ids");
			for (int i = 0; i < appIds.length(); i++) {
				sb.append(appIds.get(i));
				if (i != appIds.length() - 1) {
					sb.append(",");
				}
			}
			String apps = sb.toString();
			msgBean.setAppIds(apps);
			String mid = json.getString("mid");
			msgBean.setMid(mid);
			msgBean.setIsDisplayed(false);
			msgBean.setSendTime(json.getString("send_time"));
			msgBean.setOverdueTime(json.getString("overdue_time"));
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			msgBean.setIdUrl(json.getString("id_url"));
			msgBean.setIcon(json.getString("icon"));
			msgBean.setPackageName(json.getString("package_name"));
			msgBean.setArriveTime(formatter.format(new Date()));
			GameSDKApplication.getInstance().setMid(mid);
			Logger.d("pack&ver", "解析成功");
			Logger.d("pack&ver", "apps=" + apps);
			if (apps.contains(GameSDKApplication.getInstance().getAppid())
					|| "".equals(apps)) {
				return msgBean;
			} else {
				return null;
			}
		} catch (Exception e) {
			Logger.d("pack&ver", "解析错误");
			e.printStackTrace();
			return null;
		}
	}

	public static HashMap<String, BenefitBean> parseBenefit(String jsonData) {
		HashMap<String, BenefitBean> paramsMap = new HashMap<String, BenefitBean>();
		BenefitBean bean = null;
		JSONObject json = null;
		String result = null;
		String code = null;
		try {
			json = new JSONObject(jsonData);
			code = json.optString("status", "success");
			if ("failed".equals(code)) {
				return null;
			}
			result = json.getString("result");
			if (result.equals("success")) {
				bean = new BenefitBean();
				bean.setTitle(json.getString("title"));
				bean.setSubTitle(json.getString("sub_title"));
				bean.setContent(json.getString("content"));
				bean.setCode(json.getString("code"));
			}
			paramsMap.put("1", bean);
			bean = null;
			json = json.getJSONObject("result_vip");
			result = json.getString("result");
			if (result.equals("success")) {
				bean = new BenefitBean();
				bean.setTitle(json.getString("title"));
				bean.setSubTitle(json.getString("sub_title"));
				bean.setContent(json.getString("content"));
				bean.setCode(json.getString("code"));
			}
			paramsMap.put("0", bean);
		} catch (Exception e) {
			Logger.d("pack&ver", "解析错误2");
			return null;
		}
		return paramsMap;
	}
	
	public static void parseTask(String jsonData, HYCallBack callBack) {
		Logger.d("pack&ver", "jsonData-Task="+jsonData);
		JSONObject json = null;
		String code = null;
		ArrayList<TaskBean> taskBeanList = new ArrayList<TaskBean>();
		TaskBean taskBean=null;
		try {
			json = new JSONObject(jsonData);
			code = json.optString("status", "success");
			if ("failed".equals(code)) {
				HttpApi.getInstance().statusFailed(json, callBack);
			}else
			{
				JSONArray resultsAll=json.getJSONArray("results");
				GameSDKApplication.getInstance().setTableListID(json.getString("task_list_id"));
				for (int j = 0; j < resultsAll.length(); j++) {
					JSONObject temp = (JSONObject) resultsAll.get(j);
					taskBean=new TaskBean();
					String id=temp.getString("id");
					taskBean.setId(id);
					taskBean.setStatus(temp.getString("status"));
					taskBean.setTaskType(temp.getString("task_type"));
					taskBean.setBonus(temp.getString("bonus"));
					taskBean.setDesc(temp.getString("desc"));
					taskBean.setAmount(temp.getInt("amount"));
					if("".equals(id))
					{
						taskBeanList.add(null);
					}else
					{
						taskBeanList.add(taskBean);
					}
				}
				GameSDKApplication.getInstance().setTaskBeanList(taskBeanList);
				callBack.onSuccess(null);
			}
		} catch (Exception e) {
//			callBack.onFailed("");
			e.printStackTrace();
		}
	}
	/**
	 * 获取消费福利档位信息
	 * @param callBack
	 */
	public static HashMap<String, Object> parseConsumeBenefitsMsg(String jsonData) {
		JSONObject json = null;
		String code = null;
		String userAmount = "0";
		HashMap<String, Object> objectMap = new HashMap<String, Object>();
		List<ConsumeBenefitsBean> beanList = new ArrayList<ConsumeBenefitsBean>();
		ConsumeBenefitsBean bean ;
		try {
			json = new JSONObject(jsonData);
			code = json.optString("error_number", "success");
			if ("0000".equals(code)) {
				userAmount = json.optString("user_amount", "0");
				JSONArray resultsAll=json.getJSONArray("grades");
				for (int j = 0; j < resultsAll.length(); j++) {
					JSONObject temp = (JSONObject) resultsAll.get(j);
					bean = new ConsumeBenefitsBean();
					String status = temp.getString("status");
					String amount = temp.getString("amount");
					bean.setStatus(status);
					bean.setAmount(amount);
					beanList.add(bean);
				}
				objectMap.put("beanList", beanList);
				objectMap.put("userAmount", userAmount);
				objectMap.put("isSuccess", "true");
				return objectMap;
			}else{
				String error = json.getString("error_desc");
				objectMap.put("isSuccess", "false");
				objectMap.put("failReason", error);
				return objectMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取消费福利某档位礼品信息
	 * @param callBack
	 */
	public static HashMap<String, Object> parseConsumeBenefitsPresents(String jsonData) {
		JSONObject json = null;
		String code = null;
		String status = null;
		HashMap<String, Object> objectMap = new HashMap<String, Object>();
		List<ConsumeBenefitsPresentBean> gridBeanList = new ArrayList<ConsumeBenefitsPresentBean>();
		ConsumeBenefitsPresentBean gridBean ;
		try {
			json = new JSONObject(jsonData);
			code = json.optString("error_number", "success");
			status = json.optString("status", "2");
			
			if ("0000".equals(code)) {
				JSONArray resultsAll=json.getJSONArray("gifts");
				for (int j = 0; j < resultsAll.length(); j++) {
					JSONObject temp = (JSONObject) resultsAll.get(j);
					gridBean = new ConsumeBenefitsPresentBean();
					gridBean.setId(temp.getString("gift_id"));
					gridBean.setName(temp.getString("gift_name"));
					gridBean.setPicUrl(temp.getString("gift_image"));
					gridBeanList.add(gridBean);
				}
				objectMap.put("code", code);
				objectMap.put("status", status);
				objectMap.put("gridBeanList", gridBeanList);
				return objectMap;
			}else{
				String error = json.getString("error_desc");
				objectMap.put("code", code);
				objectMap.put("error", error);
				return objectMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 为领取物品提交个人信息
	 * @param callBack
	 */
	public static HashMap<String, String> parseConsumeBenefitsPersonnelMsg(String jsonData) {
		HashMap<String, String> map = new HashMap<String, String>();
		JSONObject json = null;
		String code = null;
		try {
			json = new JSONObject(jsonData);
			code = json.optString("error_number", "success");
			if ("0000".equals(code)) {
				String error = json.getString("error_desc");
				map.put("code", code);
				map.put("failReason", error);
			}else{
				String error = json.getString("error_desc");
				map.put("code", code);
				map.put("failReason", error);
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
