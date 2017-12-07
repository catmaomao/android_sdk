package com.heyijoy.gamesdk.activity;

import com.heyijoy.gamesdk.act.GameSDKApplication;
import com.heyijoy.gamesdk.data.MsgBean;
import com.heyijoy.gamesdk.http.HttpApi;
import com.heyijoy.gamesdk.sql.MsgDBTool;
import com.heyijoy.gamesdk.util.Util;
import com.heyijoy.gamesdk.widget.WebViewActivity;
import com.heyijoy.gamesdk.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class HYRelayActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hy_activity_init);
		if(GameSDKApplication.getInstance()==null ||GameSDKApplication.getInstance().getContext() == null){
			GameSDKApplication.getInstance().init(this);
		}
		init();
	}

	private void init() {
		doYKNotifyBar();
	}


	private void doYKNotifyBar() {// 推送消息点击跳转游戏中心事件
		MsgBean msgBean = (MsgBean) getIntent().getSerializableExtra("msgBean");
		MsgDBTool dbTool = new MsgDBTool(this);
		dbTool.updataIsDisplayed(msgBean.getMsgId());// 设置为已读
		int clickFrom = getIntent().getIntExtra("clickFrom", 2);
		String type = msgBean.getMsgType();// 1.会员 2.活动 3.礼包 4.专题 5.下载 6.其他
		String linkUrl = msgBean.getLinkUrl();
		String idUrl = msgBean.getIdUrl();
		Intent intent = new Intent();
		ComponentName cn = new ComponentName(Util.getPack(), linkUrl);
		intent.setComponent(cn);
		if (type.equals("1")) {
			try {
				Intent i = new Intent(this, WebViewActivity.class);
				String url = linkUrl + "?userType="
						+ GameSDKApplication.getInstance().getVipIsVip()
//						+ "&ytid="
//						+ GameSDKApplication.getInstance().getVipYtid()
						+ "&appId="
						+ GameSDKApplication.getInstance().getAppid();
						if(url.contains("http:")){
							i.putExtra("url", url);
						}else{
							i.putExtra("url", "http://"+url);
						}
				startActivity(i);
			} catch (Exception e) {
				Toast.makeText(this, "信息已过期", 3000).show();
				e.printStackTrace();
			}
			finish();
			return;
		} else if (type.equals("6")) {// 其他页不跳转
			finish();
			return;
		} else if (linkUrl.equals("com.youku.gamecenter.GameWebViewActivity")) {// 活动/专题详情页
			intent.putExtra("url", idUrl);
			intent.putExtra("source", type.equals("2") ? "39" : "49");// 39为活动,49为专题
		} else if (linkUrl
				.equals("com.youku.gamecenter.GameH5CardListActivity")) {// 活动/专题列表页
			intent.putExtra("type", (type.equals("2") ? "1" : "0"));// 1为活动,0为专题
		} else if (linkUrl.equals("com.youku.gamecenter.GamePresentActivity")) {// 礼包列表页
		} else if (linkUrl.equals("com.youku.gamecenter.GameDetailsActivity")) {// 游戏详情页
			intent.putExtra("appId", idUrl);
			intent.putExtra("packagename", msgBean.getPackageName());
			intent.putExtra("source", "36");
		} else if (linkUrl
				.equals("com.youku.gamecenter.GamePresentListActivity")) {// 单一游戏礼包列表页(跳本游戏的，由朱浩然确定)
			intent.putExtra("appId", GameSDKApplication.getInstance()
					.getAppid());
			intent.putExtra("packagename", getApplicationInfo().packageName);
		} else if (linkUrl
				.equals("com.youku.gamecenter.GamePresentDetailsActivity")) {// 单一游戏礼包详情页(跳本游戏的，由朱浩然确定)
			if(idUrl==null||"".equals(idUrl))
			{
				Toast.makeText(this, "客户端异常-缺少数据idUrl", 3000).show();
				finish();
				return;
			}
			intent.putExtra("presentId", idUrl);
			intent.putExtra("packagename", getApplicationInfo().packageName);
		} else if (linkUrl
				.equals("com.youku.gamecenter.GameCenterHomeActivity")) {// 跳转游戏中心首页
			intent.putExtra("source", "36");
			intent.putExtra("packagename", getApplicationInfo().packageName);
		} else {// 默认跳转游戏中心首页
			cn = new ComponentName(Util.getPack(),
					"com.youku.gamecenter.GameCenterHomeActivity");
			intent.putExtra("source", "36");
			intent.putExtra("packagename", getApplicationInfo().packageName);
		}
		try {
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "请下载最新版本的合乐智趣客户端！", 3000).show();
		}
		finish();
	}

}
