package com.heyijoy.gamesdk.lib;

import java.util.HashSet;
import java.util.Set;

import android.util.Log;

public class HYConstant {

	/**
	 * 返回sdk版本 通过代码写sdk版本
	 */
	public static String getSDKVersion() {
		String version = "1.1.5";
		return version;
	}

	public static final String TAG = "HeyiJoySDK";
	public static final String QQ_LOGIN_BIND = "qq_login_bind";
	public static final String WX_LOGIN_BIND = "wx_login_bind";
	public static final String WEIBO_LOGIN_BIND = "weibo_login_bind";
	public static final String QQ_LOGIN = "qq_login";
	public static final String WX_LOGIN = "wx_login";
	public static final String WEIBO_LOGIN = "weibo_login";
	
	public static final String SHARE_TITLE = "SHARE_TITLE";
	public static final String SHARE_DESC = "SHARE_DESC";
	public static final String SHARE_ICON_NAME = "SHARE_ICON_NAME";
	public static final String SHARE_WEBURL = "SHARE_WEBURL";
	public static final String SHARE_IMGURL = "SHARE_IMGURL";
	
	public static final String SHARE_QQ = "SHARE_QQ";
	public static final String SHARE_WX = "SHARE_WX";
	public static final String SHARE_WX_CRICLE = "SHARE_WX_CRICLE";
	public static final String SHARE_WEIBO = "SHARE_WEIBO";
	public static final String AAA = "AAA";
	public static final String BBB = "BBB";
	public static final int EXCEPTION_CODE = 2000;// 异常失败，如数据解析错误等
	public static final String AGREEMENT = "http://heyi.youku-game.com/xieyi/agreement.htm";// 用户协议地址

	public static final String HEYIJOY_KEY = "ÔÐÐÜöþÐÛØØÒÚØÈÜØ²ò¯ÏñØÒêì¨Úõ©²­ÛÊíÝÚë÷ÁÒ×úãèí¬²ãí¶ûÛÞ¨ÃìÛúîÞÔ®«¶ôÔ¡ÕÖ×Øñëý÷¶­òÛôé«ßóÍÚèñ¨ýïÀñÒø¯ò®úÞßëïÐøõÚÐàýó÷ÞÜÉõøØöóóÑðÐªÕÖà";
	public static final String PID = "11601d277adceb61";
	public static final String LANGUAGE = "java";
	public static final String ANDROID = "android";
	public static final String SDK_API_VER = "1.0.1";
	public static final String TITLE_LOGIN = "账号登录";
	public static final String TITLE_CENTER = "账号管理";
	public static final String TITLE_MODIFY_PWE = "修改密码";
	public static final String TITLE_CERTIFICATION = "实名认证";
	public static final String TITLE_QUIT = "退出游戏";
	public static final String TITLE_VIP = "会员费用支付成功";
	public static final String TITLE_ANNOUNCEMENT = "系统公告";
	public static final String TITLE_REG = "注册账号";
	public static final String TITLE_BINDING = "绑定手机";
	public static final String TITLE_FIND_PWD = "找回密码";
	public static final String TITLE_FORGET_PWD = "忘记密码";
	public static final String HINT_INFO = "温馨提示";
	public static final int TIME_ONE_BUTTON_MSG_SEND_WAIT = 15000;// 上行等待时间
	public static final int TIME_ONE_BUTTON_REG_WAIT = 40000;// 下行等待时间
	public static final int TIME_RE_SEND_VERIFY_WAIT = 60000;// 验证码 重新发送等待时间
	public static final String DEFAULT_PACKNAME = "com.heyijoy.phone";// 默认包名
	public static final String YKMAIN_SIGN_MD5 = "443726a83b7a575e2d0b7985097ebb24";// 主客签名MD5
	public static String YKMAIN_SIGN_MD5_TASK = "XsEYgS7VuRTuyzgt1CP3x9GZ4FXmsLQtv+7LXdY7z3sphUJRAkEA+GLa4rpc5Bur";// 积分任务签名，针对积分任务格外添加的签名

