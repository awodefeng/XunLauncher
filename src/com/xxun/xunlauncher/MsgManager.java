package com.xxun.xunlauncher;

import java.util.HashMap;
import java.util.Set;
import android.os.Message;
import android.os.Handler;

/**
 * @author lihaizhou
 * @createtime 2019.03.12
 * @class describe 事件的核心处理类，用于解耦，降低页面间的依赖
 */

public class MsgManager {

    private Class receiveClass;
    //存储所有的订阅者
    private HashMap<String,OnMsgListener> listenersHashMap = new HashMap<String,OnMsgListener>();//4.4 must need String,OnMsgListener in <>

    private MsgHandler msgHandler = new MsgHandler();
    private static final int SEND_TO_ALL = 0;
    private static final int SEND_TO_SINGLE = 1;

    private MsgManager(){}

    public static MsgManager getMsgManagerInstance(){
        return MsgManagerHolder.msgManagerInstance;
    }

    private static class MsgManagerHolder{
        public static MsgManager msgManagerInstance = new MsgManager();
    }
    
    public interface OnMsgListener{
        void onMsg(String msg);
    }

    /**
     * @author lihaizhou
     * @createtime 2019.03.12
     * @class describe 收到sendMsg的请求后，给订阅者发消息，这里分为是否发给指定类
     */
    private class MsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_TO_SINGLE:
                    if(receiveClass != null){
                        listenersHashMap.get(receiveClass.getName()).onMsg(msg.obj.toString());
                        receiveClass = null;
                    }
                    break;
                case SEND_TO_ALL:
                    Set<String> keys = listenersHashMap.keySet();
                    for (String key : keys) {
                        listenersHashMap.get(key).onMsg(msg.obj.toString());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * @author lihaizhou
     * @Description: 发送消息，适合一对多场景
     * @param: String类型的消息
     */
    public void sendMsg(String msg){
        msgHandler.sendMessage(msgHandler.obtainMessage(SEND_TO_ALL, msg));
    }

    /**
     * @author lihaizhou
     * @Description: 发送消息，一对一场景
     * @param: 指定接收消息的类名, String类型的消息
     */
    public void sendMsgToSingleClass(Class receiveClass,String msg){
        this.receiveClass = receiveClass;
        msgHandler.sendMessage(msgHandler.obtainMessage(SEND_TO_SINGLE, msg));
    }

    public void registerListener(Class className,OnMsgListener msgListener){
        listenersHashMap.put(className.getName(),msgListener);
    }

    public void unregisterlistener(Class className){
        listenersHashMap.remove(className.getName());
    }

}
