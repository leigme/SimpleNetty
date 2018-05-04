package me.leig.simplenetty.handler;

import io.netty.channel.ChannelHandlerContext;
import me.leig.simplenetty.bean.CtxData;
import me.leig.simplenetty.bean.NettyMessage;
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

    private ConnectListener mConnectListener;

    public final void updateUserList(List<CtxData> userDatas) {

        users = userDatas;
    }

    @Override
    public final void sendMessage(NettyMessage nettyMessage) {
        sCtxData.getCtx().channel().writeAndFlush(nettyMessage);
    }

    @Override
    public void receiveMessage(int senderId, String message) {
        log.info(senderId + " 说了 " + message);
    }

    public void disconnect(ChannelHandlerContext ctx) {
        log.info("disconnect() run");
    }

    /**
     * 客户端给服务器发送消息
     *
     * @param message
     *
     */
    public final void toServer(String message) {
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
    public final void toClient(int userId, String message) {
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
    public final void toClients(int[] userIds, String message) {
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

    public ConnectListener getConnectListener() {
        return mConnectListener;
    }

    public void setConnectListener(ConnectListener connectListener) {
        mConnectListener = connectListener;
    }
}
