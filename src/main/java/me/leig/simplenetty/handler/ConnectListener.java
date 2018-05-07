package me.leig.simplenetty.handler;

import io.netty.channel.ChannelHandlerContext;
import me.leig.simplenetty.bean.CtxData;

import java.util.List;

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
     * @param ctx
     */
    void disconnect(ChannelHandlerContext ctx);

    /**
     * 获取用户列表
     *
     * @param ctxDataList
     */
    void getUserList(List<CtxData> ctxDataList);

    /**
     * 注册用户
     *
     * @param ctxData
     */
    void addUserInfo(CtxData ctxData);

    /**
     * 移除用户
     *
     * @param ctxData
     */
    void removeUserInfo(CtxData ctxData);
}
