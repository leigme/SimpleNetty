package me.leig.simplenetty.handler;

import java.util.List;

/**
 * @author leig
 */

public interface ContentHandler {

    /**
     * 获取服务器上注册用户集合
     *
     * @param userIds
     */
    void obtainUsers(List<String> userIds);
    /**
     * 获取消息内容
     *
     * @param content
     */
    void receiveContent(String content);

    /**
     * 断线消息
     *
     * @param msg
     */
    void endConnect(String msg);

    /**
     * 服务器获取新接入的用户
     *
     * @param userId
     */
    void addUser(String userId);

    /**
     * 服务器获取消息回复
     *
     * @param userId
     */
    void replay(String userId);
}