	public static final String YK_TENPAY_CALLBACK = "yk_tenpay_callback";// 财付通支付回调广播
	public static final String YK_YK_XPAY_CALLBACK = "com.youku.phone.action.game.wxpay";// 微信主客支付回调广播
	public static final String YK_CHANGE_ACCOUNT = "android.youku.change_account";// 切换账号回调广播
	public static final String YK_SDK_PUSH_RELAY = "android.youku.sdk.pushrelay";// 内部push接收广播
	public static final String YK_SDK_PUSH_ORDER_RELAY = "android.youku.sdk.order.pushrelay";// 内部有序弹notification的push接收广播
	public static final String YK_SDK_FLOAT = "android.youku.sdk.float";// 悬浮窗广播
	public static final String YK_SDK_UPLOAD = "android.youku.sdk.upload";// 上传广播
	public static final String START_YK_SDK_PUSH_RELAY = "android.youku.sdk.start.pushrelay";// 启动内部push广播，和开机启动的广播一样，这个是启动游戏的时候广播一次，然后开启Service

	public static final String YK_FOME_GAME_CENTER = "com.youku.sdk.action.GET_MSG_BY_SDK";// 对应接收广播
	public static final String YK_TO_GAME_CENTER = "com.youku.gamecenter.outer.action.GET_PACKAGE_VERSION";// 对应发送广播
	public static final String YK_FOR_PUSH_MSG_RECE = "com.youku.sdkgame.action.RECE_FORM_PUSH_MSG";// 接收消息广播

	public static final String STATISTIC_NAME_INIT = "初始化";// 初始化
	public static final String STATISTIC_NAME_REG = "注册";// 注册
	public static final String STATISTIC_NAME_LOGIN = "登录";// 登录
	public static final String STATISTIC_NAME_PAY = "支付";// 支付
	public static final String STATISTIC_NAME_RECHARGE = "充值";// 充值
	public static final String STATISTIC_NAME_FLOAT = "悬浮窗";// 悬浮窗
	public static final String STATISTIC_NAME_BUY_VIP = "会员统计";// 会员统计
	public static final String STATISTIC_NAME_VIDEO = "录屏统计";// 录屏统计
	public static final String STATISTIC_NAME_MSG_DOWN_TIME = "短信下行时长";// 短信下行时长
	public static final String STATISTIC_NAME_ANNOUNCEMENT_CLICK = "运营";// 活动点击
	public static final String STATISTIC_NAME_REG_PROCESS = "注册过程";// 注册过程
	public static final String STATISTIC_NAME_PAGE = "页面访问";// 页面访问
	public static final String STATISTIC_NAME_BINDING = "绑定";// 页面访问
	public static final String STATISTIC_NAME_EXCEPTION = "异常";// 异常
	public static final String STATISTIC_NAME_MESSAGE_TIME = "消息时间";// 消息时间
	public static final String STATISTIC_NAME_MESSAGE = "消息";// 消息

	public static final String STATISTIC_EVENT_TYPE_INIT = "INIT";// 初始化
	public static final String STATISTIC_EVENT_TYPE_REG = "REG";// 注册
	public static final String STATISTIC_EVENT_TYPE_PAGE = "PAGE";// 页面访问
	public static final String STATISTIC_EVENT_TYPE_LOGIN = "LOGIN";// 登录
	public static final String STATISTIC_EVENT_TYPE_PAY = "PAY";// 支付
	public static final String STATISTIC_EVENT_TYPE_RECHARGE = "RECHARGE";// 充值
	public static final String STATISTIC_EVENT_TYPE_FLOAT = "DROPZONED";// 悬浮窗
	public static final String STATISTIC_EVENT_TYPE_MSG_DOWN_TIME = "MSG_DOWN_TIME";// 短信下行时长
	public static final String STATISTIC_EVENT_TYPE_ANNOUNCEMENT_CLICK = "OPERATIONS";// 活动统计
	public static final String STATISTIC_EVENT_TYPE_MESSAGE_TIME = "MESSAGESTIME";// 消息时间统计
	public static final String STATISTIC_EVENT_TYPE_MESSAGE = "MESSAGES";// 消息统计
	public static final String STATISTIC_EVENT_TYPE_REG_PROCESS = "REG_PROCESS";// 注册过程
	public static final String STATISTIC_EVENT_TYPE_BINDING_PROCESS = "BINDING_PROCESS";// 绑定过程
	public static final String STATISTIC_EVENT_TYPE_BUY_VIP = "VIP";// 购买vip
	public static final String STATISTIC_EVENT_TYPE_VIDEO = "RECORDINGVIDEO";// 购买vip
	public static final String STATISTIC_EVENT_TYPE_EXCEPTION = "EXCEPTION";// 异常统计

