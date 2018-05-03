package me.leig.simplenetty.handler;


import io.netty.channel.ChannelHandlerContext;
import me.leig.simplenetty.bean.CtxData;
import me.leig.simplenetty.bean.NettyMessage;
import me.leig.simplenetty.comm.Constant;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端消息发送监听抽象类
 *
 * @author leig
 *
 */
public enum ServerListener implements MessageListener {

    INSTANCE;

    ServerListener() {}

    private static Logger log = Logger.getLogger(ServerListener.class);

    // 服务器信息
    private CtxData mCtxData;

    // 客户端信息集合
    private static Map<String, CtxData> mMap = new ConcurrentHashMap<>();

    @Override
    public void sendMessage(NettyMessage nettyMessage) {

    }

    @Override
    public void receiveMessage(NettyMessage nettyMessage) {
        switch (nettyMessage.getMsgType()) {
            case Constant.MSG_TYPE_FIRST:
                serverToClients();
                break;
            case Constant.MSG_TYPE_HEARTBEAT:
                serverToClients();
                break;
            case Constant.MSG_TYPE_TEXT:

                if (null == nettyMessage.getreceiverId() || "".equals(nettyMessage.getreceiverId())) {
                    clientToServer();
                    return;
                }

                if (null == mMap.get(nettyMessage.getreceiverId())) {
                    serverToClients();
                    return;
                }

                clientToClients();
                break;
        }
    }

    @Override
    public void connectSuccess(NettyMessage nettyMessage) {

    }

    @Override
    public void connectFailure(NettyMessage nettyMessage) {

    }

    @Override
    public void disconnect(ChannelHandlerContext ctx) {

    }

    void serverToClients() {}

    void clientToServer() {}

    void clientToClients() {}











    /*
    *//**
     * 向客户端发送一条消息
     *
     * @param userId
     * @param receiverId
     * @param msgType
     * @param msg
     *//*
    @Override
    public void sendMessage(String userId, String receiverId, int msgType, String msg) {
        if (null == mMap.get(receiverId)) {
            log.error(receiverId + "未注册...");
            return;
        }
        ChannelHandlerContext receiverCtx = mMap.get(receiverId).getCtx();
        if (null != receiverCtx) {
            NettyMessage nettyMessage = new NettyMessage(userId,
                    receiverId, msgType, msg.getBytes());
            receiverCtx.channel().writeAndFlush(nettyMessage);
            return;
        }
        log.error(receiverId + "离线了...");
    }

    public void sendMessage(String userId, List<String> receiverIds, String msg) {
        for (String receiverId: receiverIds) {
            this.sendMessage(userId, receiverId, Constant.MSG_TYPE_TEXT, msg);
        }
    }

    public void sendMessage(List<String> receiverIds, String msg) {
        this.sendMessage(mCtxData.getUserId(), receiverIds, msg);
    }

    public void sendMessage(String userId, String msg) {
        List<String> receiverIds = new ArrayList<>();
        for (Map.Entry<String, CtxData> entry: mMap.entrySet()) {
            receiverIds.add(entry.getKey());
        }
        this.sendMessage(userId, receiverIds, msg);
    }

    public void sendMessage(String msg) {
        this.sendMessage(mCtxData.getUserId(), msg);
    }

    *//**
     * 接收客户端发来的消息
     *
     * @param nettyMessage
     *//*
    @Override
    public void receiveMessage(NettyMessage nettyMessage) {
        switch (nettyMessage.getMsgType()) {
            case Constant.MSG_TYPE_REPLY:
                mContentHandler.replay(nettyMessage.getSenderId());
                break;
            case Constant.MSG_TYPE_USERLIST:
                if (0 < mMap.size() && null != nettyMessage.getSenderId() && !"".equals(nettyMessage.getSenderId())) {
                    Iterator<Map.Entry<String, CtxData>> iterator = mMap.entrySet()
                            .iterator();
                    StringBuilder sb = new StringBuilder();
                    while (iterator.hasNext()) {
                        Map.Entry<String, CtxData> entry = iterator.next();
                        if (!entry.getKey().equals(nettyMessage.getSenderId())) {
                            sb.append(entry.getKey()).append(Constant.CONN);
                        }
                    }
                    NettyMessage nm = new NettyMessage(mCtxData.getUserId(),
                            nettyMessage.getSenderId(),
                            Constant.MSG_TYPE_TEXT,
                            sb.toString().getBytes());
                    mMap.get(nettyMessage.getSenderId()).getCtx().channel().writeAndFlush(nm);
                }
                break;
            case Constant.MSG_TYPE_TEXT:
                if (null != nettyMessage.getSenderId() && !"".equals(nettyMessage.getSenderId())) {
                    String receiverId = nettyMessage.getreceiverId();
                    if (null != receiverId && !"".equals(receiverId) && !mCtxData.getUserId().equals(receiverId)) {
                        sendMessage(nettyMessage.getSenderId(), receiverId, Constant.MSG_TYPE_TEXT, new String(nettyMessage.getData()));
                    } else if (mCtxData.getUserId().equals(receiverId)){
                        log.info("客户端发送给服务器端的消息: " + new String(nettyMessage.getData()));
                        if (null != mContentHandler) {
                            mContentHandler.receiveContent(new String(nettyMessage.getData()));
                        }
                    } else {
                        sendMessage(nettyMessage.getSenderId(), new String(nettyMessage.getData()));
                    }
                }
                break;
            default:
                log.info("消息格式未定义: " + new String(nettyMessage.getData()));
                break;
        }
    }

    @Override
    public void connectSuccess() {

    }

    @Override
    public void connectFailure() {

    }

    *//**
     * 保存客户端信息
     *
     * @param ctxData
     *//*
    public void saveCtxData(CtxData ctxData) {

        mMap.put(ctxData.getUserId(), ctxData);

        Iterator<Map.Entry<String, CtxData>> iterator = mMap.entrySet()
                .iterator();
        List<String> userIds = new ArrayList<>();

        while (iterator.hasNext()) {
            Map.Entry<String, CtxData> entry = iterator.next();
            userIds.add(entry.getKey());
        }

        mContentHandler.addUser(ctxData.getUserId());
        mContentHandler.obtainUsers(userIds);
    }

    *//**
     * 同客户端断开连接的方法
     *
     * @param ctx
     *//*
    @Override
    public void endMessage(ChannelHandlerContext ctx) {
        String userId = null;
        for (Map.Entry<String, CtxData> entry: mMap.entrySet()) {
            if (ctx == entry.getValue().getCtx()) {
                userId = entry.getKey();
            }
        }
        if (null != userId) {
            for (Map.Entry<String, CtxData> entry: mMap.entrySet()) {
                if (ctx != entry.getValue()) {
                    String msg = "[" + userId + "]离线了";
                    NettyMessage nm = new NettyMessage(mCtxData.getUserId(), entry.getKey(), Constant.MSG_TYPE_OFFLINE, msg.getBytes());
                    entry.getValue().getCtx().channel().writeAndFlush(nm);
                }
            }
            mMap.remove(userId);
            log.info("[" + userId + "]离线了");
            mContentHandler.endConnect("[" + userId + "]离线了");
        }
    }

    public CtxData getCtxData() {
        return mCtxData;
    }

    public void setCtxData(CtxData ctxData) {
        mCtxData = ctxData;
    }

    public static Map<String, CtxData> getmMap() {
        return mMap;
    }

    public static void setmMap(Map<String, CtxData> mMap) {
        ServerListener.mMap = mMap;
    }*/
}
