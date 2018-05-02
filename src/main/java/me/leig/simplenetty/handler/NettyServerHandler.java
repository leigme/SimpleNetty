package me.leig.simplenetty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.leig.simplenetty.bean.CtxData;
import me.leig.simplenetty.bean.NettyMessage;
import me.leig.simplenetty.comm.Constant;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 服务器消息处理类
 *
 * Created by i on 2017/10/12.
 */

public class NettyServerHandler extends SimpleChannelInboundHandler<NettyMessage> {

    private final static Logger log = Logger.getLogger(NettyServerHandler.class);

    private ServerListener mServerListener;

    /**
     * 构建服务器对象
     *
     * @param serverListener
     */
    public NettyServerHandler(ServerListener serverListener) {
        this.mServerListener = serverListener;
    }

    /**
     * 初始化通道
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        NettyMessage nm = new NettyMessage();
        nm.setSenderId(mServerListener.getCtxData().getUserId());
        nm.setMsgType(Constant.MSG_TYPE_FIRST);
        String sb = mServerListener.getCtxData().getLocalIP() + Constant.SEG + mServerListener.getCtxData().getPort();
        nm.setData(sb.getBytes());
        ctx.channel().writeAndFlush(nm);
        log.info("与客户端建立通道");
    }

    /**
     * 断开通道
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        mServerListener.endMessage(ctx);
        log.info("客户端离线了...");
    }

    /**
     * 处理消息
     *
     * @param channelHandlerContext
     * @param nettyMessage
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, NettyMessage nettyMessage) throws Exception {
        if (null != nettyMessage) {
            switch (nettyMessage.getMsgType()) {
                case Constant.MSG_TYPE_FIRST:
                    CtxData ctxData = new CtxData();
                    ctxData.setUserId(nettyMessage.getSenderId());
                    String msg = new String(nettyMessage.getData());
                    ctxData.setLocalIP(msg);
                    ctxData.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    ctxData.setCtx(channelHandlerContext);
                    mServerListener.saveCtxData(ctxData);
                    break;
                case Constant.MSG_TYPE_HEARTBEAT:
                    NettyMessage heartbeatMessage = new NettyMessage();
                    heartbeatMessage.setMsgType(Constant.MSG_TYPE_HEARTBEAT);
                    heartbeatMessage.setData("@@".getBytes());
                    channelHandlerContext.channel().writeAndFlush(heartbeatMessage);
                    break;
                default:
                    mServerListener.receiveMessage(nettyMessage);
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
}