	public static final String MSG_REG_CONTENT = "注册合乐智趣游戏。";// 注册上行短信，友好提示

	public static final String COL_NAME_GAME_TRACE_SOURCE_1 = "source_1";// 来自主客的入口
																			// 1
	public static final String COL_NAME_GAME_TRACE_SOURCE_2 = "source_2";// 来自主客的入口
																			// 2
	/* 注册条款 */
	public static final String MSG_BUNDLE_KEY_USER = "MSG_BUNDLE_KEY_USER";
	public static final String MSG_BUNDLE_KEY_FAIL_MSG = "MSG_BUNDLE_KEY_FAIL_MSG";
	public static final String PREF_FILE_USER = "PREF_FILE_USER";
	public static final String PREF_FILE_USER_SHARE = "PREF_FILE_USER_SHARE";
	public static final String YK_EXCEPTION = "yk_exception.xml";// 未捕获异常处理的信息本地保存文件名
	public static final String YK_PACK_VER = "yk_pack_ver.xml";
	public static final String PREF_FILE_USER_NAME = "PREF_FILE_USER_NAME";
	public static final String PREF_FILE_USER_PHONE = "PREF_FILE_USER_PHONE";
	public static final String PACK_FROM_GAMECENTER = "PACK_FROM_GAMECENTER";
	public static final String PREF_FILE_USER_PWD = "PREF_FILE_USER_PWD";
	public static final String PREF_FILE_USER_DEVICEID = "PREF_FILE_USER_DEVICEID";
	public static final String MAIN_USER_LIFECODE = "MAIN_USER_LIFECODE";
	public static final String MAIN_USER_THRID = "MAIN_USER_THRID";
	public static final String MAIN_USER_BEFORE_LOGOUT = "MAIN_USER_BEFORE_LOGOUT";
	public static final String MAIN_USER_THRID_BIND_STATUD = "MAIN_USER_THRID_BIND_STATUD";
	public static final String PREF_FILE_USER_ISPAY = "PREF_FILE_USER_ISPAY";
	public static final String PREF_FILE_USER_UID = "PREF_FILE_USER_UID";
	public static final String PREF_FILE_USER_IS_YOUKUACCOUNT = "PREF_FILE_USER_IS_YOUKUACCOUNT";
	public static final String PREF_FILE_HAS_BIND_TIME = "PREF_FILE_HAS_BIND_TIME";
	public static final String PREF_FILE_HAS_BIND_TIME_LOGIN = "PREF_FILE_HAS_BIND_TIME_LOGIN";

	public static final String PREF_FILE_CP_ICON_ID = "PREF_FILE_CP_ICON_ID";

	public static final String PREF_FILE_INPOURCARD_INFO = "PREF_FILE_INPOURCARD_INFO";
	public static final String PREF_FILE_INPOURCARD_DESC = "PREF_FILE_INPOURCARD_DESC";

	public static final String PREF_FILE_UPDATE_LOAD = "PREF_FILE_UPDATE_LOAD";
	public static final String PREF_FILE_UPDATE_LOAD_URL = "PREF_FILE_UPDATE_LOAD_URL";
	public static final String PREF_FILE_UPDATE_INSTLL_FILE_NAME = "PREF_FILE_UPDATE_INSTLL_FILE_NAME";
	public static final String PREF_FILE_UPDATE_APP_NAME = "PREF_FILE_UPDATE_APP_NAME";

	public static final String PREF_FILE_FLOAT_PACKAGE_ID = "PREF_FILE_FLOAT_PACKAGE_ID";

