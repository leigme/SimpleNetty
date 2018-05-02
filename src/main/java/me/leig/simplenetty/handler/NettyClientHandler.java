package me.leig.simplenetty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import me.leig.simplenetty.bean.CtxData;
import me.leig.simplenetty.bean.NettyMessage;
import me.leig.simplenetty.comm.Constant;
import me.leig.simplenetty.comm.NettyException;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 客户端消息处理类
 *
 * Created by i on 2017/10/12.
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<NettyMessage> {

    private final static Logger log = Logger.getLogger(NettyClientHandler.class);

    // 客户端消息监听抽象类
    private ClientListener mClientListener;

    /**
     * 初始化消息业务对象
     *
     * @param mClientListener
     */
    public NettyClientHandler(ClientListener mClientListener) {
        this.mClientListener = mClientListener;
    }

    /**
     * 建立通道发送一条初始化信息让对方获取管道信息
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        NettyMessage nm = new NettyMessage();
        nm.setSenderId(mClientListener.getCtxData().getUserId());
        nm.setMsgType(Constant.MSG_TYPE_FIRST);
        String msg = mClientListener.getCtxData().getLocalIP();
        nm.setData(msg.getBytes());
        ctx.channel().writeAndFlush(nm);
        log.info("与服务器端建立通道");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        mClientListener.endMessage(ctx);
    }

    /**
     * 读取消息
     *
     * @param ctx
     * @param nettyMessage
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage nettyMessage) throws Exception {
        if (null != mClientListener) {
            switch (nettyMessage.getMsgType()) {
                case Constant.MSG_TYPE_FIRST:
                    CtxData ctxData = new CtxData();
                    ctxData.setUserId(nettyMessage.getSenderId());
                    String[] msgData = new String(nettyMessage.getData()).split
                            (Constant.SEG);
                    if (2 < msgData.length) {
                        throw new NettyException(this.getClass(), "初始化消息格式错误!!!");
                    }
                    ctxData.setLocalIP(msgData[0]);
                    ctxData.setPort(Integer.parseInt(msgData[1]));
                    ctxData.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    ctxData.setCtx(ctx);
                    mClientListener.saveCtxData(ctxData);
                    mClientListener.obtainUsers();
                    break;
                default:
                    mClientListener.receiveMessage(nettyMessage);
                    break;
            }
        }
    }

    /**
     * 捕获异常
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.error(cause.getMessage());
    }

    /**
     * 客户端向服务器端发送状态请求
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 读取处理
     *
     * @param ctx
     */
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        log.info("---READER_IDLE---");
        log.info("---client " + ctx.channel().remoteAddress().toString() + " reader timeout, close it---");
        // 客户端连接超时, 关闭与客户端的连接
//        ctx.close();
    }

    /**
     * 发送了一条记录
     *
     * @param ctx
     */
    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        log.info("---WRITER_IDLE---");
        NettyMessage nm = new NettyMessage();
        nm.setMsgType(Constant.MSG_TYPE_HEARTBEAT);
        nm.setData("@@".getBytes());
        ctx.channel().writeAndFlush(nm);
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        log.info("---ALL_IDLE---");
    }
}
