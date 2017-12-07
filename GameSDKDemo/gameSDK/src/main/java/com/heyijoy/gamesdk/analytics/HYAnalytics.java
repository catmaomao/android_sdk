package com.heyijoy.gamesdk.analytics;

import com.heyijoy.gamesdk.act.HYCallBack;
import com.heyijoy.gamesdk.data.Bean;
import com.heyijoy.gamesdk.data.InitParams;
import com.heyijoy.gamesdk.data.CommitData;
import com.heyijoy.gamesdk.http.HttpApi;

/**
 * 统计分析
 * 
 * @author shaohuma
 *
 */
public class HYAnalytics {
	
	private static HYAnalytics hyAnalytics = null;

	private HYAnalytics() {}

	public static synchronized HYAnalytics getInstance() {
		if (hyAnalytics == null) {
			hyAnalytics = new HYAnalytics();
		}
		return hyAnalytics;
	}
	
	/**
	 * 数据统计，初始化sdk
	 * 
	 * @param params
	 */
	public void initAnalytics(InitParams params) {
		HttpApi.getInstance().initAnalytics(params, new HYCallBack() {

			@Override
			public void onSuccess(Bean bean) {

			}

			@Override
			public void onFailed(int code, String failReason) {

			}
		});
	}

	/**
	 * 上报数据
	 * 
	 * @param extraData     
	 *  
	 * 选择服务器时，dataType为1；
	 * 创建角色的时候，dataType为2；
	 * 进入游戏时，dataType为3；
	 * 等级提升时，dataType为4；
	 * 退出游戏时，dataType为5
	 */
	public void submitExtraData(CommitData extraData) {
		try {
			HttpApi.getInstance().submitDataAnalytics(extraData, new HYCallBack() {

				@Override
				public void onSuccess(Bean bean) {

				}

				@Override
				public void onFailed(int code, String failReason) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
