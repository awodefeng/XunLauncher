package com.xxun.xunlauncher;

import com.xiaoxun.sdk.utils.Constant;

/**
 * @author lihaizhou
 * @createtime 2017.12.03
 * @class describe XunLauncher常量类
 */

public class Constants {

    /*
    *待机界面相关
    * */
    public static final String FUNCTIONLIST = "functionlist";
    public static final int LAUNCHER_PAGE_MAX_SUM = 100000;
    public static int LAUNCHER_PAGE_DEFAULT_TIMES = 5000;
    public static final int VERTICAL_CLOCK_INDEX = 0;
    public static final int HORIZE_CLOCK_INDEX = 1;
    public static final int ANALOG_CLOCK_INDEX = 2;
    public static final int SQUARE_ANALOG_CLOCK_INDEX = 3;
    public static final int ROUND_ANALOG_CLOCK_INDEX = 4;
    public static final int LADYBIRD_ANALOG_CLOCK_INDEX = 5;
    
    public static final int EARTH_ONE = 6;
    public static final int EARTH_TWO = 7;
    public static final int EARTH_THTEE = 8;
    public static final int MOTION_STYLE = 10;
    public static final int TRACK_STYLE=11;
    public static final int XUNMOTION_STYLE=12;
    
    //weather releated
    public static final int WEATHER_INDICATOR_SUM = 3;
    //volte releated begin
    public static final String EXTRA_IMS_REG_STATE_KEY = "android:regState";
    public static final int STATE_OUT_OF_SERVICE = 1;

    //broadcast releated
    public static final String ACTION_LOGIN_SUCCESS = "com.xiaoxun.sdk.action.LOGIN_OK";
    public static final String ACTION_IMS_STATE_CHANGED = "com.android.ims.IMS_STATE_CHANGED";
    public static final String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    public static final String ACTION_LOGIN_SUCC = "com.xiaoxun.sdk.action.LOGIN_OK";
    public static final String ACTION_SESSION_PING_OK = "com.xiaoxun.sdk.action.SESSION_OK";
    public static final String ACTION_NET_SWITCH_SUCC = "com.xiaoxun.sdk.action.SWITCH_SUCC";
    public static final String ACTION_XIAOXUN_SEND_SIM_CHANGE = "com.xunlauncher.sim.change";
    public static final String ACTION_XIAOXUN_AIRPLAN_OFF = "com.xunlauncher.airplan.off";
    public static final String NOTIFY_LAUNCHER_CHANGE= "notify_launcher_change";
    public static final String SEND_SOS_ACTION = "com.xunlauncher.sos";
    //PowerKey releated
    public static final String SHOW_RANDOMCODE_ACTION = "com.xunlauncher.randomcode";
    public static final String GOTO_HOMESCREEN_ACTION = "com.xunlauncher.homescreen";
    
    
    //视频通话过程中，如遇高温，通知应用赶紧保存统计数据，以免被杀.
    public static final String TELL_VIDEO_TO_KEEP_DATA = "com.video.keep.data";   
    
    //Prevent_addition
    public static final String XIAOXUN_PREVENT_ADDITION = "com.xiaoxun.prevent.addition";
    public static final String XIAOXUN_CLEAR_RAM_MEMORY = "com.xiaoxun.clear.memory";

    //*/接收讲故事后台播放状态广播
    public static final String ACTION_BROAST_MEDIA_SONG_STATUS = "brocast.action.media.song.status";
    public static final String ACTION_BROAST_MEDIA_SONG_STATUS_REQ = "brocast.action.media.song.status.req";
    public static final String ACTION_BROAST_STORY_FINISH = "com.xiaoxun.xxun.story.finish";
    //*/

    //yanbing add high temperature 20180830 start
    public static final String XIAOXUN_ACTION_TEMPER_HIGH ="com.xiaoxun.xxun.high.temperature";
    public static final String XIAOXUN_ACTION_FORCE_STOP_PACKAGE="com.xiaoxun.xxun.stop.package";
    public static final String XIAOXUN_ACTION_END_CALL="com.xiaoxun.action.end.call";
    //yanbing add high temperature 20180830 end
    
    //*/灭屏后使用alarm间隔一段时间读取主板温度
    public static final String XUN_PRODIC_READ_AP_TEMPER = "xunprodic.read.aptemper";
    //*/

    //*/上传的主板温度值范围
    public static final int XUN_TEMPER_OVERHEAT = Constant.PROJECT_NAME.equals("SW707") ? 500 : 470;//BATTERY_HEALTH_OVERHEAT = 3
    public static final int XUN_TEMPER_HEAT = Constant.PROJECT_NAME.equals("SW707") ? 500 : 440;//BATTERY_HEALTH_COLD = 7
    public static final int XUN_TEMPER_NORMALHEAT = 2;//BATTERY_HEALTH_GOOD = 2
    //*/

    /*
    * 网络类型相关
    * */
    public static final String NETWORK_WIFI = "wifi";
    public static final String NETWORK_VOLTE = "4G HD";
    public static final String NETWORK_4G = "4G";
    public static final String NETWORK_3G = "3G";
    public static final String NETWORK_2G = "2G";
    public static final String NETWORK_UNAVIABLE = "unaviable";
    
    /*
    * close wifi
    * */
    public static final int WIFI_CLOSE_TIME = 15;
    public static final String KEEP_WIFI_CONNECT = "com.xxun.xunlauncher.action.KEEP_WIFI_CONNECT";
    
    /*
    * alert dialog releated
    * */
    public static final int SIM_ALERT_DIALOG = 1;
    public static final int SERVER_LOGIN_FAIL = 2;
    public static final int QQ_NOTICE_DIALOG = 3;

