package me.leig.simplenetty.bean;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author leig
 */

public class CtxData {

    // 连接用户编号
    private String userId;
    // 本地地址
    private String localIP;
    // 远端地址
    private String remoteIP;
    // 连接端口
    private int port = 9009;
    // 连接时间
    private String time;
    // 连接备注
    private String remark;
    // 持有的管道
    private ChannelHandlerContext ctx;

    public CtxData() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
