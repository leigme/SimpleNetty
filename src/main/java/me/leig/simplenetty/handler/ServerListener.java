package me.leig.simplenetty.handler;

import io.netty.channel.ChannelHandlerContext;
import me.leig.simplenetty.bean.CtxData;
import me.leig.simplenetty.bean.NettyMessage;
import me.leig.simplenetty.comm.Constant;
import org.apache.log4j.Logger;

import java.util.ArrayList;
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

    // 服务器自身信息
    private CtxData mCtxData;

    // 客户端信息集合
    private static Map<String, CtxData> mMap = new ConcurrentHashMap<>();

    private ConnectListener mConnectListener;

    @Override
    public final void sendMessage(NettyMessage nettyMessage) {
        CtxData ctxData = mMap.get(nettyMessage.getReceiverId());
        if (null == ctxData) {
            log.error(nettyMessage.getReceiverId() + " is not connect");
            return;
        }
        ctxData.getCtx().channel().writeAndFlush(nettyMessage);
    }

    @Override
    public void receiveMessage(int userId, String message) {
        log.info(userId + " 说了: " + message);
    }

    public void disconnect(ChannelHandlerContext ctx) {
        String msg = "";
        // 更新用户存储列表
        for (Map.Entry<String, CtxData> entry : mMap.entrySet()) {
            if (ctx == entry.getValue().getCtx()) {
                msg = entry.getValue().getUserId()
                        + Constant.SEG
                        + entry.getValue().getUserName()
                        + Constant.SEG
                        + entry.getValue().getLocalIP()
                        + Constant.SEG
                        + entry.getValue().getTime()
                        + Constant.SEG
                        + entry.getValue().getRemark();
                mConnectListener.removeUserInfo(entry.getValue());
                mMap.remove(entry.getKey());
                log.info(entry.getKey() + " disconnect");
            }
        }

        // 通知其他用户离线用户信息
        for (Map.Entry<String, CtxData> entry: mMap.entrySet()) {
            NettyMessage nettyMessage = new NettyMessage();
            nettyMessage.setMsgType(Constant.MSG_TYPE_USERLIST);
            nettyMessage.setReceiverId(entry.getKey());
            nettyMessage.setData(msg.getBytes());
            sendMessage(nettyMessage);
        }

        // 通知其他用户更新用户列表
        for (Map.Entry<String, CtxData> entry: mMap.entrySet()) {
            getUserList(Integer.parseInt(entry.getKey()));
        }
        log.info("disconnect() run");
    }

    public final void toClient(int receiverId, String message) {
        CtxData ctxData = mMap.get(String.valueOf(receiverId));
        if (null == ctxData) {
            log.error(receiverId + " is not connect");
            return;
        }
        NettyMessage nettyMessage = new NettyMessage();
        nettyMessage.setReceiverId(String.valueOf(receiverId));
        nettyMessage.setData(message.getBytes());
        ctxData.getCtx().channel().writeAndFlush(nettyMessage);
    }

    public final void toClients(String message) {
        for (Map.Entry<String, CtxData> entry : mMap.entrySet()) {
            String user = entry.getKey();
            toClient(Integer.parseInt(user), message);
        }
    }

    public final void clientToClient(int senderId, int receiverId, String message) {
        NettyMessage nettyMessage = new NettyMessage();
        nettyMessage.setSenderId(String.valueOf(senderId));
        nettyMessage.setReceiverId(String.valueOf(receiverId));
        nettyMessage.setData(message.getBytes());
        sendMessage(nettyMessage);
    }

    public final void saveCtxData(CtxData ctxData) {
        if (0 < mMap.size()) {
            String msg = ctxData.getUserId()
                    + Constant.SEG
                    + ctxData.getUserName()
                    + Constant.SEG
                    + ctxData.getLocalIP()
                    + Constant.SEG
                    + ctxData.getTime()
                    + Constant.SEG
                    + ctxData.getRemark();
            for (Map.Entry<String, CtxData> entry : mMap.entrySet()) {
                NettyMessage nettyMessage = new NettyMessage();
                nettyMessage.setMsgType(Constant.MSG_TYPE_ADDUSER);
                nettyMessage.setReceiverId(entry.getValue().getUserId());
                nettyMessage.setData(msg.getBytes());
                sendMessage(nettyMessage);
            }
        }
        mConnectListener.addUserInfo(ctxData);
        mMap.put(ctxData.getUserId(), ctxData);
        for (Map.Entry<String, CtxData> entry : mMap.entrySet()) {
            getUserList(Integer.parseInt(entry.getKey()));
        }
    }

    public final void getUserList(int userId) {
        CtxData ctxData = mMap.get(String.valueOf(userId));
        if (null == ctxData) {
            log.error(userId + " is not connect");
            return;
        }
        NettyMessage nettyMessage = new NettyMessage();
        nettyMessage.setMsgType(Constant.MSG_TYPE_USERLIST);
        nettyMessage.setReceiverId(String.valueOf(userId));
        StringBuilder sb = new StringBuilder();
        List<CtxData> ctxDataList = new ArrayList<>();
        for (Map.Entry<String, CtxData> entry : mMap.entrySet()) {
            String user = entry.getKey();
            String userName = entry.getValue().getUserName();
            sb.append(user)
                    .append(Constant.SEG)
                    .append(userName)
                    .append(Constant.SEG)
                    .append(entry.getValue().getLocalIP())
                    .append(Constant.SEG)
                    .append(entry.getValue().getTime())
                    .append(Constant.SEG)
                    .append(entry.getValue().getRemark())
                    .append(Constant.CONN);
            ctxDataList.add(entry.getValue());
        }
        nettyMessage.setData(sb.toString().getBytes());
        sendMessage(nettyMessage);
        mConnectListener.getUserList(ctxDataList);
    }

    public void setCtxData(CtxData ctxData) {
        this.mCtxData = ctxData;
    }

    public CtxData getCtxData() {
        return mCtxData;
    }

    public ConnectListener getConnectListener() {
        return mConnectListener;
    }

    public void setConnectListener(ConnectListener connectListener) {
        mConnectListener = connectListener;
    }
}