    /*
    * QQ Msg releated
    * */
    public static final String NOTIFICATION_TYPE_ADDFRIEND = "1";
    public static final String NOTIFICATION_TYPE_MESSAGE = "2";
    public static final String NOTIFICATION_TYPE_ADDFRIEND_RESULT = "3";
    public static final String ACTION_REPLY = "com.tencent.qq.action.conversation.reply";
    public static final String ACTION_ACCEPT = "com.tencent.qq.action.addFriend.accept";
    public static final String ACTION_REJECT = "com.tencent.qq.action.addFriend.reject";
    public static final String BROADCAST_NEW_MESSAGE_NOFITY_WATCH = "com.tencent.qqlite.watch.conversation";


    /*
    * 时钟相关
    * */
    public static final String CLOCK_TWELVE_FORMATE = "12";
    public static final String CLOCK_FORMATE_NAME = "clock_formate";

    /*
    * 绑定相关
    * */
    public static final String BIND_REQUEST_FROM_SERVER = "com.xunlauncher.bindrequest";
    public static final String UNBIND_REQUEST_FROM_SERVER = "com.xunlauncher.unbindrequest";
    public static final String BIND_SUCCESS_FROM_SERVER = "com.xunlauncher.bindsuccess";
    public static final String BIND_CONFIRM_ACTION = "com.xunlauncher.confirmbind";

    //shutdown releated
    public static final long SHUTDOWN_TOTAL_TIME = 3600000;
    public static final long SHUTDOWN_INTERVAL = 1000;
    /**
     * 电信运营商
     */
    public static final String CHINA_TELECOM[] = { "46011" , "46003" ,"46005" };


	/**
	jifen
	*/

    public static final String AUTOHORITY = "com.xxun.myprovider";
    public static final int OPENBOX =0; //开宝箱    0
    public static final int SUNSTEP = 1; //计步达标 1
    public static final int RUNKILOMETRE = 2;  //跑1公里  2
    public static final int BUSCARD =3;//刷公交 3
    public static final int MAKEFRIENDS =4; //添加好友 4
    public static final int PHOTOLEARNREAD = 5;//拍照识字 5
    public static final int PHOTOLEARNPICTURE = 6;//拍照识图 6
    public static final int USELITTLELOVE =7;//使用小爱同学 7

    public static final int RELEASEWECHAT = 8;//发布朋友圈 8
    public static final int LIKESWECHAT = 9;//点赞朋友圈      9
    public static final int COMMENTWECHAT =10;//评论朋友圈 10

    public static final int ENGLISHSTUDY = 11;//英语学习完成 11
    public static final int ENGLISHPASS =12;//英语测试通过    12
    public static final int OTAUPDATE = 13;//升级固件 13

    public static final int LASTBOX = 14 ;//中级宝箱 14

    public static final String OPENBOX_China ="开宝箱    "; //开宝箱    0
    public static final String SUNSTEP_China = "计步达标"; //计步达标 1
    public static final String RUNKILOMETRE_China  = "跑一公里";  //跑1公里  2
    public static final String BUSCARD_China ="刷公交    ";//刷公交 3
    public static final String MAKEFRIENDS_China ="添加好友"; //添加好友 4
    public static final String PHOTOLEARNREAD_China = "拍照识字";//拍照识字 5
    public static final String PHOTOLEARNPICTURE_China = "拍照识图";//拍照识图 6
    public static final String USELITTLELOVE_China ="使用小爱";//使用小爱同学 7

    public static final String RELEASEWECHAT_China = "发朋友圈";//发布朋友圈 8
    public static final String LIKESWECHAT_China = "点朋友圈";//点赞朋友圈      9
    public static final String COMMENTWECHAT_China = "评朋友圈";//评论朋友圈 10

    public static final String ENGLISHSTUDY_China = "英语学习";//英语学习完成 11
    public static final String ENGLISHPASS_China = "英语测试";//英语测试通过    12
    public static final String OTAUPDATE_China = "升级固件";//升级固件 13

    public static final String LASTBOX_China = "终级宝箱" ;//中级宝箱 14

    public static final String ExchangeDial_China = "兑换表盘"; //兑换表盘 15

  //是否弹窗状态
    public static final String isOPENBOX_China = "isOPENBOX_China"; //是否开宝箱
    public static final String isSUNSTEP_China = "isSUNSTEP_China"; //计步达标 弹窗 1
    public static final String isRUNKILOMETRE_China  = "isRUNKILOMETRE_China"; //跑一公里弹窗 2
    public static final String isBUSCARD = "isBUSCARD";//刷公交弹窗  3
    public static final String isMAKEFRIENDS ="isMAKEFRIENDS"; //添加好友弹窗  4
    public static final String isPHOTOLEARNREAD = "isPHOTOLEARNREAD";//拍照识字弹窗  5
    public static final String isPHOTOLEARNPICTURE = "isPHOTOLEARNPICTURE";//拍照识图 6
    public static final String isUSELITTLELOVE = "isUSELITTLELOVE";//使用小爱同学 7
    public static final String isRELEASEWECHAT = "isRELEASEWECHAT";//发布朋友圈 8
    public static final String isLIKESWECHAT = "isLIKESWECHAT";//点赞朋友圈      9
    public static final String isCOMMENTWECHAT = "isCOMMENTWECHAT";//评论朋友圈 10
    public static final String isENGLISHSTUDY = "isENGLISHSTUDY";//英语学习完成 11
    public static final String isENGLISHPASS = "isENGLISHPASS";//英语测试通过    12
    public static final String isOTAUPDATE = "isOTAUPDATE";//升级固件 13



}
