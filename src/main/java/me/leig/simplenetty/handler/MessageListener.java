package me.leig.simplenetty.handler;

import io.netty.channel.ChannelHandlerContext;
import me.leig.simplenetty.bean.NettyMessage;

/**
 * @author leig
 * @version 20170301
 */
public interface MessageListener {

    /**
     * 发送消息
     *
     * @param userId
     * @param receiverId
     * @param msgType
     * @param msg
     */
    void sendMessage(String userId, String receiverId, int msgType, String msg);

    /**
     * 接收消息
     *
     * @param nettyMessage
     */
    void receiveMessage(NettyMessage nettyMessage);

    /**
     * 断开连接的信息
     *
     * @param ctx
     */
    void endMessage(ChannelHandlerContext ctx);
}
