package me.leig.simplenetty.handler;

import io.netty.channel.ChannelHandlerContext;
import me.leig.simplenetty.bean.CtxData;
import me.leig.simplenetty.bean.NettyMessage;
import me.leig.simplenetty.comm.Constant;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端消息发送监听抽象类
 *
 * @author leig
 */
public abstract class ClientListener implements MessageListener {

    private static Logger log = Logger.getLogger(ClientListener.class);

    // 客户端信息
    private CtxData mCtxData;

    // 服务器端信息
    private CtxData serverCtxData;

    // 用户列表
    private List<String> userIds;

    // 消息接收机回调接口
    private ContentHandler mContentHandler;

    public ClientListener(CtxData ctxData, ContentHandler contentHandler) {
        this.mCtxData = ctxData;
        this.mContentHandler = contentHandler;
        userIds = new ArrayList<>();
    }

    /**
     * 获取服务器注册的用户集合
     */
    public void obtainUsers() {
        if (null != serverCtxData && null != serverCtxData.getCtx()) {
            NettyMessage nm = new NettyMessage();
            nm.setSenderId(mCtxData.getUserId());
            nm.setMsgType(Constant.MSG_TYPE_USERLIST);
            nm.setreceiverId(serverCtxData.getUserId());
            nm.setData("获取用户列表".getBytes());
            serverCtxData.getCtx().channel().writeAndFlush(nm);
        }
    }

    /**
     * 向服务器发送一条消息
     *
     * @param userId
     * @param receiverId
     * @param msgType
     * @param msg
     */
    @Override
    public void sendMessage(String userId, String receiverId, int msgType, String msg) {
        if (null == serverCtxData) {
            log.error(userId + "未注册...");
            return;
        }
        ChannelHandlerContext receiverCtx = serverCtxData.getCtx();
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

    /**
     * 接收服务器消息的回调处理方法
     *
     * @param nettyMessage
     */
    @Override
    public void receiveMessage(NettyMessage nettyMessage) {

        if (null == serverCtxData) {
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
                sendMessage(mCtxData.getUserId(), serverCtxData.getUserId(), Constant.MSG_TYPE_REPLY, "收到");
                break;
            default:
                log.info("消息格式未定义: " + new String(nettyMessage.getData()));
                break;
        }
    }

    /**
     * 保存服务器信息
     *
     * @param ctxData
     */
    public void saveCtxData(CtxData ctxData) {
        this.serverCtxData = ctxData;
    }

    /**
     * 同服务器端断开连接监听方法
     *
     * @param ctx
     */
    @Override
    public void endMessage(ChannelHandlerContext ctx) {
        log.info("与服务器断开连接了...");
        mContentHandler.endConnect("与服务器断开连接了...");
    }

    public CtxData getServerCtxData() {
        return serverCtxData;
    }

    public void setServerCtxData(CtxData serverCtxData) {
        this.serverCtxData = serverCtxData;
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
    }
}
