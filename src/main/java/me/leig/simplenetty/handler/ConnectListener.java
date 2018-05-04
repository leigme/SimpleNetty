package me.leig.simplenetty.handler;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author leig
 */

public interface ConnectListener {

    /**
     * 连接成功
     *
     */
    void connectSuccess();

    /**
     * 连接失败
     *
     */
    void connectFailure();

    /**
     * 断开连接的信息
     *
     */
    void disconnect(ChannelHandlerContext ctx);

}
