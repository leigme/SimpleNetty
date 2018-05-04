package me.leig.simplenetty.handler;

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

}