	public static final String REG_START = "1";// 开始申请
	public static final String REG_SUCESS_NEW = "2";// 新用户申请成功
	public static final String REG_SUCESS_OLD = "3";// 老用户申请成功
	public static final String REG_SEND_SUCESS = "4";// 短信发送成功
	public static final String REG_NO_SIM = "5";// 没有sim卡 或者欠费等不能发送短信
	public static final String REG_SEND_FAIL = "6";// 收到发送失败的回执
	public static final String REG_SEND_TIME_OUT = "7";// 发送超时或者被拦截
	public static final String REG_SEND_API_EXCEPTION = "8";// 系统短信接口调用，发送异常

	public static final String Toast_New_User = ",欢迎进入游戏";
	public static final String Toast_Old_User = ",欢迎回来";
	public static final String Toast_MODIFY_PWD = "修改成功";
	// 短信上下行地址
	public static String PREF_FILE_REG_MOBILE_UP_NO = "PREF_FILE_REG_MOBILE_UP_NO";// 发送注册手机号
																					// 正式
	public static String PREF_FILE_REG_MOBILE_RECEIVE_NO = "PREF_FILE_REG_MOBILE_RECEIVE_NO";// 发送注册手机号
																								// 正式

	public static final String PREF_FILE_COOKIE = "PREF_FILE_COOKIE";
	public static final String PREF_FILE_LOGOUT_STATUS = "PREF_FILE_LOGOUT_STATUS";// 注销状态
	public static final String FILE_SAVE_PATH = "hyGames";// 下载文件保存路径
	public static final String VIDEO_FILE_SAVE_PATH = "hyGameVideos";// 下载文件保存路径

	public static final int MESSAGE_WHAT_NO_VERSION = 0;// 没有新版本
	public static final int MESSAGE_WHAT_UNDOWN = 1;// 取消下载
	public static final int MESSAGE_WHAT_UNINSTALL = 2;// 后台下载
	public static final int MESSAGE_WHAT_NO_NET = 3;// 没有网络连接

	// vip存储键
	public static final String VIP_IS_VIP = "isVip";// vip状态
	public static final String VIP_MSG = "vipMsg";
	public static final String VIP_URL = "vipUrl";// 购买vip活动展示地址
	public static final String VIP_SWITCH = "vipSwitch";// 悬浮窗中vip购买展示开关
	public static final String USER_AVATAR = "userAvatar";// 用户头像
	public static final String USER_NAME = "userName";// 用户名
	public static final String VIP_PKG_SWITCH = "vipPkgSwitch";// 悬浮窗中vip列表展示开关
	public static final String VIP_SCHEME = "vipScheme";
	public static final String VIP_YTID = "vipYtid";// ytid
	public static final String VIP_SEND_PKG_MSG = "send_package_msg";// vip购买支付成功后提示语
	public static final String FORUM_SWITCH = "forum_switch";// 论坛开关
	public static final String FORUM_URL = "forum_url";// 论坛url
	public static final String IS_VIP_GOOD = "isVipGood";

	// 支付方式代号
	public static final String CHANNEL_ALIPAY = "100";// 支付宝
	public static final String CHANNEL_UNIONPAY = "98";// 银联
	public static final String CHANNEL_WXPAY = "97";// 微信
	public static final String CHANNEL_FENQILEPAY = "92";// 分期乐
	public static final String WXAPPID = "wx879a24fa8b67b795";// 微信appid
	public static final String CHANNEL_SMS = "110";// 短代
	public static final String CHANNEL_YK_WXPAY = "118";// 主客微信
	public static final String CHANNEL_OPERATOR = "12";// 运营商充值卡

	public static String WX_APPID = "";// 微信appid
	public static Boolean isRecharge = false;// 判断当前是充值还是支付

	// 运营商代号
	public static final String CHANNEL_CHINAUNICOM = "1";
	public static final String CHANNEL_CHINAMOBILE = "2";
	public static final String CHANNEL_CHINATELECOM = "3";

