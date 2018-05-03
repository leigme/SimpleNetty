package me.leig.simplenetty.handler;

import io.netty.channel.ChannelHandlerContext;
import me.leig.simplenetty.bean.CtxData;
import me.leig.simplenetty.bean.NettyMessage;
import me.leig.simplenetty.comm.Constant;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 客户端消息发送监听抽象类
 *
 * @author leig
 *
 */
public enum ClientListener implements MessageListener {

    INSTANCE;

    ClientListener() {}

    private static Logger log = Logger.getLogger(ClientListener.class);

    // 客户端信息
    private CtxData mCtxData;

    // 服务器端信息
    private CtxData sCtxData;

    // 用户列表
    private List<CtxData> users;

    // 消息接收机回调接口
    private ContentHandler mContentHandler;

    public void updateUserList(List<CtxData> userDatas) {
        users = userDatas;
    }

    @Override
    public void sendMessage(NettyMessage nettyMessage) {
        sCtxData.getCtx().channel().writeAndFlush(nettyMessage);
    }

    @Override
    public void receiveMessage(int senderId, String message) {
        log.info(senderId + " 说了 " + message);
    }


    @Override
    public void connectSuccess() {
        NettyMessage nettyMessage = new NettyMessage();
        nettyMessage.setSenderId(mCtxData.getUserId());
        nettyMessage.setMsgType(Constant.MSG_TYPE_FIRST);
        nettyMessage.setData(mCtxData.getLocalIP().getBytes());
        sendMessage(nettyMessage);
    }

    @Override
    public void connectFailure(NettyMessage nettyMessage) {

    }

    @Override
    public void disconnect(ChannelHandlerContext ctx) {

    }

    /**
     * 客户端给服务器发送消息
     *
     * @param message
     *
     */
    public void toServer(String message) {
        NettyMessage nettyMessage = new NettyMessage();
        nettyMessage.setSenderId(mCtxData.getUserId());
        nettyMessage.setData(message.getBytes());
        sendMessage(nettyMessage);
    }

    /**
     * 客户端给客户端发送消息
     *
     * @param userId
     * @param message
     *
     */
    public void toClient(int userId, String message) {
        NettyMessage nettyMessage = new NettyMessage();
        nettyMessage.setSenderId(mCtxData.getUserId());
        nettyMessage.setReceiverId(String.valueOf(userId));
        nettyMessage.setData(message.getBytes());
        sendMessage(nettyMessage);
    }

    /**
     * 客户端给客户端集合发送消息
     *
     * @param userIds
     * @param message
     */
    public void toClients(int[] userIds, String message) {
        for (int userId: userIds) {
            toClient(userId, message);
        }
    }

    public CtxData getCtxData() {
        return mCtxData;
    }

    public void setCtxData(CtxData ctxData) {
        mCtxData = ctxData;
    }

    public CtxData getsCtxData() {
        return sCtxData;
    }

    public void setsCtxData(CtxData sCtxData) {
        this.sCtxData = sCtxData;
    }

    /*    *//**
     * 获取服务器注册的用户集合
     *//*
    public void obtainUsers() {
        if (null != sCtxData && null != sCtxData.getCtx()) {
            NettyMessage nm = new NettyMessage();
            nm.setSenderId(mCtxData.getUserId());
            nm.setMsgType(Constant.MSG_TYPE_USERLIST);
            nm.setreceiverId(sCtxData.getUserId());
            nm.setData("获取用户列表".getBytes());
            sCtxData.getCtx().channel().writeAndFlush(nm);
        }
    }

    *//**
     * 向服务器发送一条消息
     *
     * @param userId
     * @param receiverId
     * @param msgType
     * @param msg
     *//*
    @Override
    public void sendMessage(String userId, String receiverId, int msgType, String msg) {
        if (null == sCtxData) {
            log.error(userId + "未注册...");
            return;
        }
        ChannelHandlerContext receiverCtx = sCtxData.getCtx();
        if (null != receiverCtx) {
            NettyMessage nettyMessage = new NettyMessage(userId,
                    receiverId, msgType, msg.getBytes());
            receiverCtx.channel().writeAndFlush(nettyMessage);
            return;
        }
        log.error(receiverId + "离线了...");
    }

    public void sendMessage(String receiverId, int msgType, String msg) {
        this.sendMessage(mCtxData.getUserId(), receiverId, msgType, msg);
    }

    public void sendMessage(String receiverId, String msg) {
        this.sendMessage(receiverId, Constant.MSG_TYPE_TEXT, msg);
    }

    public void sendMessage(String msg) {
        this.sendMessage("", msg);
    }

    @Override
    public void sendMessage(NettyMessage nettyMessage) {

    }

    *//**
     * 接收服务器消息的回调处理方法
     *
     * @param nettyMessage
     *//*
    @Override
    public void receiveMessage(NettyMessage nettyMessage) {

        if (null == sCtxData) {
            log.error("未注册...");
            return;
        }

        switch (nettyMessage.getMsgType()) {
            case Constant.MSG_TYPE_USERLIST:
                String msg = new String(nettyMessage.getData());
                log.info(msg);
                String[] strs = msg.split(Constant.CONN);
                if (0 < strs.length) {
                    for (String str: strs) {
                        userIds.add(str.split(Constant.SEG)[0]);
                    }
                    mContentHandler.obtainUsers(userIds);
                }
                break;
            case Constant.MSG_TYPE_TEXT:
                if (null != mContentHandler) {
                    mContentHandler.receiveContent(new String(nettyMessage.getData()));
                }
                sendMessage(mCtxData.getUserId(), sCtxData.getUserId(), Constant.MSG_TYPE_REPLY, "收到");
                break;
            default:
                log.info("消息格式未定义: " + new String(nettyMessage.getData()));
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

    *//**
     * 保存服务器信息
     *
     * @param ctxData
     *//*
    public void saveCtxData(CtxData ctxData) {
        this.sCtxData = ctxData;
    }

    *//**
     * 同服务器端断开连接监听方法
     *
     * @param ctx
     *//*
    @Override
    public void endMessage(ChannelHandlerContext ctx) {
        log.info("与服务器断开连接了...");
        mContentHandler.endConnect("与服务器断开连接了...");
    }

    public CtxData getServerCtxData() {
        return sCtxData;
    }

    public void setServerCtxData(CtxData sCtxData) {
        this.sCtxData = sCtxData;
    }

    public CtxData getCtxData() {
        return mCtxData;
    }

    public void setCtxData(CtxData ctxData) {
        mCtxData = ctxData;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }*/
}
