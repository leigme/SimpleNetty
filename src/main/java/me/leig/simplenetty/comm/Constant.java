package me.leig.simplenetty.comm;

/**
 * @author leig
 */

public interface Constant {

    /**
     * int类型占用长度
     */
    int INT_SIZE = 4;

    /*消息类型*/
    /**
     * 首次连接
     */
    int MSG_TYPE_FIRST = 1;

    /**
     * 客户端离线
     */
    int MSG_TYPE_OFFLINE = 4;

    /**
     * 心跳类型
     */
    int MSG_TYPE_HEARTBEAT = 11;

    /**
     * 消息回复
     */
    int MSG_TYPE_REPLY = 12;

    /**
     * 注册用户
     */
    int MSG_TYPE_ADDUSER = 101;

    /**
     * 移除用户
     */
    int MSG_TYPE_REMOVEUSER = 102;

    /**
     * 用户列表
     */
    int MSG_TYPE_USERLIST = 103;

    /**
     * 文字消息
     */
    int MSG_TYPE_TEXT = 1001;
    /**
     * 图片消息
     */
    int MSG_TYPE_IMAGE = 1002;
    /**
     * 视频消息
     */
    int MSG_TYPE_VIDEO = 1003;

    /*消息内容类型*/
    /**
     * 消息接收人
     */
    String RECEIVE_ALL = "ALL";

    String RECEIVE_LIST = "LIST";

    /**
     * 等待时间
     */
    int WAITTIME = 5000;

    /**
     * 连接状态 成功
     */
    String STATUE_OK = "success";

    /**
     * 连接状态 失败
     */
    String STATUE_NG= "failure";

    /**
     * 执行结果 正常类型
     */
    String RES_OK = "RES_OK";

    /**
     * 执行结果 异常类型
     */
    String RES_NG = "RES_NG";

    /**
     * 消息内容连接符
     */
    String CONN = "&";

    String SEG = "@";
}