	// 支付埋点的常量
	public static final String PAY_TYPE_ALIPAY = "1";// 支付宝支付
	public static final String PAY_TYPE_WXPAY = "2";// 微信支付
	public static final String PAY_TYPE_SMS = "3";// 短代
	public static final String PAY_TYPE_UNIONPAY = "4";// 银联
	public static final String PAY_TYPE_TENPAY = "5";// 财付通
	public static final String RECHA_TYPE_CHINAMOBILE = "6";// 移动充值卡
	public static final String RECHA_TYPE_CHINAUNICOM = "7";// 联通充值卡
	public static final String RECHA_TYPE_CHINATELECOM = "8";// 电信充值卡
	public static final String PAY_TYPE_GOLD = "6";// 金币支付
	public static final String PAY_RECHA_TYPE_CHINAMOBILE = "9";// 移动充值卡充值并消费
	public static final String PAY_RECHA_TYPE_CHINAUNICOM = "10";// 联通充值卡充值并消费
	public static final String PAY_RECHA_TYPE_CHINATELECOM = "11";// 电信充值卡充值并消费
	public static final String PAY_TYPE_YK_WXPAY = "12";// 微信主客支付统计

	public static final String PAY_ASK = "1";// 支付请求状态
	public static final String PAY_SUCCESS = "2";// 支付成功状态
	public static final String PAY_FAILED = "3";// 支付失败状态

	// 支付宝的版本号
	public static final String VERSION_ALIPAY = "v2";

	// 短信返回提示头
	public static final String MMS_TITLE_ONEBUTTON_NEW_USER = "用户注册：";
	public static final String MMS_TITLE_ONEBUTTON_OLDER_USER = "身份验证：";
	public static final String MMS_TITLE_REG_GET_VERIFYNO = "验证身份：";
	// public static final String MMS_TITLE_REG_NEW_USER_BY_VERIFYNO = "账号注册";
	public static final String MMS_TITLE_FIND_PWD = "找回密码：";
	public static final String MMS_TITLE_BINDING = "绑定验证：";
	public static final String MMS_TITLE_BINDING_PHONE = "绑定手机：";

	public static String REG_MOBILE_UP_NO = "951312948479";// 发送注册手机号 正式
	public static String REG_MOBILE_RECEIVE_NO = "951312948479";// 接收号码 正式

	/*
	 * public static String REG_MOBILE_RECEIVE_NO_1 = "951312948479";//接收号码 正式
	 * public static String REG_MOBILE_RECEIVE_NO_2 = "951312948479";//接收号码 正式
	 * public static String REG_MOBILE_RECEIVE_NO_3 =
	 * "951312093239610021";//接收号码 正式
	 */

	public static Set<String> REG_MOBILE_RECEIVE_LIST = new HashSet<String>();

	public static String INIT_DOMAIN_NAME = "https://mainsdk.youku-game.com";// 初始化访问域名（正式）
	public static String INIT_DOMAIN_NAME_TEST = "https://sandbox-mainsdk.youku-game.com";// 初始化访问域名（测试）

	// static {
	// if (HYBuildConfig.IS_TEST) {
	// INIT_DOMAIN_NAME_TEST = "https://sandbox-mainsdk.youku-game.com";//
	// 初始化访问域名（测试）
	// // INIT_DOMAIN_NAME = "172.16.78.99:8000";//初始化访问域名（测试）
	//
	// REG_MOBILE_RECEIVE_LIST.add("951312948480");
	// REG_MOBILE_RECEIVE_LIST.add("951312093239610021");
	// YKMAIN_SIGN_MD5_TASK =
	// "uP2nVtzYkz725jjIwSn7AkASbLvAAT09Jm646im7CEia3nRdgtu0W7+FR8BllQTv";
	// } else {
	// INIT_DOMAIN_NAME = "https://mainsdk.youku-game.com";// 初始化访问域名（正式）
	//
	// REG_MOBILE_UP_NO = "951312948479";// 发送注册手机号 正式
	// REG_MOBILE_RECEIVE_LIST.add("951312948479");
	// REG_MOBILE_RECEIVE_LIST.add("951312093239610021");
	// REG_MOBILE_RECEIVE_LIST.add("1252004010681792");
	// YKMAIN_SIGN_MD5_TASK =
	// "XsEYgS7VuRTuyzgt1CP3x9GZ4FXmsLQtv+7LXdY7z3sphUJRAkEA+GLa4rpc5Bur";
	// }
	// }

