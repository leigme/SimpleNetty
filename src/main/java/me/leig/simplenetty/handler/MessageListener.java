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
     * @param nettyMessage
     *
     */
    void sendMessage(NettyMessage nettyMessage);

    /**
     * 接收消息
     *
     * @param senderId
     * @param message
     */
    void receiveMessage(int senderId, String message);

    /**
     * 连接成功
     *
     */
    void connectSuccess();

    /**
     * 连接失败
     *
     */
    void connectFailure(NettyMessage nettyMessage);

    /**
     * 断开连接的信息
     *
     * @param ctx
     */
    void disconnect(ChannelHandlerContext ctx);
}