	public static String urlAddress() {
//		if (HYBuildConfig.IS_TEST) {
//			INIT_DOMAIN_NAME_TEST = "https://sandbox-mainsdk.youku-game.com";// 初始化访问域名（测试）
//			Log.e("HeyiJoySDK", "dev environment=" + INIT_DOMAIN_NAME_TEST);
//			HYBuildConfig.DEBUG = true;
//			return INIT_DOMAIN_NAME_TEST;
//		} else {
//			INIT_DOMAIN_NAME = "https://mainsdk.youku-game.com";// 初始化访问域名（正式）
//			HYBuildConfig.DEBUG = false;
//			return INIT_DOMAIN_NAME;
//		}
		INIT_DOMAIN_NAME = "https://main.youku-game.com";// 初始化访问域名（正式）
		return INIT_DOMAIN_NAME;
	}

	public static final String URL_INIT = urlAddress() + "/game/initial";// 初始化
	public static final String URL_REG_NICKNAME = urlAddress() + "/game/nick/register";// 用户名注册
	public static final String URL_LOGIN = urlAddress() + "/game/login";// 登录接口
	public static final String URL_BINDTHIRDPARTY = urlAddress() + "/game/bindthirdparty";// 绑定三方账号
	public static final String URL_MODIFY_PWD = urlAddress() + "/game/nick/repassword";// 用户修改密码
	public static final String BIND_PHONE_CODE = urlAddress() + "/game/bindphone/request_verifycode";// 绑定手机号，获取验证码
	public static final String BIND_PHONE = urlAddress() + "/game/bindphone/bindphone";// 绑定手机号
	public static final String URL_CHECK_APPORDERID = urlAddress() + "/game/check_apporderid";// appOrderId验证接口
	public static final String URL_PAY = urlAddress() + "/game/do_pay";// 请求支付接口地址，获取交易参数
	public static final String URL_FORGET_PWD = urlAddress() + "/game/resetpassword/resetpassword";// 忘记密码，重置密码
	// 忘记密码，获取验证码
	public static final String VERIFYCODE_RESETPASSWORD = urlAddress()
			+ "/game/resetpassword/request_verifycode_resetpassword";
	// 统计相关
	public static final String INIT_ANALYTICS = urlAddress() + "/game/do_pay";// sdk初始化,调用统计接口
	public static final String COMMIT_ANALYTICS = urlAddress() + "/game/do_pay";// sdk上报数据
	public static final String URL_CERTIFICATION = urlAddress() + "/game/authenticate";// sdk实名认证

	public static final String URL_LOGIN_BYVERIFYNO = "https://" + INIT_DOMAIN_NAME + "/game/verify/login";// 验证码登录
	public static final String URL_REG_GETVERINO = "https://" + INIT_DOMAIN_NAME + "/onebutton/register";// 请求注册验证码
	public static final String URL_REG = "https://" + INIT_DOMAIN_NAME + "/game/register";// 发送注册验证码
	public static final String URL_TASK_LIST = "https://" + INIT_DOMAIN_NAME + "/game/task/list";// 获取任务列表
	public static final String URL_TASK_BONUS = "https://" + INIT_DOMAIN_NAME + "/game/task/state";// 获取任务积分
	public static final String URL_FLOAT_ORDER = "https://" + INIT_DOMAIN_NAME + "/game/float/order";// 获取悬浮窗顺序
	public static final String URL_FINDPWD_REQUEST_VERIFY = "https://" + INIT_DOMAIN_NAME
			+ "/reset/password/verifycode";// 请求找回密码验证码
	public static final String URL_FINDPWD = "https://" + INIT_DOMAIN_NAME + "/game/reset/password";// 发送新密码和验证码
	public static final String URL_PAY_STANDALONE = "https://" + INIT_DOMAIN_NAME + "/game/do_standalone_pay";// 请求单机支付接口地址
	public static final String URL_PAY_SMS = "https://" + INIT_DOMAIN_NAME + "/game/sms_do_pay";// 请求短信支付接口地址
	public static final String URL_BUY_VIP = "https://" + INIT_DOMAIN_NAME + "/game/member_msg";// 请求是否会员及购买地址接口地址
	public static final String URL_VIPCODE_LIST = "https://" + INIT_DOMAIN_NAME + "/game/member_code";// 请求是否会员码列表接口地址
	public static final String URL_PAY_INPOUR = "https://" + INIT_DOMAIN_NAME + "/game/inpour_and_pay";// 优豆充值并支付接口地址
	public static final String URL_CHECK_BALANCE = "https://" + INIT_DOMAIN_NAME + "/game/get_account_info";// 查询金币余额接口地址
	public static final String URL_CHECK_TASK = "https://" + INIT_DOMAIN_NAME + "/game/user/point";// 查询任务积分接口地址
	public static final String URL_GET_PAY_DETAIL = "http://" + INIT_DOMAIN_NAME + "/game/get_orders";// 查询历史订单接口地址
	public static final String URL_GET_DETAIL_PAY = "http://" + INIT_DOMAIN_NAME + "/game/get_consume_orders";// 查询历史支付订单接口地址
	public static final String URL_GET_DETAIL_RECHARGE = "http://" + INIT_DOMAIN_NAME + "/game/get_charge_orders";// 查询历史充值订单接口地址
	public static final String URL_GET_PACKAGES = "http://" + INIT_DOMAIN_NAME + "/get/packages";// 获取礼包信息地址
	public static final String URL_GET_CONSUME_BENEFITS_MSG = "http://" + INIT_DOMAIN_NAME + "/game/grade/info";// 获取消费福利档位信息
	public static final String URL_GET_CONSUME_BENEFITS_PRESENTS = "http://" + INIT_DOMAIN_NAME + "/game/gift/info";// 获取消费福利礼品列表信息
	public static final String URL_SUBMIT_CONSUME_BENEFITS_PERSONNEL_MSG = "https://" + INIT_DOMAIN_NAME
			+ "/game/gift/user";// 为领取物品提交个人信息
	public static final String URL_LOGIN_COOKIE = "https://" + INIT_DOMAIN_NAME + "/user/refresh/session";// cookie进行登录接口
	public static final String URL_BINDING_REQUEST_VERIFY = "https://" + INIT_DOMAIN_NAME + "/game/nick/sendverify";// 获取绑定验证码
	public static final String URL_BINDING = "https://" + INIT_DOMAIN_NAME + "/game/nick/bindphone";// 绑定手机
	public static final String URL_ANNOUNCEMENT = "http://" + INIT_DOMAIN_NAME + "/game/activities/get";// 活动公告get?appkey=87f3659a17f22f01&guid=9c553730ef5b6c8c542bfd31b5e25b69
	public static final String URL_BENIFIT = "https://" + INIT_DOMAIN_NAME + "/get/login/vip/code";// 查询是否有福利
	public static final String URL_GET_ACCESS_TOKEN = "https://" + INIT_DOMAIN_NAME + "/game/yktk_token_exchange";// 获取access_token

	public static final String URL_STATISTIC_EXCEPTION = INIT_DOMAIN_NAME
			+ "/openapi-wireless/statis/sdkgames/exception";// 未处理异常埋点

	public static final String URL_STATISTIC_ANNOUNCEMENT_CLICK = INIT_DOMAIN_NAME
			+ "/openapi-wireless/statis/sdkgames/operations";// 活动点击统计埋点地址
	public static final String URL_STATISTIC_BUY_VIP = INIT_DOMAIN_NAME + "/openapi-wireless/statis/sdkgames/members";// 会员购买统计埋点地址
	public static final String URL_STATISTIC_VIDEO = INIT_DOMAIN_NAME
			+ "/openapi-wireless/statis/sdkgames/recordingvideo";
	public static final String AGREEMENT_URL = "http://www.youku.com/pub/youku/service/agreement.shtml ";
	public static final String URL_GOLD_PROTO = "http://pay.youku.com/inpouragreement.html";// 金币消费协议
	public static final String URL_OPERATOR_PROTO = "http://" + INIT_DOMAIN_NAME
			+ "/game/topupcard.html?qq-pf-to=pcqq.c2c";// 运营商充值卡了解更多
}
